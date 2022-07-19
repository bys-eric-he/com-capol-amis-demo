package com.capol.amis.rabbit;

import com.alibaba.fastjson.JSONObject;
import com.capol.amis.AmisApplicationTests;
import com.capol.amis.config.properties.RabbitMQProperties;
import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.helpers.gen.TestEntityGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/15 15:44
 */
public class ProducerTests extends AmisApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQProperties rabbitProperties;

    @Test
    public void testPublish() {
        DatasetUnionBO datasetUnion = TestEntityGenerator.getDatasetUnion();
        for (int i = 0; i < 30; i++) {
            rabbitTemplate.convertAndSend(rabbitProperties.getExchange(), rabbitProperties.getRoutingKey(), JSONObject.toJSONString(datasetUnion));
        }
        System.out.println("消息发送成功");
    }

}
