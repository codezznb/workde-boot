package cn.workde.core.secure.config;

import cn.workde.core.secure.registry.SecureRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * secure注册默认配置
 * @author zhujingang
 * @date 2019/8/29 10:00 PM
 */
@Order
@Configuration
public class RegistryConfiguration {

	@Bean
	@ConditionalOnMissingBean(SecureRegistry.class)
	public SecureRegistry secureRegistry() {
		return new SecureRegistry();
	}
}
