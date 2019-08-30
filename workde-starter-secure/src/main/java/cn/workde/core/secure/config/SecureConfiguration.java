package cn.workde.core.secure.config;

import cn.workde.core.base.properties.WorkdeProperties;
import cn.workde.core.secure.Aspect.AuthAspect;
import cn.workde.core.secure.handler.IPermissionHandler;
import cn.workde.core.secure.handler.WorkdePermissionHandler;
import cn.workde.core.secure.interceptor.TokenInterceptor;
import cn.workde.core.secure.registry.SecureRegistry;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhujingang
 * @date 2019/8/29 5:35 PM
 */
@Order
@Configuration
@AllArgsConstructor
@AutoConfigureAfter(RegistryConfiguration.class)
public class SecureConfiguration implements WebMvcConfigurer {

    private final SecureRegistry secureRegistry;
	private final JdbcTemplate jdbcTemplate;

	private WorkdeProperties workdeProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (secureRegistry.isEnable()) {
            registry.addInterceptor(new TokenInterceptor())
                    .excludePathPatterns(secureRegistry.getExcludePatterns())
                    .excludePathPatterns(secureRegistry.getDefaultExcludePatterns())
                    .excludePathPatterns(workdeProperties.getWorkdeSecureProperties().getSkipUrl());
        }
    }

	@Bean
	public AuthAspect authAspect() {
		return new AuthAspect();
	}

	@Bean
	@ConditionalOnMissingBean(IPermissionHandler.class)
	public IPermissionHandler permissionHandler() {
		return new WorkdePermissionHandler(jdbcTemplate);
	}
}
