package cn.workde.core.boot.launch;

import cn.hutool.core.util.StrUtil;
import cn.workde.core.boot.constant.AppConstant;
import cn.workde.core.boot.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.*;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目启动器，搞定环境变量问题
 * @author zhujingang
 * @date 2019/8/28 6:28 PM
 */
public class WorkdeApplication {

    /**
     * Create an application context
     * java -jar app.jar --spring.profiles.active=prod --server.port=2333
     *
     * @param appName application name
     * @param source  The sources
     * @return an application context created from the current state
     */
    public static ConfigurableApplicationContext run(String appName, Class source, String... args) {
        SpringApplicationBuilder builder = createSpringApplicationBuilder(appName, source, args);
        return builder.run(args);
    }

    public static SpringApplicationBuilder createSpringApplicationBuilder(String appName, Class source, String... args) {

        Assert.hasText(appName, "[appName]服务名不能为空");
        // 读取环境变量，使用spring boot的规则
        ConfigurableEnvironment environment = new StandardEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(new SimpleCommandLinePropertySource(args));
        propertySources.addLast(new MapPropertySource(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, environment.getSystemProperties()));
        propertySources.addLast(new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, environment.getSystemEnvironment()));

        SpringApplicationBuilder builder = new SpringApplicationBuilder(source);

        String profile = isLocalDev() ? "dev" : "prod";

        String startJarPath = WorkdeApplication.class.getResource("/").getPath().split("!")[0];
        System.out.println(String.format("----启动中，读取到的环境变量:[%s]，jar地址:[%s]----", profile, startJarPath));
        Properties props = System.getProperties();
        props.setProperty("spring.application.name", appName);
        props.setProperty("spring.profiles.active", profile);
        props.setProperty("info.desc", appName);
        props.setProperty("info.version", AppConstant.APPLICATION_VERSION);
        props.setProperty("workde.env", profile);
        props.setProperty("workde.name", appName);
        props.setProperty("workde.is-local", String.valueOf(isLocalDev()));
        props.setProperty("workde.dev-mode", profile.equals(AppConstant.PROD_CODE) ? "false" : "true");
        props.setProperty("spring.main.allow-bean-definition-overriding", "true");
        // 加载自定义组件
        List<LauncherService> launcherList = new ArrayList<>();
        ServiceLoader.load(LauncherService.class).forEach(launcherList::add);
        launcherList.stream().sorted(Comparator.comparing(LauncherService::getOrder)).collect(Collectors.toList())
                .forEach(launcherService -> launcherService.launcher(builder, appName, profile, isLocalDev()));
        return builder;
    }

    /**
     * 判断是否为本地开发环境
     *
     * @return boolean
     */
    public static boolean isLocalDev() {
        String osName = System.getProperty("os.name");
        return StrUtil.isNotEmpty(osName) && !(AppConstant.OS_NAME_LINUX.equals(osName.toUpperCase()));
    }
}
