package com.capol.amis.service.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * Service层事务管理
 */
@Slf4j
public class ServiceTransactionDefinition {
    /**
     * 事务管理器
     */
    //@Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    /**
     * 事务的一些基础信息，如超时时间、隔离级别、传播属性等
     */
    //@Autowired
    private TransactionDefinition transactionDefinition;

    /**
     * 事务的一些状态信息，如是否是一个新的事务、是否已被标记为回滚
     */
    private TransactionStatus transactionStatus;

    /**
     * 开启事务
     */
    public void start() {
        //根据事务定义TransactionDefinition，获取事务
        transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        log.info("--------开始事务----------");
    }

    /**
     * 提交事务
     */
    public void commit() {
        dataSourceTransactionManager.commit(transactionStatus);
        log.info("--------提交事务----------");
    }

    /**
     * 回滚事务
     */
    public void rollback() {
        dataSourceTransactionManager.rollback(transactionStatus);
        log.warn("--------回滚事务----------");
    }
}
