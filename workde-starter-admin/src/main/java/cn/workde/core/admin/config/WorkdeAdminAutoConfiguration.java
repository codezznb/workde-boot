package cn.workde.core.admin.config;

import cn.workde.core.admin.properties.WorkdeAdminProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhujingang
 * @date 2019/9/2 1:03 PM
 */
@Configuration
@EnableConfigurationProperties(WorkdeAdminProperties.class)
@Import({WorkdeAdminWebConfiguration.class})
@AutoConfigureAfter({WebClientAutoConfiguration.class})
public class WorkdeAdminAutoConfiguration {
}
