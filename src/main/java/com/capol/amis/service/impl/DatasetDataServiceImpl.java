package com.capol.amis.service.impl;

import com.capol.amis.entity.DatasetUnionDO;
import com.capol.amis.entity.bo.*;
import com.capol.amis.enums.TableFieldTypeEnum;
import com.capol.amis.enums.TableRelationTypeEnum;
import com.capol.amis.service.*;
import com.capol.amis.utils.CapolListUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
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
    private ITemplateFormDataService templateFormDataService;

    @Autowired
    private ITemplateGridDataService templateGridDataService;

    @Autowired
    private IDatasetUnionService datasetUnionService;

    @Autowired
    private IDatasetFieldService datasetFieldService;

    @Autowired
    private IAmisFormConfigSevice amisFormConfigSevice;

    @Autowired
    @Qualifier(value = "datasetServiceExecutor")
    private Executor datasetServiceExecutor;

    @Override
    public List<Map<Long, Object>> getUnionJoinDatas(DatasetUnionBO datasetUnion) {
        // 依次获取关联
        DatasetTableBasicBO leftTable = datasetUnion.getLeftTable();
        Long leftTableId = leftTable.getTableId();
        List<DatasetRightUnionBO> rightUnions = datasetUnion.getRightUnions();
        // 查询字段
        Map<Long, Set<Long>> queryFieldsMap = datasetUnion.getDatasetQueryFields();

        // ============================================ 获取左表详细信息 ============================================
        // map(rowId, map(fieldName, data))
        // 左表是主表数据
        Map<Long, Map<Long, Optional<TemplateDataBO>>> leftData = templateFormDataService.queryClassifiedFormDataByTableId(leftTableId);

        if (MapUtils.isEmpty(leftData)) {
            return new ArrayList<>();
        }

        Set<Long> leftQueryFields = queryFieldsMap.get(leftTableId);
        Map<Long, Map<Long, Object>> leftQueryDatas = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> cfs = new CopyOnWriteArrayList<>();

        // ============================================ 获取左侧数据数据 ============================================
        cfs.add(CompletableFuture.runAsync(() -> {
            getLeftDatas(leftData, leftQueryFields, leftQueryDatas);
        }, datasetServiceExecutor));
        //getLeftDatas(leftData, leftQueryFields, leftQueryDatas);
        //System.out.println(leftQueryDatas);

        // ============================================ 获取连接数据 ============================================
        // 可能有多个连接 -> list(map(左表rowid, list(map(右表字段, 右表数据))))
        List<Map<Long, List<Map<Long, Object>>>> unionedDataList = new CopyOnWriteArrayList<>();

        // 并行获取右侧连接数据
        List<CompletableFuture<Void>> unionCfs = rightUnions
                .stream()
                .map(union -> CompletableFuture.runAsync(() -> {
                    DatasetTableBasicBO rightTable = union.getRightTable();
                    TableRelationTypeEnum rightTableType = rightTable.getTableType();
                    List<DatasetUnionFieldBO> unionFields = union.getUnionFields();
                    // 左右表连接字段Id, 目前只有一个字段, 后续可拓展
                    Map<Long, Long> unionMap = new HashMap<>(1);
                    unionFields.forEach(unionField -> {
                        Long leftFieldId = unionField.getLeftTemplateId();
                        Long rightFieldId = unionField.getRightTemplateId();
                        unionMap.put(leftFieldId, rightFieldId);
                    });
                    Set<Long> leftFields = unionMap.keySet();
                    // 右表名
                    Long rightTableId = rightTable.getTableId();
                    // 右表查询字段
                    Set<Long> rightQueryFields = queryFieldsMap.get(rightTableId);
                    // 右表数据
                    // TODO zyx 表格类型
                    // Map<Long, Map<String, Optional<TemplateFormDataDO>>> rightData = templateFormDataService.queryClassifiedFromDataByTableId(rightTableId);
                    Map<Long, Map<Long, Optional<TemplateDataBO>>> rightData;
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

    /**
     * 拼接主表与关联数据
     */
    private List<Map<Long, Object>> joinUnionData(Map<Long, Map<Long, Object>> leftQueryDatas,
                                                  List<Map<Long, List<Map<Long, Object>>>> unionedDataList) {
        List<Map<Long, Object>> resultListMap = new ArrayList<>();
        leftQueryDatas.forEach((leftRowId, leftDataMap) -> {
            // 获取右侧的数据
            List<List<Map<Long, Object>>> listOfListMap = new ArrayList<>();
            for (Map<Long, List<Map<Long, Object>>> unionedDatas : unionedDataList) {
                List<Map<Long, Object>> unionedData = unionedDatas.get(leftRowId);
                if (unionedData != null) {
                    listOfListMap.add(unionedData);
                }
            }
            List<Map<Long, Object>> rightMapList = null;
            if (CollectionUtils.isNotEmpty(listOfListMap)) {
                rightMapList = CapolListUtil.buildCartesianListMap(listOfListMap);
            }
            if (CollectionUtils.isNotEmpty(rightMapList)) { // 有右侧数据匹配
                for (Map<Long, Object> rightMap : rightMapList) {
                    // 不要直接操作 rightMap, 否则会
                    Map<Long, Object> rightMapData = new HashMap<>(rightMap);
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
     *
     * @param leftData         左表数据
     * @param rightData        右表数据
     * @param leftFields       左表字段
     * @param unionMap         关联字段
     * @param rightQueryFields 右表查询字段
     * @return 关联后的数据 map(左表id, list(右表关联数据))
     */
    private Map<Long, List<Map<Long, Object>>> getUnionDatas(Map<Long, Map<Long, Optional<TemplateDataBO>>> leftData,
                                                             Map<Long, Map<Long, Optional<TemplateDataBO>>> rightData,
                                                             Set<Long> leftFields,
                                                             Map<Long, Long> unionMap,
                                                             Set<Long> rightQueryFields) {
        int unionSize = unionMap.size();
        // 左表的同一个rowId可能会匹配多条右表数据
        // map(左表rowId, list(map(右表字段id, 右表数据)))
        Map<Long, List<Map<Long, Object>>> unionDatas = new HashMap<>();
        leftData.forEach((leftRowId, leftFieldDataMap) -> rightData.forEach((rightRowId, rightFieldDataMap) -> {
            int matched = 0;
            for (Long leftField : leftFields) {
                Long rightField = unionMap.get(leftField);
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
                List<Map<Long, Object>> dataList = unionDatas.get(leftRowId);
                if (dataList == null) {
                    dataList = new ArrayList<>();
                }
                Map<Long, Object> dataMap = new HashMap<>();
                for (Long rightQueryField : rightQueryFields) {
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
    private void getLeftDatas(Map<Long, Map<Long, Optional<TemplateDataBO>>> leftData,
                              Set<Long> leftQueryFields,
                              Map<Long, Map<Long, Object>> leftQueryDatas) {
        leftData.forEach((rowId, leftFieldDataMap) -> {
            Map<Long, Object> leftQueryDataMap = new HashMap<>();
            for (Long leftQueryField : leftQueryFields) {
                Optional<TemplateDataBO> leftFormData = leftFieldDataMap.get(leftQueryField);
                if (!Objects.isNull(leftFormData)) {
                    leftFormData.ifPresent(formDataDO -> leftQueryDataMap.put(leftQueryField, formDataDO.getFieldTextValue()));
                } else {
                    log.error("数据表中{}行id为{}的字段不存在,请检查参数正确性", rowId, leftQueryField);
                }
            }
            leftQueryDatas.put(rowId, leftQueryDataMap);
        });
    }

    /**
     * 获取所有表格连接字段
     */
    @Override
    public List<DatasetUnionBO> getUnionFields() {
        // 获取查询字段
        Map<Long, Map<Long, Set<Long>>> allQueryFields = datasetFieldService.getQueryFields();
        // 表ID与表类型映射
        Map<Long, TableRelationTypeEnum> tableTypeMap = amisFormConfigSevice.getTableRelationType();
        // 一个数据集对应一个左表和若干个右表
        Map<Long, List<DatasetUnionDO>> allUnionMap = datasetUnionService.getAllUnionMap();

        List<DatasetUnionBO> datasetUnions = new ArrayList<>();

        allUnionMap.forEach((datasetId, unionsByDataset) -> {
            // 数据集关联关系的粒度是以数据集计算的
            DatasetUnionBO datasetUnion = new DatasetUnionBO();
            // 一个数据集只有一张左表, 而且左表信息是相同的
            DatasetUnionDO linfo = unionsByDataset.get(0);
            Long leftId = linfo.getSourceTableId();
            // 获取左表信息
            DatasetTableBasicBO leftTable = new DatasetTableBasicBO()
                    .setTableId(leftId)
                    .setTableName(linfo.getSourceTableName())
                    .setTableType(tableTypeMap.get(leftId))
                    // TODO zyx 左表没有别名
                    .setTableAliasName(null);
            List<DatasetRightUnionBO> rightUnions = new ArrayList<>();
            // 右表关联关系拼接
            unionsByDataset.stream()
                    .collect(Collectors.groupingBy(DatasetUnionDO::getTargetTableId))
                    .forEach((rightId, unionList) -> {
                        DatasetRightUnionBO rightUnion = new DatasetRightUnionBO();
                        // 右表id相同时,信息应该也是相同的
                        DatasetUnionDO rinfo = unionList.get(0);
                        DatasetTableBasicBO rightTable = new DatasetTableBasicBO()
                                .setTableId(rightId)
                                .setTableName(rinfo.getTargetTableName())
                                .setTableType(tableTypeMap.get(rightId))
                                .setTableAliasName(rinfo.getTargetTableNameAlias());
                        // 关联字段
                        List<DatasetUnionFieldBO> rightUnionFields = new ArrayList<>();
                        unionList.forEach(union -> {
                            DatasetUnionFieldBO rightUnionFiled = new DatasetUnionFieldBO()
                                    .setLeftTemplateId(union.getSourceFieldId())
                                    .setRightTemplateId(union.getSourceFieldId());
                            rightUnionFields.add(rightUnionFiled);
                        });
                        // 设置参数
                        rightUnion.setRightTable(rightTable).setUnionFields(rightUnionFields);
                        rightUnions.add(rightUnion);
                    });
            datasetUnion.setLeftTable(leftTable)
                    // TODO zyx 设置字段值
                    .setRightUnions(rightUnions)
                    .setDatasetQueryFields(allQueryFields.get(datasetId));
            datasetUnions.add(datasetUnion);
        });
        return datasetUnions;
    }
}
