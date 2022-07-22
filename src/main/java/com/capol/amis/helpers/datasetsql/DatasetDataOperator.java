package com.capol.amis.helpers.datasetsql;

import com.capol.amis.entity.bo.DatasetFieldBasicBO;
import com.capol.amis.enums.TableFieldTypeEnum;
import com.capol.amis.utils.SnowflakeUtil;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * @author Yaxi.Zhang
 * @desc
 * @since 2022/7/22 12:13
 */
public class DatasetDataOperator extends BatchPsOperator<Map<String, Object>> {

    private final List<DatasetFieldBasicBO> queryList;
    private final SnowflakeUtil snowflakeUtil;

    public DatasetDataOperator(List<Map<String, Object>> items,
                               List<DatasetFieldBasicBO> queryList,
                               JdbcTemplate jdbcTemplate,
                               String sql) {
        super(items, jdbcTemplate, sql);
        this.queryList = queryList;
        this.snowflakeUtil = new SnowflakeUtil();
    }

    @Override
    public void bind(Map<String, Object> item, PreparedStatement ps, int i) throws SQLException {
        int querySize = queryList.size();
        Map<String, Object> itemMap = items.get(i);
        ps.setLong(1, snowflakeUtil.nextId());
        for (int j = 0; j < querySize; j++) {
            DatasetFieldBasicBO query = queryList.get(j);
            TableFieldTypeEnum fieldType = TableFieldTypeEnum.getFieldTypeEnumByDesc(query.getOriFieldType());
            // TODO zyx 依据类型选择插入字段
            switch (fieldType) {
                case TINYINT:
                case BIGINT:
                case VARCHAR:
                case TEXT:
                case DATETIME:
                default:
                    Object obj = itemMap.get(query.getFieldNameAlias());
                    if (itemMap.get(query.getFieldNameAlias()) == null) {
                        // ps 下标从1开始
                        ps.setNull(j + 2, Types.NULL);
                    } else {
                        ps.setString(j + 2, obj.toString());
                    }
                    break;
            }
        }
    }
}
