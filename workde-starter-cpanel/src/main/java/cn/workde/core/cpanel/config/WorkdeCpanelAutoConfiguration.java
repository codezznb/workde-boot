package cn.workde.core.cpanel.config;

import cn.workde.core.cpanel.controller.LoginController;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhujingang
 * @date 2019/9/5 5:10 PM
 */
@Configuration
@AutoConfigureAfter({WebClientAutoConfiguration.class})
public class WorkdeCpanelAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public LoginController loginController() {
		return new LoginController();
	}

}
