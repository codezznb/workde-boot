package cn.workde.core.secure.config;

import cn.workde.core.secure.aspect.AuthAspect;
import cn.workde.core.secure.interceptor.TokenInterceptor;
import cn.workde.core.secure.properties.WorkdeSecureProperties;
import cn.workde.core.secure.registry.SecureRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhujingang
 * @date 2019/8/29 5:35 PM
 */
@Slf4j
@Order
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({WorkdeSecureProperties.class})
public class SecureConfiguration implements WebMvcConfigurer {

    private final SecureRegistry secureRegistry;

	private final WorkdeSecureProperties workdeSecureProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	log.info("secure enable {}", workdeSecureProperties.getEnable());
        if (workdeSecureProperties.getEnable()) {
            registry.addInterceptor(new TokenInterceptor())
                    .excludePathPatterns(secureRegistry.getExcludePatterns())
                    .excludePathPatterns(secureRegistry.getDefaultExcludePatterns())
                    .excludePathPatterns(workdeSecureProperties.getSkip());
        }
    }

	@Bean
	public AuthAspect authAspect() {
		return new AuthAspect();
	}

}
