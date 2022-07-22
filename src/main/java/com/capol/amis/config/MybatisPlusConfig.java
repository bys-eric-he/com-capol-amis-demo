package com.capol.amis.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.capol.amis.enums.DBTypeEnum;
import com.capol.amis.helpers.DynamicDataSource;
import com.capol.amis.parser.EnterpriseTableNameParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置加载表名处理器
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.capol.amis.mapper", sqlSessionFactoryRef = "datasourceSqlSessionFactory")
@Slf4j
public class MybatisPlusConfig {
    /*
    @Bean(name = "datasource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = DruidDataSourceBuilder.create().build();
        return druidDataSource;
    }

    @Bean(name = "qcDatasource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.master")
    public DataSource qcDatasource() {
        DruidDataSource druidDataSource = DruidDataSourceBuilder.create().build();
        return druidDataSource;
    }

    @Bean(name = "datasourceTransactionManager")
    public DataSourceTransactionManager masterTransactionManager(@Qualifier(value = "datasource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "datasourceSqlSessionFactory")
    @ConfigurationPropertiesBinding()
    public SqlSessionFactory sqlSessionFactory(@Qualifier(value = "qcDatasource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/*.xml"));
        //加载分表插件
        factoryBean.setPlugins(mybatisPlusInterceptor());
        //配置打印SQL语句
        factoryBean.setConfiguration(printSQLConfig());
        return factoryBean.getObject();
    }

    public MybatisConfiguration printSQLConfig() {
        MybatisConfiguration config = new MybatisConfiguration();
        config.setLogImpl(StdOutImpl.class);

        return config;
    }
    */

    @Bean(name = "amisDemo")
    @ConfigurationProperties(prefix = "spring.datasource.druid.amis-demo")
    public DataSource amisDemo() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "qaBiz")
    @ConfigurationProperties(prefix = "spring.datasource.druid.qa-biz")
    public DataSource qaBiz() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "report")
    @ConfigurationProperties(prefix = "spring.datasource.druid.clickhouse")
    public DataSource report() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 动态数据源配置
     */
    @Bean(name = "multipleDataSource")
    @Primary
    public DataSource multipleDataSource(@Qualifier("amisDemo") DataSource amisDemo,
                                         @Qualifier("qaBiz") DataSource qaBiz,
                                         @Qualifier("report") DataSource report) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DBTypeEnum.AMIS_DEMO.getValue(), amisDemo);
        targetDataSources.put(DBTypeEnum.QA_BIZ.getValue(), qaBiz);
        targetDataSources.put(DBTypeEnum.REPORT.getValue(), report);
        dynamicDataSource.setTargetDataSources(targetDataSources);
        // 程序默认数据源，根据程序调用数据源频次，把常调用的数据源作为默认
        dynamicDataSource.setDefaultTargetDataSource(amisDemo);
        return dynamicDataSource;
    }

    @Bean("datasourceSqlSessionFactory")
    @ConfigurationPropertiesBinding()
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(multipleDataSource(amisDemo(), qaBiz(), report()));
        // 设置默认需要扫描的 xml 文件
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*.xml"));
        //其他配置项
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        // 驼峰和下划线转换
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        configuration.setCallSettersOnNulls(true);
        // 打印SQL语句
        configuration.setLogImpl(StdOutImpl.class);
        sqlSessionFactory.setConfiguration(configuration);
        //sqlSessionFactory.setTypeAliasesPackage("com.capol.amis.entity");
        // 数据库查询结果驼峰式返回
        sqlSessionFactory.setObjectWrapperFactory(new MybatisMapWrapperFactory());
        // 添加分表插件
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor());
        // 实现自动填充功能
        // sqlSessionFactory.setGlobalConfig(printSQLConfig());
        return sqlSessionFactory.getObject();
    }

    /**
     * 动态事务配置
     *
     * @param dataSource
     * @return
     */
    @Bean(name = "multipleTransactionManager")
    @Primary
    public DataSourceTransactionManager multipleTransactionManager(@Qualifier("multipleDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 定义分表插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        HashMap<String, TableNameHandler> handlerMap = new HashMap<>();
        //这里为不同的表设置对应表名处理器
        handlerMap.put("cfg_business_subject", new EnterpriseTableNameParser());
        handlerMap.put("t_template_form_conf", new EnterpriseTableNameParser());
        handlerMap.put("t_template_grid_conf", new EnterpriseTableNameParser());
        handlerMap.put("t_template_form_data", new EnterpriseTableNameParser());
        handlerMap.put("t_template_grid_data", new EnterpriseTableNameParser());
        handlerMap.put("t_dataset", new EnterpriseTableNameParser());
        handlerMap.put("t_dataset_field", new EnterpriseTableNameParser());
        handlerMap.put("t_dataset_union", new EnterpriseTableNameParser());

        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(handlerMap);
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        return interceptor;
    }

    @Bean(name = "amisDemoJdbcTemplate")
    public JdbcTemplate amisDemoJdbcTemplate(@Qualifier("amisDemo") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "qaBizJdbcTemplate")
    public JdbcTemplate qaBizJdbcTemplate(@Qualifier("qaBiz") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "reportTemplate")
    public JdbcTemplate reportJdbcTemplate(@Qualifier("report") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
