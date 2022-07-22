package com.capol.amis.template;

import com.capol.amis.AmisApplicationTests;
import com.capol.amis.helpers.gen.TestEntityGenerator;
import com.capol.amis.utils.TaskRunnerUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 15:52
 */
@Slf4j
public class TemplateTests extends AmisApplicationTests {
    @Autowired
    @Qualifier("amisDemoJdbcTemplate")
    private JdbcTemplate amisDemoJdbcTemplate;

    @Autowired
    @Qualifier("reportTemplate")
    private JdbcTemplate reportTemplate;

    @Autowired
    private TestEntityGenerator testEntityGenerator;

    @Test
    public void testAmisTemplate() {
        String sql = "select * from cfg_business_subject_324225698253233659";
        List<Map<String, Object>> result = amisDemoJdbcTemplate.query(sql, new ColumnMapRowMapper());
        result.forEach(System.out::println);
    }

    @Test
    public void testReportTemplate() {
        String sql = "insert into user_test(id, age, creator, creator_id) values (?, ?, ?, ?)";
        StopWatch sw = new StopWatch();

        List<Map<String, Object>> mapList1 = testEntityGenerator.getMapList(2000);
        List<Object[]> listObjs1 = listMap2ListObjs(testEntityGenerator.getMapList(2000));
        List<Map<String, Object>> mapList2 = testEntityGenerator.getMapList(2000);
        List<Object[]> listObjs2 = listMap2ListObjs(testEntityGenerator.getMapList(2000));
        List<Map<String, Object>> mapList3 = testEntityGenerator.getMapList(2000);
        List<Object[]> listObjs3 = listMap2ListObjs(testEntityGenerator.getMapList(2000));

        TaskRunnerUtils.recordTask(sw, "task01", () -> insertCkExecBps(sql, mapList1));
        // TaskRunnerUtils.recordTask(sw, "task11", () -> insertCkExecList(sql, listObjs1));
        TaskRunnerUtils.recordTask(sw, "task02", () -> insertCkExecBps(sql, mapList2));
        // TaskRunnerUtils.recordTask(sw, "task12", () -> insertCkExecList(sql, listObjs2));
        TaskRunnerUtils.recordTask(sw, "task03", () -> insertCkExecBps(sql, mapList3));
        // TaskRunnerUtils.recordTask(sw, "task13", () -> insertCkExecList(sql, listObjs3));

        TaskRunnerUtils.printStopWatch(sw);
    }

    private List<Object[]> listMap2ListObjs(List<Map<String, Object>> mapList) {
        List<Object[]> result = new ArrayList<>();
        mapList.forEach(map -> {
            Object[] obj = new Object[map.size()];
            obj[0] = map.get("id");
            obj[1] = map.get("age");
            obj[2] = map.get("creator1");
            obj[3] = map.get("creator_id");
            result.add(obj);
        });
        return result;
    }

    private void insertCkExecBps(String sql, List<Map<String, Object>> mapList) {
        reportTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, Long.parseLong((String) mapList.get(i).get("id")));
                ps.setInt(2, Integer.parseInt((String) mapList.get(i).get("age")));
                // 模拟取null过程
                Object creator = mapList.get(i).get("creator1");
                if (creator == null) {
                    ps.setNull(3, Types.NULL);
                } else {
                    ps.setString(3, creator.toString());
                }
                //ps.setString(3, (String) mapList.get(i).get("creator"));
                ps.setLong(4, Long.parseLong((String) mapList.get(i).get("creator_id")));
            }
            @Override
            public int getBatchSize() {
                return mapList.size();
            }
        });
    }

    private void insertCkExecList(String sql, List<Object[]> listObjs) {
        reportTemplate.batchUpdate(sql, listObjs);
    }


}
