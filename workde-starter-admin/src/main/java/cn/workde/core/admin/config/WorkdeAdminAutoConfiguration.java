package cn.workde.core.admin.config;

import cn.workde.core.admin.controller.IndexController;
import cn.workde.core.admin.controller.LoginController;
import cn.workde.core.admin.module.ModuleListener;
import cn.workde.core.admin.module.menu.MenuManager;
import cn.workde.core.admin.module.service.ModuleService;
import cn.workde.core.admin.properties.WorkdeAdminProperties;
import cn.workde.core.admin.web.WorkdeHandlerMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * @author zhujingang
 * @date 2019/9/2 1:03 PM
 */
@Configuration
@EnableConfigurationProperties(WorkdeAdminProperties.class)
@AutoConfigureAfter({WebClientAutoConfiguration.class})
@Slf4j
public class WorkdeAdminAutoConfiguration {

	private final WorkdeAdminProperties workdeAdminProperties;

	public WorkdeAdminAutoConfiguration(WorkdeAdminProperties workdeAdminProperties) {
		this.workdeAdminProperties = workdeAdminProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public LoginController loginController() {
		return new LoginController();
	}

	@Bean
	@ConditionalOnMissingBean
	public IndexController indexController() {
		return new IndexController();
	}

	@Bean
	public ModuleService moduleService() {
		return new ModuleService();
	}

	@EventListener(WebServerInitializedEvent.class)
	public void initMenuGroupItems() {
		List<ModuleListener> moduleListenerList = new ArrayList<>();
		ServiceLoader.load(ModuleListener.class).forEach(moduleListenerList::add);
		moduleListenerList.stream().sorted(Comparator.comparing(ModuleListener::getOrder)).collect(Collectors.toList())
			.forEach(moduleListener -> {
				moduleListener.onConfigMenuGroup(MenuManager.getInstance().getMenuGroups());
			});
		MenuManager.getInstance().buildMenuItem();
		log.info("【初始化配置-菜单管理】... 已初始化完毕");
	}

	@Configuration
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	public static class ServletRestModuleConfiguration {

		private WorkdeAdminProperties workdeAdminProperties;

		public ServletRestModuleConfiguration(WorkdeAdminProperties workdeAdminProperties) {
			this.workdeAdminProperties = workdeAdminProperties;
		}

		@Bean
		public RequestMappingHandlerMapping adminHandlerMapper(ContentNegotiationManager contentNegotiationManager) {
			RequestMappingHandlerMapping mapping = new WorkdeHandlerMapping(workdeAdminProperties.getContextPath());
			mapping.setOrder(0);
			mapping.setContentNegotiationManager(contentNegotiationManager);
			return mapping;
		}

	}
}
