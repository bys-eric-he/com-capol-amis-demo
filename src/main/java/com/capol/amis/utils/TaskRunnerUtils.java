package com.capol.amis.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/20 11:22
 */
@Slf4j
public class TaskRunnerUtils {

    public static void recordTask(StopWatch sw, String taskName, Thread thread) {
        sw.start(taskName);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sw.stop();
    }

    public static void printStopWatch(StopWatch sw) {
        for (StopWatch.TaskInfo taskInfo : sw.getTaskInfo()) {
            log.info("==========>>>>>>>>>> task name: {} <<<<<<<<<<==========", taskInfo.getTaskName());
            log.info("==========>>>>>>>>>> task cost: {} <<<<<<<<<<==========", taskInfo.getTimeMillis());
        }
    }


}
