package com.capol.amis.service.impl;

import com.capol.amis.entity.bo.DatasetFieldBasicBO;
import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.helpers.comparator.DatasetFieldBasicComparator;
import com.capol.amis.helpers.datasetsql.DatasetDataOperator;
import com.capol.amis.helpers.datasetsql.DatasetSqlGenerator;
import com.capol.amis.helpers.publisher.DataSyncPublisher;
import com.capol.amis.service.IDataSyncService;
import com.capol.amis.service.IDatasetDataService;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/18 09:27
 * desc: 数据同步实现类
 */
@Service
@Slf4j
public class DataSyncServiceImpl implements IDataSyncService {

    @Autowired
    private DataSyncPublisher dataSyncPublisher;

    @Autowired
    private IDatasetDataService datasetDataService;

    @Autowired
    @Qualifier("reportTemplate")
    private JdbcTemplate jdbcTemplate;

    /**
     * xxl-job中注册任务, 将连表操作元数据发送到MQ中
     */
    @Override
    public void registerJobHandler(String jobHandler) {
        XxlJobExecutor.registJobHandler(jobHandler, new IJobHandler() {
            @Override
            public void execute() throws Exception {
                // TODO zyx 生成List<DatasetUnionBO> 发送到MQ中
                List<DatasetUnionBO> unionFields = datasetDataService.getUnionFields();
                unionFields.forEach(dataSyncPublisher::datasetSyncPublish);
            }
        });
    }

    /**
     * 从主题表中同步数据到数据集中
     */
    @Override
    public void syncDataToClickhouse(DatasetUnionBO datasetUnionBO) {
        // 获取数据
        List<Map<String, Object>> unionJoinDatas = datasetDataService.getUnionJoinDatas(datasetUnionBO);
        // 获取字段列表并按照别名排序
        Map<Long, List<DatasetFieldBasicBO>> datasetQueryFields = datasetUnionBO.getDatasetQueryFields();
        List<DatasetFieldBasicBO> queryList = new ArrayList<>();
        for (List<DatasetFieldBasicBO> querys : datasetQueryFields.values()) {
            queryList.addAll(querys);
        }
        queryList.sort(new DatasetFieldBasicComparator());

        // 删除并重新创建数据集表
        String tableName = DatasetSqlGenerator.TABLE_PREFIX + datasetUnionBO.getDatasetId();
        jdbcTemplate.execute(DatasetSqlGenerator.genDropTableSql(tableName));
        jdbcTemplate.execute(DatasetSqlGenerator.genCreateTableSql(tableName, queryList));
        // 插入数据
        String insertSql = DatasetSqlGenerator.genInsertSql(tableName, queryList);
        new DatasetDataOperator(unionJoinDatas, queryList, jdbcTemplate, insertSql).exec();
    }

}
