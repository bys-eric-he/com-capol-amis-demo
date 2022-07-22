package com.capol.amis.helpers.listener;

import com.alibaba.fastjson.JSONObject;
import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.service.IDataSyncService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/15 14:40
 * desc: 监听数据同步消息
 */
@Component
@Slf4j
public class DataSyncListener {

    @Autowired
    private IDataSyncService dataSyncService;

    @RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
    public void consume(String msg, Channel channel, Message message) throws IOException {
        log.info("==========>>>>>>>>>> 接收到同步任务元数据:{}, 开始执行同步操作", msg);
        DatasetUnionBO datasetUnionBO = JSONObject.parseObject(msg, DatasetUnionBO.class);
        dataSyncService.syncDataToClickhouse(datasetUnionBO);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
