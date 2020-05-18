package io.penguinstats.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ：yamika
 * @date ：Created in 2020/5/19 0:22
 * @description： ThreadPool config
 * @modified By：yamika
 */
@EnableAsync
@Configuration
public class ExecutorConfig {
    /** default threads num */
    private static final int CORE_POOL_SIZE = 1;
    /** max threads nym*/
    private static final int MAX_POOL_SIZE = 4;
    /** max free time(seconds) */
    private static final int KEEP_ALIVE_TIME = 15;
    /** size of wait queue */
    private static final int QUEUE_CAPACITY = 2;
    /** default prefix*/
    private static final String THREAD_NAME_PREFIX = "executorService-";

    @Bean("executor")
    public ThreadPoolTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_TIME);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
//        the policy of thread pool to solve rejected task
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }


}
