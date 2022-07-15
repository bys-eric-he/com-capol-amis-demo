package com.capol.amis.service;

import com.alibaba.fastjson.JSONObject;
import com.capol.amis.AmisApplicationTests;
import com.capol.amis.entity.bo.DatasetRightUnionBO;
import com.capol.amis.entity.bo.DatasetTableBasicBO;
import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.entity.bo.DatasetUnionFieldBO;
import com.capol.amis.enums.TableRelationTypeEnum;
import com.capol.amis.testutils.TestEntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/12 18:42
 */
public class DatasetDataServiceTests extends AmisApplicationTests {

    @Autowired
    private IDatasetDataService datasetDataService;

    @Test
    public void testGetUnionJoinDatas() {
        DatasetUnionBO datasetUnionBO = TestEntityUtils.getDatasetUnion();

        StopWatch sw = new StopWatch();
        sw.start("task10");
        datasetDataService.getUnionJoinDatas(datasetUnionBO);
        sw.stop();
        sw.start("task20");
        datasetDataService.getUnionJoinDatas2(datasetUnionBO);
        sw.stop();
        sw.start("task11");
        datasetDataService.getUnionJoinDatas(datasetUnionBO);
        sw.stop();
        sw.start("task21");
        datasetDataService.getUnionJoinDatas2(datasetUnionBO);
        sw.stop();
        sw.start("task12");
        datasetDataService.getUnionJoinDatas(datasetUnionBO);
        sw.stop();
        sw.start("task22");
        datasetDataService.getUnionJoinDatas2(datasetUnionBO);
        sw.stop();

        for (StopWatch.TaskInfo taskInfo : sw.getTaskInfo()) {
            System.out.println("taskInfo.getTaskName() = " + taskInfo.getTaskName());
            System.out.println("taskInfo.getTimeSeconds() = " + taskInfo.getTimeSeconds() + "s");
        }

    }

    @Test
    public void testJSONObject() {
        // 左表
        DatasetTableBasicBO leftTable = new DatasetTableBasicBO()
                .setTableId(328434438381764608L)
                .setTableName("cfg_table_1745662207332353")
                .setTableType(TableRelationTypeEnum.MAIN_TYPE);

        // 右表
        DatasetTableBasicBO rightTable = new DatasetTableBasicBO()
                .setTableId(328465748152287232L)
                .setTableName("cfg_table_1748490574143489")
                .setTableType(TableRelationTypeEnum.MAIN_TYPE);

        // 关联
        List<DatasetRightUnionBO> unionList = new ArrayList<>();

        Set<DatasetUnionFieldBO> unionFields = new HashSet<>();
        DatasetUnionFieldBO unionField = new DatasetUnionFieldBO().setLeftFieldName("creator_id").setRightFieldName("creator_id");
        unionFields.add(unionField);
        DatasetRightUnionBO rightUnion = new DatasetRightUnionBO().setRightTable(rightTable).setUnionFields(unionFields);

        unionList.add(rightUnion);
        // 查询字段
        Map<Long, Set<String>> queryFiledsMap = new HashMap<>();
        Set<String> leftQueryFields = Stream.of(
                        "column_328434439346454530",
                        "column_328434439346454531",
                        "column_328434439346454528",
                        "column_328434439346454529")
                .collect(Collectors.toSet());
        queryFiledsMap.put(328434438381764608L, leftQueryFields);
        Set<String> rightQueryFields = Stream.of(
                        "column_328465748571717636",
                        "column_328465748571717635",
                        "column_328465748571717634")
                .collect(Collectors.toSet());
        queryFiledsMap.put(328465748152287232L, rightQueryFields);
        // 查询信息
        DatasetUnionBO datasetUnionBO = new DatasetUnionBO().setLeftTable(leftTable).setRightUnions(unionList).setDatasetQueryFields(queryFiledsMap);

        String jsonStr = JSONObject.toJSONString(datasetUnionBO);

        DatasetUnionBO parsedObject = JSONObject.parseObject(jsonStr, DatasetUnionBO.class);

        DatasetTableBasicBO leftTable1 = parsedObject.getLeftTable();
        System.out.println(leftTable1.getTableId());
        System.out.println(leftTable1.getTableType());
    }

}
