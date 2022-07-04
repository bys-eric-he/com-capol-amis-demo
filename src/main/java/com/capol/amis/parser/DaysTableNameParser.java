package com.capol.amis.parser;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 按天分表解析
 */
public class DaysTableNameParser implements TableNameHandler {

    @Override
    public String dynamicTableName(String sql, String tableName) {
        String dateDay = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return tableName + "_" + dateDay;
    }
}
