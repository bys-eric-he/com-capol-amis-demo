package com.capol.amis.helpers.publisher;

import com.alibaba.fastjson.JSONObject;
import com.capol.amis.config.properties.RabbitMQProperties;
import com.capol.amis.entity.bo.DatasetUnionBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/15 15:53
 * desc: 数据同步消息发送
 */
@Component
@Slf4j
public class DataSyncPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQProperties rabbitProperties;

    public void datasetSyncPublish(DatasetUnionBO datasetUnion) {
        // 开启confirms机制
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("==========>>>>>>>>>> confirmCallBack触发 <<<<<<<<<<==========");
            if (correlationData != null) {
                ReturnedMessage returned = correlationData.getReturned();
                if (returned != null) {
                    log.info("==========>>>>>>>>>> returned.getMessage():{}", returned.getMessage());
                }
            }
            if (!ack) {
                System.out.println("cause=" + cause);
                log.warn("==========>>>>>>>>>> rabbit数据未达exchange, 原因为:{}", cause);
            }
        });
        // 开启returns机制
        rabbitTemplate.setReturnsCallback(returned -> {
            String msg = new String(returned.getMessage().getBody());
            log.warn("==========>>>>>>>>>> 消息:{}入路由队列失败 <<<<<<<<<<==========", msg);
        });
        String pulisherContent = JSONObject.toJSONString(datasetUnion);
        // 发送消息
        rabbitTemplate.convertAndSend(rabbitProperties.getExchange(), rabbitProperties.getRoutingKey(), pulisherContent,
                message -> {
                    // 消息持久化
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                });
        log.info("==========>>>>>>>>>> 同步数据消息发送成功, 内容为:{}", pulisherContent);
    }

}
