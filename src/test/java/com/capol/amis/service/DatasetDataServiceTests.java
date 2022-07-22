package com.capol.amis.service;

import com.capol.amis.AmisApplicationTests;
import com.capol.amis.entity.DatasetUnionDO;
import com.capol.amis.entity.bo.DatasetFieldBasicBO;
import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.helpers.comparator.DatasetFieldBasicComparator;
import com.capol.amis.helpers.datasetsql.DatasetDataOperator;
import com.capol.amis.helpers.datasetsql.DatasetSqlGenerator;
import com.capol.amis.helpers.gen.TestEntityGenerator;
import com.capol.amis.utils.TaskRunnerUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/12 18:42
 */
@Slf4j
public class DatasetDataServiceTests extends AmisApplicationTests {

    @Autowired
    private IDatasetDataService datasetDataService;

    @Autowired
    private TestEntityGenerator testEntityGenerator;

    @Autowired
    @Qualifier("reportTemplate")
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testGetUnionJoinDatas() {
        DatasetUnionBO datasetUnionBO = testEntityGenerator.getDatasetUnion();

        StopWatch sw = new StopWatch();
        TaskRunnerUtils.recordTask(sw, "task00", () -> datasetDataService.getUnionJoinDatas(datasetUnionBO));
        TaskRunnerUtils.recordTask(sw, "task01", () -> datasetDataService.getUnionJoinDatas(datasetUnionBO));
        TaskRunnerUtils.recordTask(sw, "task02", () -> datasetDataService.getUnionJoinDatas(datasetUnionBO));

        TaskRunnerUtils.printStopWatch(sw);
    }

    @Test
    public void testUnionFields() {
        List<DatasetUnionBO> unionFields = datasetDataService.getUnionFields();
        log.info("==========>>>>>>>>>> 连接字段：{}", unionFields);

        DatasetUnionBO datasetUnionBO = unionFields.get(0);
        List<Map<String, Object>> unionJoinDatas = datasetDataService.getUnionJoinDatas(datasetUnionBO);
        log.info("==========>>>>>>>>>> 连接数据:{}", unionJoinDatas);

        Map<Long, List<DatasetFieldBasicBO>> datasetQueryFields = datasetUnionBO.getDatasetQueryFields();

        List<DatasetFieldBasicBO> queryList = new ArrayList<>();
        for (List<DatasetFieldBasicBO> querys : datasetQueryFields.values()) {
            queryList.addAll(querys);
        }
        queryList.sort(new DatasetFieldBasicComparator());
        String tableName = DatasetSqlGenerator.TABLE_PREFIX + datasetUnionBO.getDatasetId();

        String dropSql = DatasetSqlGenerator.genDropTableSql(tableName);
        jdbcTemplate.execute(dropSql);
        String createSql = DatasetSqlGenerator.genCreateTableSql(tableName, queryList);
        jdbcTemplate.execute(createSql);
        String insertSql = DatasetSqlGenerator.genInsertSql(tableName, queryList);
        new DatasetDataOperator(unionJoinDatas, queryList, jdbcTemplate, insertSql).exec();
    }

}
