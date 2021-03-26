package io.penguinstats.configuration;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class ExecutorConfig {

    @Value("${executor.size.core}")
    private Integer core = 10;

    @Value("${executor.size.max}")
    private Integer max = 20;

    @Value("${executor.size.queue}")
    private Integer queue = 8;

    @Value("${executor.keepalive.time}")
    private Integer keepalive = 60;

    @Bean
    public ThreadPoolTaskExecutor threadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(max);
        executor.setQueueCapacity(queue);
        executor.setThreadNamePrefix("threadPool_");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setKeepAliveSeconds(keepalive);
        executor.initialize();
        return executor;
    }

}
