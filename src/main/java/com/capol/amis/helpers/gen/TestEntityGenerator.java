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

        List<DatasetUnionFieldBO> unionFields = new ArrayList<>();
        DatasetUnionFieldBO unionField = new DatasetUnionFieldBO()
                .setLeftFieldName("creator_id").setRightFieldName("creator_id")
                .setLeftTemplateId(328434439350648847L).setRightTemplateId(328465748643020806L);

        unionFields.add(unionField);
        DatasetRightUnionBO rightUnion = new DatasetRightUnionBO().setRightTable(rightTable).setUnionFields(unionFields);

        unionList.add(rightUnion);
        // 查询字段
        Map<Long, Set<Long>> queryFiledsMap = new HashMap<>();
        Set<Long> leftQueryFields = Stream.of(
                        328434439350648865L,
                        328434439350648867L,
                        328434439350648861L,
                        328434439350648863L
                )
                .collect(Collectors.toSet());
        queryFiledsMap.put(328434438381764608L, leftQueryFields);
        Set<Long> rightQueryFields = Stream.of(
                        328465748643020828L,
                        328465748643020826L,
                        328465748643020824L)
                .collect(Collectors.toSet());
        queryFiledsMap.put(328465748152287232L, rightQueryFields);
        // 查询信息
        return new DatasetUnionBO().setLeftTable(leftTable).setRightUnions(unionList).setDatasetQueryFields(queryFiledsMap);
    }
}
