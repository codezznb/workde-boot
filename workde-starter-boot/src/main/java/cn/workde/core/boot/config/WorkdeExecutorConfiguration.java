package cn.workde.core.boot.config;

import cn.workde.core.base.properties.WorkdeAsyncProperties;
import cn.workde.core.base.properties.WorkdeProperties;
import lombok.AllArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步处理
 * @author zhujingang
 * @date 2019/8/28 8:39 PM
 */
@Configuration
@EnableAsync
@EnableScheduling
@AllArgsConstructor
public class WorkdeExecutorConfiguration extends AsyncConfigurerSupport {

    private WorkdeProperties workdeProperties;

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        WorkdeAsyncProperties workdeAsyncProperties = workdeProperties.getWorkdeAsyncProperties();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(workdeAsyncProperties.getCorePoolSize());
        executor.setMaxPoolSize(workdeAsyncProperties.getMaxPoolSize());
        executor.setQueueCapacity(workdeAsyncProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(workdeAsyncProperties.getKeepAliveSeconds());
        executor.setThreadNamePrefix("async-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }


}
