package com.capol.amis.helpers.gen;

import com.capol.amis.entity.bo.DatasetRightUnionBO;
import com.capol.amis.entity.bo.DatasetTableBasicBO;
import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.entity.bo.DatasetUnionFieldBO;
import com.capol.amis.enums.TableRelationTypeEnum;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/18 09:31
 * desc: 测试类实体生成
 */
public class TestEntityGenerator {

    public static DatasetUnionBO getDatasetUnion() {
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
        return new DatasetUnionBO().setLeftTable(leftTable).setRightUnions(unionList).setDatasetQueryFields(queryFiledsMap);
    }
}
