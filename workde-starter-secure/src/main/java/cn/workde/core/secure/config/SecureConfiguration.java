package cn.workde.core.secure.config;

import cn.workde.core.secure.interceptor.TokenInterceptor;
import cn.workde.core.secure.registry.SecureRegistry;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhujingang
 * @date 2019/8/29 5:35 PM
 */
@Order
@Configuration
@AllArgsConstructor
public class SecureConfiguration implements WebMvcConfigurer {

    private final SecureRegistry secureRegistry;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        secureProperties.getClient().forEach(
//                cs -> registry.addInterceptor(secureHandler.clientInterceptor(cs.getClientId()))
//                        .addPathPatterns(cs.getPathPatterns())
//        );

        if (secureRegistry.isEnable()) {
            registry.addInterceptor(new TokenInterceptor())
                    .excludePathPatterns(secureRegistry.getExcludePatterns())
                    .excludePathPatterns(secureRegistry.getDefaultExcludePatterns());
                    //.excludePathPatterns(secureUrlProperties.getExcludePatterns())
                    //.excludePathPatterns(secureProperties.getSkipUrl());
        }
    }
}
