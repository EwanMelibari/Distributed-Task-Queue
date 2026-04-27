package com.e1.DTQ.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    // IMPROVEMENT: dedicated thread pool for task processing.
    // Without this, @Scheduled runs on a single-thread scheduler — a 5s HEAVY_TASK
    // would block all LIGHT_TASK polling for its entire duration.
    // With @Async("taskExecutor"), the scheduler thread returns immediately after
    // handing the task off, so polling continues at the configured fixedRate.
    @Bean(name = "taskExecutor")
    public Executor taskExecutor(
            @Value("${worker.thread-pool.core-size:4}") int coreSize,
            @Value("${worker.thread-pool.max-size:8}") int maxSize,
            @Value("${worker.thread-pool.queue-capacity:100}") int queueCapacity) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("task-worker-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}