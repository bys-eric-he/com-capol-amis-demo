package com.capol.amis.parser;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 按天分表解析
 */
public class DaysTableNameParserEx implements TableNameHandler {

    private static ThreadLocal<String> tableNameSuffix = new ThreadLocal<>();

    public static ThreadLocal<String> getTableNameSuffix() {
        return tableNameSuffix;
    }

    public static void setTableNameSuffix(ThreadLocal<String> tableNameSuffix) {
        DaysTableNameParserEx.tableNameSuffix = tableNameSuffix;
    }

    @Override
    public String dynamicTableName(String sql, String tableName) {
        String suffix = tableNameSuffix.get();
        if (!StringUtils.isEmpty(suffix)) {
            return tableName + "_" + suffix;
        } else {
            String dateDay = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            return tableName + "_" + dateDay;
        }
    }
}