package com.capol.amis.config;

import com.capol.amis.config.properties.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/15 11:31
 * desc: rabbitmq 配置
 */
@Configuration
public class RabbitMQConfig {

    @Autowired
    private RabbitMQProperties rabbitProperties;

    @Bean
    public Exchange bootExchange(){
        // channel.DeclareExchange
        return ExchangeBuilder.topicExchange(rabbitProperties.getExchange()).build();
    }

    @Bean
    public Queue bootQueue(){
        return QueueBuilder.durable(rabbitProperties.getDefaultReceiveQueue()).build();
    }

    @Bean
    public Binding bootBinding(Exchange bootExchange, Queue bootQueue){
        return BindingBuilder.bind(bootQueue).to(bootExchange).with(rabbitProperties.getRoutingKey()).noargs();
    }
}
