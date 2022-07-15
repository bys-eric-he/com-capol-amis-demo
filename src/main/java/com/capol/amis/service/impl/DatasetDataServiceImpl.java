package com.capol.amis.service.impl;

import com.capol.amis.entity.bo.*;
import com.capol.amis.enums.TableFieldTypeEnum;
import com.capol.amis.enums.TableRelationTypeEnum;
import com.capol.amis.mapper.TemplateFormDataMapper;
import com.capol.amis.service.IDatasetDataService;
import com.capol.amis.service.ITemplateFormDataService;
import com.capol.amis.service.ITemplateGridDataService;
import com.capol.amis.utils.CapolListUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 17:28
 * desc: 数据集数据查询服务
 */
@Slf4j
@Service
public class DatasetDataServiceImpl implements IDatasetDataService {

    @Autowired
    private TemplateFormDataMapper templateFormDataMapper;

    @Autowired
    private ITemplateFormDataService templateFormDataService;

    @Autowired
    private ITemplateGridDataService templateGridDataService;

    @Autowired
    @Qualifier(value = "datasetServiceExecutor")
    private Executor datasetServiceExecutor;

    @Override
    public List<Map<String, Object>> getUnionJoinDatas(DatasetUnionBO datasetUnion) {
        // 依次获取关联
        DatasetTableBasicBO leftTable = datasetUnion.getLeftTable();
        Long leftTableId = leftTable.getTableId();
        List<DatasetRightUnionBO> rightUnions = datasetUnion.getRightUnions();
        // 查询字段
        Map<Long, Set<String>> queryFieldsMap = datasetUnion.getDatasetQueryFields();

        // ============================================ 获取左表详细信息 ============================================
        // map(rowId, map(fieldName, data))
        // 左表是主表数据
        Map<Long, Map<String, Optional<TemplateDataBO>>> leftData = templateFormDataService.queryClassifiedFormDataByTableId(leftTableId);

        if (MapUtils.isEmpty(leftData)) {
            return new ArrayList<>();
        }

        Set<String> leftQueryFields = queryFieldsMap.get(leftTableId);
        Map<Long, Map<String, Object>> leftQueryDatas = new ConcurrentHashMap<>();


        List<CompletableFuture<Void>> cfs = new CopyOnWriteArrayList<>();

        // ============================================ 获取左侧数据数据 ============================================
        cfs.add(CompletableFuture.runAsync(() -> {
            getLeftDatas(leftData, leftQueryFields, leftQueryDatas);
        }, datasetServiceExecutor));
        //getLeftDatas(leftData, leftQueryFields, leftQueryDatas);
        //System.out.println(leftQueryDatas);

        // ============================================ 获取连接数据 ============================================
        // 可能有多个连接 -> list(map(左表rowid, list(map(右表字段, 右表数据))))
        List<Map<Long, List<Map<String, Object>>>> unionedDataList = new CopyOnWriteArrayList<>();

        // 并行获取右侧连接数据
        List<CompletableFuture<Void>> unionCfs = rightUnions
                .stream()
                .map(union -> CompletableFuture.runAsync(() -> {
                    DatasetTableBasicBO rightTable = union.getRightTable();
                    TableRelationTypeEnum rightTableType = rightTable.getTableType();
                    Set<DatasetUnionFieldBO> unionFields = union.getUnionFields();
                    // 左右表连接字段, 目前只有一个字段, 后续可拓展
                    Map<String, String> unionMap = new HashMap<>(1);
                    unionFields.forEach(unionField -> {
                        String leftFieldName = unionField.getLeftFieldName();
                        String rightFieldName = unionField.getRightFieldName();
                        unionMap.put(leftFieldName, rightFieldName);
                    });
                    Set<String> leftFields = unionMap.keySet();
                    // 右表名
                    Long rightTableId = rightTable.getTableId();
                    // 右表查询字段
                    Set<String> rightQueryFields = queryFieldsMap.get(rightTableId);
                    // 右表数据
                    // TODO zyx 表格类型
                    // Map<Long, Map<String, Optional<TemplateFormDataDO>>> rightData = templateFormDataService.queryClassifiedFromDataByTableId(rightTableId);
                    Map<Long, Map<String, Optional<TemplateDataBO>>> rightData;
                    switch (rightTableType) {
                        case MAIN_TYPE:
                        default:
                            rightData = templateFormDataService.queryClassifiedFormDataByTableId(rightTableId);
                            break;
                        case SUB_TYPE:
                            rightData = templateGridDataService.queryClassifiedGridDataByTableId(rightTableId);
                            break;
                    }
                    // 获取连接数据
                    unionedDataList.add(getUnionDatas(leftData, rightData, leftFields, unionMap, rightQueryFields));
                }, datasetServiceExecutor))
                .collect(Collectors.toList());
        cfs.addAll(unionCfs);

        // ============================================ 拼接左右数据 ============================================
        CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0])).join();
        return joinUnionData(leftQueryDatas, unionedDataList);
    }

    @Override
    public List<Map<String, Object>> getUnionJoinDatas2(DatasetUnionBO datasetUnion) {
        // 依次获取关联
        DatasetTableBasicBO leftTable = datasetUnion.getLeftTable();
        Long leftTableId = leftTable.getTableId();
        List<DatasetRightUnionBO> rightUnions = datasetUnion.getRightUnions();
        // 查询字段
        Map<Long, Set<String>> queryFieldsMap = datasetUnion.getDatasetQueryFields();

        // ============================================ 获取左表详细信息 ============================================
        // map(rowId, map(fieldName, data))
        // 左表是主表数据
        Map<Long, Map<String, Optional<TemplateDataBO>>> leftData = templateFormDataService.queryClassifiedFormDataByTableId(leftTableId);

        if (MapUtils.isEmpty(leftData)) {
            return new ArrayList<>();
        }

        Set<String> leftQueryFields = queryFieldsMap.get(leftTableId);
        Map<Long, Map<String, Object>> leftQueryDatas = new ConcurrentHashMap<>();


        List<CompletableFuture<Void>> cfs = new CopyOnWriteArrayList<>();

        // ============================================ 获取左侧数据数据 ============================================
        getLeftDatas(leftData, leftQueryFields, leftQueryDatas);
        //getLeftDatas(leftData, leftQueryFields, leftQueryDatas);
        //System.out.println(leftQueryDatas);

        // ============================================ 获取连接数据 ============================================
        // 可能有多个连接 -> list(map(左表rowid, list(map(右表字段, 右表数据))))
        List<Map<Long, List<Map<String, Object>>>> unionedDataList = new CopyOnWriteArrayList<>();

        // 并行获取右侧连接数据
        rightUnions
                .forEach(union ->  {
                    DatasetTableBasicBO rightTable = union.getRightTable();
                    TableRelationTypeEnum rightTableType = rightTable.getTableType();
                    Set<DatasetUnionFieldBO> unionFields = union.getUnionFields();
                    // 左右表连接字段, 目前只有一个字段, 后续可拓展
                    Map<String, String> unionMap = new HashMap<>(1);
                    unionFields.forEach(unionField -> {
                        String leftFieldName = unionField.getLeftFieldName();
                        String rightFieldName = unionField.getRightFieldName();
                        unionMap.put(leftFieldName, rightFieldName);
                    });
                    Set<String> leftFields = unionMap.keySet();
                    // 右表名
                    Long rightTableId = rightTable.getTableId();
                    // 右表查询字段
                    Set<String> rightQueryFields = queryFieldsMap.get(rightTableId);
                    // 右表数据
                    // TODO zyx 表格类型
                    // Map<Long, Map<String, Optional<TemplateFormDataDO>>> rightData = templateFormDataService.queryClassifiedFromDataByTableId(rightTableId);
                    Map<Long, Map<String, Optional<TemplateDataBO>>> rightData;
                    switch (rightTableType) {
                        case MAIN_TYPE:
                        default:
                            rightData = templateFormDataService.queryClassifiedFormDataByTableId(rightTableId);
                            break;
                        case SUB_TYPE:
                            rightData = templateGridDataService.queryClassifiedGridDataByTableId(rightTableId);
                            break;
                    }
                    // 获取连接数据
                    unionedDataList.add(getUnionDatas(leftData, rightData, leftFields, unionMap, rightQueryFields));
                });

        // ============================================ 拼接左右数据 ============================================
        return joinUnionData(leftQueryDatas, unionedDataList);
    }

    /**
     * 拼接主表与关联数据
     */
    private List<Map<String, Object>> joinUnionData(Map<Long, Map<String, Object>> leftQueryDatas,
                                                    List<Map<Long, List<Map<String, Object>>>> unionedDataList) {
        List<Map<String, Object>> resultListMap = new ArrayList<>();
        leftQueryDatas.forEach((leftRowId, leftDataMap) -> {
            // 获取右侧的数据
            List<List<Map<String, Object>>> listOfListMap = new ArrayList<>();
            for (Map<Long, List<Map<String, Object>>> unionedDatas : unionedDataList) {
                List<Map<String, Object>> unionedData = unionedDatas.get(leftRowId);
                if (unionedData != null) {
                    listOfListMap.add(unionedData);
                }
            }
            List<Map<String, Object>> rightMapList = null;
            if (CollectionUtils.isNotEmpty(listOfListMap)) {
                rightMapList = CapolListUtil.buildCartesianListMap(listOfListMap);
            }
            if (CollectionUtils.isNotEmpty(rightMapList)) { // 有右侧数据匹配
                for (Map<String, Object> rightMap : rightMapList) {
                    // 不要直接操作 rightMap, 否则会
                    Map<String, Object> rightMapData = new HashMap<>(rightMap);
                    rightMapData.putAll(leftDataMap);
                    resultListMap.add(rightMapData);
                }
            } else {
                // 没有右侧数据匹配
                resultListMap.add(leftDataMap);
            }
        });
        return resultListMap;
    }

    /**
     * 获取关联数据
     * @param leftData 左表数据
     * @param rightData 右表数据
     * @param leftFields 左表字段
     * @param unionMap 关联字段
     * @param rightQueryFields 右表查询字段
     * @return 关联后的数据 map(左表id, list(右表关联数据))
     */
    private Map<Long, List<Map<String, Object>>> getUnionDatas(Map<Long, Map<String, Optional<TemplateDataBO>>> leftData,
                                                               Map<Long, Map<String, Optional<TemplateDataBO>>> rightData,
                                                               Set<String> leftFields,
                                                               Map<String, String> unionMap,
                                                               Set<String> rightQueryFields) {
        int unionSize = unionMap.size();
        // 左表的同一个rowId可能会匹配多条右表数据
        // map(左表rowId, list(map(右表字段, 右表数据)))
        Map<Long, List<Map<String, Object>>> unionDatas = new HashMap<>();
        leftData.forEach((leftRowId, leftFieldDataMap) -> rightData.forEach((rightRowId, rightFieldDataMap) -> {
            int matched = 0;
            for (String leftField : leftFields) {
                String rightField = unionMap.get(leftField);
                Optional<TemplateDataBO> leftFieldData = leftFieldDataMap.get(leftField);
                Optional<TemplateDataBO> rightFieldData = rightFieldDataMap.get(rightField);
                if (!Objects.isNull(leftFieldData) && !Objects.isNull(rightFieldData)) {
                    if (leftFieldData.isPresent() && rightFieldData.isPresent()) {
                        // FIXME zyx 小概率发生哈希碰撞
                        if (Objects.equals(leftFieldData.get().getFieldHashValue(), rightFieldData.get().getFieldHashValue())) {
                            matched++;
                        }
                    }
                } else {
                    log.warn("左表{}行{}字段或者右表{}行{}字段不存在,请检查参数正确性", leftRowId, leftField, rightRowId, rightField);
                }
            }
            // 所有匹配字段的hash值相等时, 添加数据
            if (matched == unionSize) {
                List<Map<String, Object>> dataList = unionDatas.get(leftRowId);
                if (dataList == null) {
                    dataList = new ArrayList<>();
                }
                Map<String, Object> dataMap = new HashMap<>();
                for (String rightQueryField : rightQueryFields) {
                    Optional<TemplateDataBO> formData = rightFieldDataMap.get(rightQueryField);
                    // TODO zyx 依据数据类型添加数据
                    formData.ifPresent(formDataDO -> {
                        Object fieldValue;
                        // switch (formDataDO.getFieldType())
                        TableFieldTypeEnum fieldTypeType = TableFieldTypeEnum.getFieldTypeEnumByDesc(formDataDO.getFieldType());
                        switch (fieldTypeType) {
                            case BIGINT:
                            case TINYINT:
                                // fieldValue = formDataDO.getFieldNumberValue();
                                // break;
                            case VARCHAR:
                            case DATETIME:
                            case TEXT:
                            default:
                                fieldValue = formDataDO.getFieldTextValue();
                                break;
                        }
                        dataMap.put(rightQueryField, fieldValue);
                    });
                }
                dataList.add(dataMap);
                unionDatas.put(leftRowId, dataList);
            }
        }));
        return unionDatas;
    }

    /**
     * 获取左表所需字段的数据
     */
    private void getLeftDatas(Map<Long, Map<String, Optional<TemplateDataBO>>> leftData,
                              Set<String> leftQueryFields,
                              Map<Long, Map<String, Object>> leftQueryDatas) {
        leftData.forEach((rowId, leftFieldDataMap) -> {
            Map<String, Object> leftQueryDataMap = new HashMap<>();
            for (String leftQueryField : leftQueryFields) {
                Optional<TemplateDataBO> leftFormData = leftFieldDataMap.get(leftQueryField);
                if (!Objects.isNull(leftFormData)) {
                    leftFormData.ifPresent(formDataDO -> leftQueryDataMap.put(leftQueryField, formDataDO.getFieldTextValue()));
                } else {
                    log.error("数据表中{}行无{}字段字段不存在,请检查参数正确性", rowId, leftQueryField);
                }
            }
            leftQueryDatas.put(rowId, leftQueryDataMap);
        });
    }
}
