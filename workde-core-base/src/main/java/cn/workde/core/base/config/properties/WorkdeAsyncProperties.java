package cn.workde.core.base.config.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhujingang
 * @date 2019/8/28 6:14 PM
 */
@Getter
@Setter
@ConfigurationProperties("workde.async")
public class WorkdeAsyncProperties {

    /**
     * 异步核心线程数，默认：2
     */
    private int corePoolSize = 2;
    /**
     * 异步最大线程数，默认：50
     */
    private int maxPoolSize = 50;
    /**
     * 队列容量，默认：10000
     */
    private int queueCapacity = 10000;
    /**
     * 线程存活时间，默认：300
     */
    private int keepAliveSeconds = 300;

}
