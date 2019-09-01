package cn.workde.core.boot.config;

import cn.workde.core.boot.json.JsonReturnHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhujingang
 * @date 2019/9/1 11:17 AM
 */
@Configuration
@AutoConfigureAfter({WorkdeAutoConfiguration.class})
public class WorkdeReturnConfiguration {

	@Autowired
	private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

	@Bean
	public HandlerMethodReturnValueHandler jsonReturnHandler() {
		return new JsonReturnHandler();
	}

	@PostConstruct
	public void init() {
		final List<HandlerMethodReturnValueHandler> originalHandlers = new ArrayList<>(
			requestMappingHandlerAdapter.getReturnValueHandlers());
		final int deferredPos = obtainValueHandlerPosition(originalHandlers, DeferredResultMethodReturnValueHandler.class);
		// Add our typehandler directly after the deferred typehandler.
		originalHandlers.add(deferredPos + 1, jsonReturnHandler());

		requestMappingHandlerAdapter.setReturnValueHandlers(originalHandlers);
	}

	private int obtainValueHandlerPosition(final List<HandlerMethodReturnValueHandler> originalHandlers, Class<?> handlerClass) {
		for (int i = 0; i < originalHandlers.size(); i++) {
			final HandlerMethodReturnValueHandler valueHandler = originalHandlers.get(i);
			if (handlerClass.isAssignableFrom(valueHandler.getClass())) {
				return i;
			}
		}
		return -1;
	}

}
