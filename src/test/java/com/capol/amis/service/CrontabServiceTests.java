package com.capol.amis.service;

import com.capol.amis.AmisApplicationTests;
import com.capol.amis.entity.bo.XxlJobInfoBO;
import com.capol.amis.utils.BaseInfoContextHolder;
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
        log.info("==========>>>>>>>>>> 开始注册xxljob任务 <<<<<<<<<<==========");
        Long enterpriseId = BaseInfoContextHolder.getEnterpriseAndProjectInfo().getEnterpriseId();
        XxlJobInfoBO jobInfo = new XxlJobInfoBO();
        jobInfo.setId(1002);
        jobInfo.setJobDesc("企业" + enterpriseId + "同步任务");
        jobInfo.setScheduleConf("0 2/5 * * * ?");
        jobInfo.setExecutorHandler("handler_" + enterpriseId);
        crontabService.add(jobInfo);
        log.info("==========>>>>>>>>>> 开始注册xxljob任务 <<<<<<<<<<==========");
    }


}
