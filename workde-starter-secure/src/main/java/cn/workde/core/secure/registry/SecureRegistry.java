package cn.workde.core.secure.registry;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * secure api放行配置
 * @author zhujingang
 * @date 2019/8/29 5:12 PM
 */
@Data
public class SecureRegistry {

    private final List<String> defaultExcludePatterns = new ArrayList<>();

    private final List<String> excludePatterns = new ArrayList<>();

    public SecureRegistry() {
		this.defaultExcludePatterns.add("/doc.html");
        this.defaultExcludePatterns.add("/v2/api-docs/**");
        this.defaultExcludePatterns.add("/v2/api-docs-ext/**");
		this.defaultExcludePatterns.add("/js/**");
		this.defaultExcludePatterns.add("/webjars/**");
		this.defaultExcludePatterns.add("/swagger-resources/**");
		this.defaultExcludePatterns.add("/actuator/health/**");
		this.defaultExcludePatterns.add("/error");
        this.defaultExcludePatterns.add("/auth/**");
		this.defaultExcludePatterns.add("/reg/**");
		this.defaultExcludePatterns.add("/forget/**");
		this.defaultExcludePatterns.add("/layui/**");
		this.defaultExcludePatterns.add("/scripts/**");
		this.defaultExcludePatterns.add("/images/**");
		this.defaultExcludePatterns.add("/styles/**");
		this.defaultExcludePatterns.add("/cpanel/login");
    }

    /**
     * 设置放行api
     */
    public SecureRegistry excludePathPatterns(String... patterns) {
        return excludePathPatterns(Arrays.asList(patterns));
    }

    /**
     * 设置放行api
     */
    public SecureRegistry excludePathPatterns(List<String> patterns) {
        this.excludePatterns.addAll(patterns);
        return this;
    }
}
