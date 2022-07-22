package com.capol.amis.helpers.datasetsql;

import lombok.Data;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Yaxi.Zhang
 * @desc 批量插入/更新基类
 * @since 2022/7/21 17:29
 * reference: https://juejin.cn/post/6844904183200481288
 */
@Data
public abstract class BatchPsOperator<T> {

    protected String sql;
    protected JdbcTemplate jdbcTemplate;
    protected List<T> items;

    public BatchPsOperator(List<T> items, JdbcTemplate jdbcTemplate, String sql) {
        this.items = items;
        this.setJdbcTemplate(jdbcTemplate);
        this.sql = sql;
    }
    /**子类重写此方法
     * @param item 需要操作对应表的实体类
     * @param ps   自动注入,无需传入
     */
    public abstract void bind(T item, PreparedStatement ps, int i) throws SQLException;

    public void exec() {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                bind(items.get(i), ps, i);
            }
            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
    }

}
