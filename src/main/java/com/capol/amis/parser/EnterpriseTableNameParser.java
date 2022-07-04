package com.capol.amis.parser;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.capol.amis.utils.BaseInfoContextHolder;

/**
 * 按企业分表解析
 */
public class EnterpriseTableNameParser implements TableNameHandler {

    @Override
    public String dynamicTableName(String sql, String tableName) {
        Long enterpriseId = BaseInfoContextHolder.getEnterpriseAndProjectInfo().getEnterpriseId();
        return tableName + "_" + enterpriseId;
    }
}
