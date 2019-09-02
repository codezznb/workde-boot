package cn.workde.core.admin.config;

import cn.workde.core.admin.properties.WorkdeAdminProperties;
import cn.workde.core.admin.web.AdminControllerHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author zhujingang
 * @date 2019/9/2 1:06 PM
 */
@Configuration
public class WorkdeAdminWebConfiguration {

	private final WorkdeAdminProperties workdeAdminProperties;

	public WorkdeAdminWebConfiguration(WorkdeAdminProperties workdeAdminProperties) {
		this.workdeAdminProperties = workdeAdminProperties;
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
			RequestMappingHandlerMapping mapping = new AdminControllerHandlerMapping(workdeAdminProperties.getContextPath());
			mapping.setOrder(0);
			mapping.setContentNegotiationManager(contentNegotiationManager);
			return mapping;
		}

	}
}
