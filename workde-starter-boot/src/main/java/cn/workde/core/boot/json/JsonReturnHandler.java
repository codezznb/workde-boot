package cn.workde.core.boot.json;

import cn.workde.core.boot.annotation.MoreSerializeField;
import cn.workde.core.boot.annotation.SerializeField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * @author zhujingang
 * @date 2019/9/1 9:23 AM
 */
@Slf4j
public class JsonReturnHandler implements HandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter methodParameter) {
		// 如果有我们自定义的 JSON 注解 就用我们这个Handler 来处理
		boolean hasJsonAnnotation= methodParameter.hasMethodAnnotation(SerializeField.class) || methodParameter.hasMethodAnnotation(MoreSerializeField.class);
		return hasJsonAnnotation;
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
		// 设置这个就是最终的处理类了，处理完不再去找下一个类进行处理
		modelAndViewContainer.setRequestHandled(true);
		// 获得注解并执行filter方法 最后返回
		HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
		Annotation[] annos = methodParameter.getMethodAnnotations();
		JsonSerializer jsonSerializer = new JsonSerializer();
		Arrays.asList(annos).forEach(a -> {
			if (a instanceof SerializeField) {
				SerializeField serializeField = (SerializeField) a;
				jsonSerializer.filter(serializeField);
			}else if(a instanceof MoreSerializeField) {
				MoreSerializeField moreSerializeField = (MoreSerializeField) a;
				Arrays.asList(moreSerializeField.value()).forEach( serializeField -> {
					jsonSerializer.filter(serializeField);
				});
			}
		});
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		String json = jsonSerializer.toJson(returnValue);
		response.getWriter().write(json);

	}
}
