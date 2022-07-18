package com.capol.amis.service;

import com.capol.amis.AmisApplicationTests;
import com.capol.amis.entity.bo.XxlJobInfoBO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.Instant;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/18 10:00
 */
@Slf4j
public class CrontabServiceTests extends AmisApplicationTests {

    @Autowired
    private ICrontabService crontabService;

    @Test
    public void testCron() throws IOException {
        XxlJobInfoBO jobInfo = new XxlJobInfoBO();
        jobInfo.setId(1002);
        jobInfo.setJobDesc("rabbit test");
        jobInfo.setScheduleConf("0 2/5 * * * ?");
        jobInfo.setExecutorHandler("handler_" + Instant.now().getEpochSecond());
        crontabService.add(jobInfo);
        log.info("==========>>>>>>>>>> 注册成功 <<<<<<<<<<==========");
        System.in.read();
    }


}
