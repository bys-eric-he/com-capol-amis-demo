package com.capol.amis;

import com.capol.amis.config.ServerConfig;
import com.capol.amis.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
// 排除Druid相关依赖, 防止出现Failed to determine a suitable driver class错误
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableDiscoveryClient
@MapperScan("com.capol.amis.mapper")
@MapperScan(basePackages = {"com.capol.amis.mapper"})
@ServletComponentScan(basePackages = {"com.capol.amis.filter"})
public class AmisApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(AmisApplicationStarter.class, args);
        log.info("------->端口：{}, 服务启动完成!", SpringContextHolder.getBean(ServerConfig.class).getServerPort());
        log.info("------->访问：{}", SpringContextHolder.getBean(ServerConfig.class).getUrl());
        log.info("======>SnowflakeUtil:{}<======", SpringContextHolder.getBean("SnowflakeUtil").toString());
        log.info("======>RedisUtil:{}<======", SpringContextHolder.getBean("RedisUtil").toString());
    }
}
