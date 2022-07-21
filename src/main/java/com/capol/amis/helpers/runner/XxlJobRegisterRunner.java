package com.capol.amis.helpers.runner;

import com.capol.amis.service.IDataSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/18 14:07
 * desc: XXLJob任务注册
 */
@Slf4j
@Component
public class XxlJobRegisterRunner implements CommandLineRunner {

    @Autowired
    private IDataSyncService dataSyncService;

    @Override
    public void run(String... args) throws Exception {
        // TODO zyx 这里仅注册一个任务, 后续需要读取数据库中所有任务然后全部注册
        dataSyncService.registerJobHandler("handler_1658112508");
    }
}
