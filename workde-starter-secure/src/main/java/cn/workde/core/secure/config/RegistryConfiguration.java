package cn.workde.core.secure.config;

import cn.workde.core.secure.handler.IPermissionHandler;
import cn.workde.core.secure.handler.WorkdePermissionHandler;
import cn.workde.core.secure.registry.SecureRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * secure注册默认配置
 * @author zhujingang
 * @date 2019/8/29 10:00 PM
 */
@Slf4j
@Order
@Configuration
public class RegistryConfiguration {

	@Bean
	@ConditionalOnMissingBean(SecureRegistry.class)
	public SecureRegistry secureRegistry() {
		log.info("【初始化权限放行配置】Bean：SecureRegistry ... 已初始化完毕。");
		return new SecureRegistry();
	}

	@Bean
	@ConditionalOnMissingBean(IPermissionHandler.class)
	public IPermissionHandler permissionHandler() {
		return new WorkdePermissionHandler();
	}
}
