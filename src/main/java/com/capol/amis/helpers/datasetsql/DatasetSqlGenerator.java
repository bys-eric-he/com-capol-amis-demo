package com.capol.amis.helpers.datasetsql;

import com.capol.amis.entity.bo.DatasetFieldBasicBO;
import com.capol.amis.enums.TableFieldTypeEnum;
import com.capol.amis.helpers.comparator.DatasetFieldBasicComparator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author Yaxi.Zhang
 * @desc 数据集SQL生成器
 * @since 2022/7/7 17:41
 */
@Slf4j
public class DatasetSqlGenerator {
    // 通用字符串
    public static final String DOT = ",";
    public static final String SPACE = " ";
    public static final String SINGLE_QUOTES = "'";
    public static final String BACK_QUOTE = "`";
    public static final String LEFT_BRACKET = "(";
    public static final String RIGHT_BRACKET = ")";
    public static final String LINE_BREAK = "\n";
    public static final String TABS = "\t";
    public static final String EQUAL_SIGN = "=";
    public static final String QUESTION_MARK = "?";
    public static final String IDENTITY = "id";
    public static final String IDENTITY_DESC = "主键id";
    public static final String TABLE_PREFIX = "cfg_table_";
    public static final Integer DEFAULT_GRANULARITY = 8192;

    // DLL操作
    public static final String CREATE_TABLE = "CREATE TABLE";
    public static final String DROP_TABLE = "DROP TABLE";
    public static final String IF_EXISTS = "IF EXISTS";
    public static final String IF_NOT_EXISTS = "IF NOT EXISTS";
    public static final String DEFAULT_NULL = "DEFAULT NULL";
    public static final String COMMENT = "COMMENT";
    public static final String ENGINE = "ENGINE";
    public static final String PARTITION_BY = "PARTITION_BY";
    public static final String ORDER_BY = "ORDER BY";
    public static final String SETTINGS_INDEX_GRANULARITY = "SETTINGS index_granularity";

    // DML操作
    public static final String INSERT_INTO = "INSERT INTO";
    public static final String VALUES = "VALUES";

    // Clickhouse引擎
    public static final String MERGE_TREE = "MergeTree";
    public static final String REPLACING_MERGE_TREE = "ReplacingMergeTree";

    // 数据类型
    public static final String NULLABLE = "Nullable";
    public static final String INT_32_TYPE = "Int32";
    public static final String INT_64_TYPE = "Int64";
    public static final String STRING_TYPE = "String";
    public static final String DATETIME_TYPE = "DateTime";

    /**
     * 生成建表SQL
     */
    public static String genCreateTableSql(String tableName, List<DatasetFieldBasicBO> fields) {
        StringBuilder sb = new StringBuilder();
        // 建表
        sb.append(CREATE_TABLE + SPACE + IF_NOT_EXISTS + SPACE + BACK_QUOTE)
                .append(tableName).append(BACK_QUOTE).append(LINE_BREAK).append(LEFT_BRACKET).append(LINE_BREAK);
        // 表字段
        // id字段
        sb.append(buildIdField());
        if (CollectionUtils.isEmpty(fields)) {
            log.warn("==========>>>>>>>>>> 表{}建表传参的查询字段为空,请检查", tableName);
        } else {
            // 对别名字段进行排序
            fields.sort(new DatasetFieldBasicComparator());
            int fieldSize = fields.size();
            sb.append(DOT).append(LINE_BREAK);
            for (int i = 0; i < fieldSize; i++) {
                sb.append(buildFieldDesc(fields.get(i)));
                if (i != fieldSize - 1) {
                    sb.append(DOT).append(LINE_BREAK);
                }
            }
        }
        // 表描述
        sb.append(LINE_BREAK).append(RIGHT_BRACKET).append(LINE_BREAK)
                .append(TABS).append(ENGINE).append(SPACE).append(EQUAL_SIGN).append(SPACE).append(MERGE_TREE).append(LINE_BREAK)
                .append(TABS).append(TABS).append(ORDER_BY).append(SPACE).append(IDENTITY).append(LINE_BREAK)
                .append(TABS).append(TABS).append(SETTINGS_INDEX_GRANULARITY).append(SPACE).append(EQUAL_SIGN).append(SPACE).append(DEFAULT_GRANULARITY);
        return sb.toString();
    }

    /**
     * 生成插入数据SQL
     */
    public static String genInsertSql(String tableName, List<DatasetFieldBasicBO> fields) {
        StringBuilder sb = new StringBuilder();
        int fieldSize = fields == null ? 0 : fields.size();
        if (fieldSize == 0) {
            log.warn("==========>>>>>>>>>> 表{}插入数据传参的查询字段为空,请检查", tableName);
        } else {
            // 对别名字段进行排序
            fields.sort(new DatasetFieldBasicComparator());
        }
        sb.append(INSERT_INTO).append(SPACE).append(BACK_QUOTE).append(tableName).append(BACK_QUOTE).append(SPACE);
        sb.append(LEFT_BRACKET);
        // 字段
        sb.append(IDENTITY);
        if (fieldSize > 0) {
            for (int i = 0; i < fieldSize; i++) {
                sb.append(DOT).append(SPACE).append(fields.get(i).getFieldNameAlias());
            }
        }
        sb.append(RIGHT_BRACKET).append(SPACE).append(VALUES).append(SPACE).append(LEFT_BRACKET);
        // 占位符
        sb.append(QUESTION_MARK);
        sb.append(genPlaceHolder(fieldSize));
        sb.append(RIGHT_BRACKET);

        return sb.toString();
    }

    /**
     * 生成删除表SQL
     */
    public static String genDropTableSql(String tableName) {
        return DROP_TABLE + SPACE + IF_EXISTS + SPACE + BACK_QUOTE + tableName + BACK_QUOTE;
    }

    private static String genPlaceHolder(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(DOT).append(SPACE).append(QUESTION_MARK);
        }
        return sb.toString();
    }

    private static String buildIdField() {
        return SPACE + BACK_QUOTE + IDENTITY + BACK_QUOTE + SPACE + INT_64_TYPE + SPACE + COMMENT + SPACE + SINGLE_QUOTES + IDENTITY_DESC + SINGLE_QUOTES;
    }

    private static String buildFieldDesc(DatasetFieldBasicBO field) {
        StringBuilder sb = new StringBuilder();
        sb.append(SPACE).append(BACK_QUOTE).append(field.getFieldNameAlias()).append(BACK_QUOTE).append(SPACE);
        TableFieldTypeEnum fieldType = TableFieldTypeEnum.getFieldTypeEnumByDesc(field.getOriFieldType());
        // TODO zyx 依据类型分组
        switch (fieldType) {
            case TINYINT:
            case BIGINT:
            case VARCHAR:
            case TEXT:
            case DATETIME:
            default:
                sb.append(NULLABLE).append(LEFT_BRACKET).append(STRING_TYPE).append(RIGHT_BRACKET);
                break;
        }
        sb.append(SPACE).append(DEFAULT_NULL).append(SPACE).append(COMMENT).append(SPACE).append(SINGLE_QUOTES).append(field.getFieldAlias()).append(SINGLE_QUOTES);
        return sb.toString();
    }




}
