package cn.workde.core.admin.web;

import cn.workde.core.admin.module.menu.MenuItem;
import cn.workde.core.admin.module.menu.MenuManager;
import cn.workde.core.admin.module.menu.annotation.AdminMenu;
import cn.workde.core.base.utils.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

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
		Boolean adminController = AnnotatedElementUtils.hasAnnotation(beanType, AdminController.class) || AnnotatedElementUtils.hasAnnotation(beanType, Controller.class);
		return adminController;
	}

	@Override
	protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
		Class<?> handlerType = (handler instanceof String ?
			obtainApplicationContext().getType((String) handler) : handler.getClass());
		if(handlerType != null) {
			AdminController adminController = handlerType.getAnnotation(AdminController.class);
			RequestMappingInfo requestMappingInfo;
			if(adminController != null) {
				requestMappingInfo = withPrefix(mapping, adminController);
				log.debug("{} ----> {}.{}", requestMappingInfo, method.getDeclaringClass(), method.getName());
				super.registerHandlerMethod(handler, method, requestMappingInfo);
			}else {
				requestMappingInfo = mapping;
			}
			AdminMenu adminMenu = method.getAnnotation(AdminMenu.class);
			if(adminMenu != null) {
				MenuItem menuItem = new MenuItem(adminMenu);
				if(StringUtils.isEmpty(menuItem.getUrl())) menuItem.setUrl(withUrl(requestMappingInfo.getPatternsCondition().getPatterns()));
				log.debug("{}",menuItem);
				MenuManager.getInstance().addMenuItem(menuItem);
			}
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

	private String withUrl(Set<String> patterns) {
		return patterns.stream().collect(Collectors.joining()).toString();
	}
}
