package cn.workde.core.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

/**
 * @author zhujingang
 * @date 2019/8/29 9:55 PM
 */
@Slf4j
public class SpringUtils implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		log.info("注入ApplicationContextAware");
		SpringUtils.context = context;
	}

	public static <T> T getBean(Class<T> clazz) {
		if (clazz == null) {
			return null;
		}
		try {
			return context.getBean(clazz);
		}catch (NoSuchBeanDefinitionException exception){
			return null;
		}
	}

	public static <T> T getBean(String beanId) {
		if (beanId == null) {
			return null;
		}
		try {
			return (T) context.getBean(beanId);
		}catch (NoSuchBeanDefinitionException exception){
			return null;
		}
	}

	public static <T> T getBean(String beanName, Class<T> clazz) {
		if (null == beanName || "".equals(beanName.trim())) {
			return null;
		}
		if (clazz == null) {
			return null;
		}
		try {
			return (T) context.getBean(beanName, clazz);
		}catch (NoSuchBeanDefinitionException exception){
			return null;
		}
	}

	public static ApplicationContext getContext() {
		if (context == null) {
			return null;
		}
		return context;
	}

	public static void publishEvent(ApplicationEvent event) {
		if (context == null) {
			return;
		}
		try {
			context.publishEvent(event);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

}
