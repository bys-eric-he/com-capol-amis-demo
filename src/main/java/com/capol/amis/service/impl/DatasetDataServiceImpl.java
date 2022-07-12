package com.capol.amis.service.impl;

import com.capol.amis.entity.TemplateFormDataDO;
import com.capol.amis.entity.bo.DatasetRightUnionBO;
import com.capol.amis.entity.bo.DatasetTableBasicBO;
import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.entity.bo.DatasetUnionFieldBO;
import com.capol.amis.mapper.TemplateFormDataMapper;
import com.capol.amis.service.IDatasetDataService;
import com.capol.amis.service.ITemplateFormDataService;
import com.capol.amis.utils.CapolListUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
    private TemplateFormDataMapper templateFormDataMapper;

    @Autowired
    private ITemplateFormDataService templateFormDataService;

    @Autowired
    @Qualifier(value = "datasetServiceExecutor")
    private Executor datasetServiceExecutor;

    @Override
    public List<Map<String, Object>> getUnionJoinDatas(DatasetUnionBO datasetUnion) {
        // 依次获取关联
        DatasetTableBasicBO leftTable = datasetUnion.getLeftTable();
        Long leftTableId = leftTable.getTableId();
        Set<DatasetRightUnionBO> rightUnions = datasetUnion.getRightUnions();
        // 查询字段
        Map<Long, Set<String>> queryFieldsMap = datasetUnion.getDatasetQueryFields();

        // ============================================ 获取左表详细信息 ============================================
        // map(rowId, map(fieldName, data))
        Map<Long, Map<String, Optional<TemplateFormDataDO>>> leftData = templateFormDataService.queryClassifiedFromDataByTableId(leftTableId);

        Set<String> leftQueryFields = queryFieldsMap.get(leftTableId);
        Map<Long, Map<String, Object>> leftQueryDatas = new ConcurrentHashMap<>();


        List<CompletableFuture<Void>> cfs = new CopyOnWriteArrayList<>();

        // ============================================ 获取左侧数据数据 ============================================
        cfs.add(CompletableFuture.runAsync(() -> getLeftDatas(leftData, leftQueryFields, leftQueryDatas), datasetServiceExecutor));

        // ============================================ 获取连接数据 ============================================
        // 可能有多个连接 -> list(map(左表rowid, list(map(右表字段, 右表数据))))
        List<Map<Long, List<Map<String, Object>>>> unionedDataList = new CopyOnWriteArrayList<>();

        // 并行获取右侧连接数据
        List<CompletableFuture<Void>> unionCfs = rightUnions
                .stream()
                .map(union -> CompletableFuture.runAsync(() -> {
                    DatasetTableBasicBO rightTable = union.getRightTable();
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
                    Map<Long, Map<String, Optional<TemplateFormDataDO>>> rightData = templateFormDataService.queryClassifiedFromDataByTableId(rightTableId);
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
                    rightMapData.putAll(rightMap);
                    rightMapList.add(rightMapData);
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
     */
    private Map<Long, List<Map<String, Object>>> getUnionDatas(Map<Long, Map<String, Optional<TemplateFormDataDO>>> leftData,
                                                               Map<Long, Map<String, Optional<TemplateFormDataDO>>> rightData,
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
                Optional<TemplateFormDataDO> leftFieldData = leftFieldDataMap.get(leftField);
                Optional<TemplateFormDataDO> rightFieldData = rightFieldDataMap.get(rightField);
                if (leftFieldData.isPresent() && rightFieldData.isPresent()) {
                    if (Objects.equals(leftFieldData.get().getFieldHashValue(), rightFieldData.get().getFieldHashValue())) {
                        matched++;
                    }
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
                    Optional<TemplateFormDataDO> formData = rightFieldDataMap.get(rightQueryField);
                    // TODO zyx 依据数据类型添加数据
                    formData.ifPresent(templateFormDataDO -> dataMap.put(rightQueryField, templateFormDataDO.getFieldStringValue()));
                }
                dataList.add(dataMap);
                unionDatas.put(leftRowId, dataList);
            }
        }));
        return unionDatas;
    }

    /**
     * 获取左表数据
     */
    private void getLeftDatas(Map<Long, Map<String, Optional<TemplateFormDataDO>>> leftData,
                              Set<String> leftQueryFields,
                              Map<Long, Map<String, Object>> leftQueryDatas) {
        leftData.forEach((rowId, leftFieldDataMap) -> {
            Map<String, Object> leftQueryDataMap = new HashMap<>();
            for (String leftQueryField : leftQueryFields) {
                Optional<TemplateFormDataDO> leftFormData = leftFieldDataMap.get(leftQueryField);
                leftFormData.ifPresent(formDataDO -> leftQueryDataMap.put(leftQueryField, formDataDO.getFieldStringValue()));
            }
            leftQueryDatas.put(rowId, leftQueryDataMap);
        });
    }

}
