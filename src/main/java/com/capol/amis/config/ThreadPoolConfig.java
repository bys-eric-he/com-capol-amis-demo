package com.capol.amis.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/12 09:41
 * desc: 线程池配置类
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "datasetServiceExecutor")
    public Executor datasetServiceExecutor() {
        // ThreadFactory threadFactory = new ThreadFactoryBuilder().setNamePrefix("datasetServiceExecutor-").build();
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("datasetServiceExecutor-%s").build();
        return new ThreadPoolExecutor(3,
                6,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
