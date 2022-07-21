package com.capol.amis.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/15 14:53
 * desc: RabbitMQ属性配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq.template")
@Data
public class RabbitMQProperties {
    private String exchange;
    private String defaultReceiveQueue;
    private String routingKey;
}
