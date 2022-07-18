package com.capol.amis.service.impl;

import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.helpers.gen.TestEntityGenerator;
import com.capol.amis.helpers.publisher.DataSyncPublisher;
import com.capol.amis.service.IDataSyncService;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/18 09:27
 * desc: 数据同步实现类
 */
@Service
public class DataSyncServiceImpl implements IDataSyncService {

    @Autowired
    private DataSyncPublisher dataSyncPublisher;

    @Override
    public void registerJobHandler(String jobHandler) {
        XxlJobExecutor.registJobHandler(jobHandler, new IJobHandler() {
            @Override
            public void execute() throws Exception {
                DatasetUnionBO datasetUnion = TestEntityGenerator.getDatasetUnion();
                dataSyncPublisher.datasetSyncPublish(datasetUnion);
            }
        });
    }

}
