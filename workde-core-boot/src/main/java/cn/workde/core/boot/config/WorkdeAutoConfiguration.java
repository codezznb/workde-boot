package cn.workde.core.boot.config;

import cn.workde.core.base.config.properties.WorkdeProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author zhujingang
 * @date 2019/8/28 6:13 PM
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(WorkdeProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@AllArgsConstructor
public class WorkdeAutoConfiguration {

    private WorkdeProperties workdeProperties;

}
