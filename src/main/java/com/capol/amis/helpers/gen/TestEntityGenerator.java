package com.capol.amis.helpers.gen;

import com.capol.amis.entity.bo.DatasetRightUnionBO;
import com.capol.amis.entity.bo.DatasetTableBasicBO;
import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.entity.bo.DatasetUnionFieldBO;
import com.capol.amis.enums.TableRelationTypeEnum;
import com.capol.amis.utils.SnowflakeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/18 09:31
 * desc: 测试类实体生成
 */
@Component
public class TestEntityGenerator {

    @Autowired
    private SnowflakeUtil snowflakeUtil;

    public DatasetUnionBO getDatasetUnion() {
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

    public List<Map<String, Object>> getMapList(int n) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", String.valueOf(snowflakeUtil.nextId()));
            map.put("age", String.valueOf(i + ThreadLocalRandom.current().nextInt(30) + 20));
            map.put("creator", "zhangsan");
            map.put("creator_id", String.valueOf(100000000+ThreadLocalRandom.current().nextInt(20000) + 20));
            list.add(map);
        }
        return list;
    }
}
