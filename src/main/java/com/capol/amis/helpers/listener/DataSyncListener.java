package com.capol.amis.helpers.listener;

import com.alibaba.fastjson.JSONObject;
import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.service.IDatasetDataService;
import com.rabbitmq.client.Channel;
import javafx.scene.shape.HLineTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/15 14:40
 * desc: 监听数据同步消息
 */
@Component
@Slf4j
public class DataSyncListener {

    @Autowired
    private IDatasetDataService datasetDataService;

    @RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
    public void consume(String msg, Channel channel, Message message) throws IOException {
        // TODO zyx 同步数据到Clickhouse中
        DatasetUnionBO datasetUnionBO = JSONObject.parseObject(msg, DatasetUnionBO.class);
        List<Map<String, Object>> unionJoinDatas = datasetDataService.getUnionJoinDatas(datasetUnionBO);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
