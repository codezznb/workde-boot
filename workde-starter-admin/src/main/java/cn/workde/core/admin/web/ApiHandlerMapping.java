package cn.workde.core.admin.web;

import cn.workde.core.base.utils.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author zhujingang
 * @date 2019/9/2 4:54 PM
 */
@Slf4j
public class ApiHandlerMapping extends RequestMappingHandlerMapping {

	private final String adminContextPath;

	public ApiHandlerMapping(String adminContextPath) {
		this.adminContextPath = adminContextPath;
	}

	@Override
	protected boolean isHandler(Class<?> beanType) {
		Boolean adminController = AnnotatedElementUtils.hasAnnotation(beanType, AdminController.class);
		return adminController;
	}

	@Override
	protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
		Class<?> handlerType = (handler instanceof String ?
			obtainApplicationContext().getType((String) handler) : handler.getClass());
		if(handlerType != null) {
			AdminController adminController = handlerType.getAnnotation(AdminController.class);
			log.info("handler {} new mapping {}", handlerType, withPrefix(mapping, adminController));
			super.registerHandlerMethod(handler, method, withPrefix(mapping, adminController));
		}
	}

	private RequestMappingInfo withPrefix(RequestMappingInfo mapping, AdminController adminController) {
		if (!StringUtils.hasText(adminContextPath)) {
			return mapping;
		}
		PatternsRequestCondition patternsCondition = new PatternsRequestCondition(
			withNewPatterns(mapping.getPatternsCondition().getPatterns(), adminController));
		return new RequestMappingInfo(patternsCondition, mapping.getMethodsCondition(), mapping.getParamsCondition(),
			mapping.getHeadersCondition(), mapping.getConsumesCondition(), mapping.getProducesCondition(),
			mapping.getCustomCondition());
	}

	private String[] withNewPatterns(Set<String> patterns, AdminController adminController) {
		return patterns.stream()
			.map(pattern -> PathUtils.normalizePath(adminContextPath) + PathUtils.normalizePath(adminController.path() +  pattern))
			.toArray(String[]::new);
	}
}
