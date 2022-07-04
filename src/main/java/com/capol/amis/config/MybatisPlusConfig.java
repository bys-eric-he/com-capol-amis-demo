package com.capol.amis.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.capol.amis.parser.EnterpriseTableNameParser;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * 配置加载表名处理器
 */
@Configuration
@MapperScan(basePackages = "com.capol.amis.mapper", sqlSessionFactoryRef = "datasourceSqlSessionFactory")
public class MybatisPlusConfig {
    @Bean(name = "datasource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = DruidDataSourceBuilder.create().build();
        return druidDataSource;
    }

    @Bean(name = "datasourceTransactionManager")
    public DataSourceTransactionManager masterTransactionManager(@Qualifier(value = "datasource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "datasourceSqlSessionFactory")
    @ConfigurationPropertiesBinding()
    public SqlSessionFactory sqlSessionFactory(@Qualifier(value = "datasource") DataSource dataSource) throws Exception {
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

    /**
     * 定义分表插件
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        HashMap<String, TableNameHandler> map = new HashMap<String, TableNameHandler>();

        //这里为不同的表设置对应表名处理器
        map.put("cfg_business_subject", new EnterpriseTableNameParser());
        map.put("t_template_form_conf", new EnterpriseTableNameParser());
        map.put("t_template_grid_conf", new EnterpriseTableNameParser());
        map.put("t_template_form_data", new EnterpriseTableNameParser());
        map.put("t_template_grid_data", new EnterpriseTableNameParser());

        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(map);
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        return interceptor;
    }

    /**
     * 打印SQL语句
     *
     * @return
     */
    public MybatisConfiguration printSQLConfig() {
        MybatisConfiguration config = new MybatisConfiguration();
        config.setLogImpl(StdOutImpl.class);

        return config;
    }
}
