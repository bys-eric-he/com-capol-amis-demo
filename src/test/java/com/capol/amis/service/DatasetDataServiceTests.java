package com.capol.amis.service;

import com.capol.amis.AmisApplicationTests;
import com.capol.amis.entity.bo.DatasetUnionBO;
import com.capol.amis.helpers.gen.TestEntityGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

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

    @Test
    public void testGetUnionJoinDatas() {
        DatasetUnionBO datasetUnionBO = testEntityGenerator.getDatasetUnion();

        StopWatch sw = new StopWatch();
        sw.start("task00");
        datasetDataService.getUnionJoinDatas(datasetUnionBO);
        sw.stop();
        sw.start("task01");
        datasetDataService.getUnionJoinDatas(datasetUnionBO);
        sw.stop();
        sw.start("task02");
        List<Map<Long, Object>> unionJoinDatas = datasetDataService.getUnionJoinDatas(datasetUnionBO);
        unionJoinDatas.forEach(line -> log.info("==========>>>>>>>>>> {} <<<<<<<<<<==========", line));
        sw.stop();

        for (StopWatch.TaskInfo taskInfo : sw.getTaskInfo()) {
            System.out.println("taskInfo.getTaskName() = " + taskInfo.getTaskName());
            System.out.println("taskInfo.getTimeSeconds() = " + taskInfo.getTimeSeconds() + "s");
        }

    }

}
