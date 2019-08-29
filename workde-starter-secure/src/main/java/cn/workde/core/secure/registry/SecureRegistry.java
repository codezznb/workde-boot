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

    private boolean enable = false;

    private final List<String> defaultExcludePatterns = new ArrayList<>();

    private final List<String> excludePatterns = new ArrayList<>();

    public SecureRegistry() {
        this.defaultExcludePatterns.add("/client/**");
        this.defaultExcludePatterns.add("/actuator/health/**");
        this.defaultExcludePatterns.add("/v2/api-docs/**");
        this.defaultExcludePatterns.add("/v2/api-docs-ext/**");
        this.defaultExcludePatterns.add("/auth/**");
        this.defaultExcludePatterns.add("/token/**");
        this.defaultExcludePatterns.add("/log/**");
        this.defaultExcludePatterns.add("/menu/routes");
        this.defaultExcludePatterns.add("/menu/auth-routes");
        this.defaultExcludePatterns.add("/menu/top-menu");
        this.defaultExcludePatterns.add("/process/resource-view");
        this.defaultExcludePatterns.add("/process/diagram-view");
        this.defaultExcludePatterns.add("/manager/check-upload");
        this.defaultExcludePatterns.add("/error/**");
        this.defaultExcludePatterns.add("/assets/**");
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
