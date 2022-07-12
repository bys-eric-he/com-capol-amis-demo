package com.capol.amis.template;

import com.capol.amis.AmisApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 15:52
 */
public class TemplateTests extends AmisApplicationTests {
    @Autowired
    @Qualifier("amisDemoJdbcTemplate")
    private JdbcTemplate amisDemoJdbcTemplate;

    @Test
    public void testTemplate() {
        String sql = "select * from cfg_business_subject_324225698253233659";
        List<Map<String, Object>> query = amisDemoJdbcTemplate.query(sql, new ColumnMapRowMapper());
        query.forEach(System.out::println);
    }
}
