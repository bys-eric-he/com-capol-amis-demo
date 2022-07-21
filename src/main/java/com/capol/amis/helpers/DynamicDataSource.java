package com.capol.amis.helpers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.io.Serializable;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/6 11:12
 * desc: 动态数据源实现
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource implements Serializable {
    @Override
    protected Object determineCurrentLookupKey() {
        String datasource = DataSourceContextHolder.getDbType();
        log.debug("当前使用数据源:{}", datasource);
        return datasource;
    }
}
