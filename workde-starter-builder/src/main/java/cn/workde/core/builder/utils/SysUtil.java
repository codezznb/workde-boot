package cn.workde.core.builder.utils;

import cn.workde.core.base.properties.WorkdeProperties;
import cn.workde.core.base.utils.IdUtils;
import cn.workde.core.base.utils.SpringUtils;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/**
 * @author zhujingang
 * @date 2019/9/18 9:46 AM
 */
public class SysUtil {

	public static Long getId() {
		return IdUtils.getId();
	}

	public static void executeServiceMethod(final String serviceName, final String methodName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Object service = SpringUtils.getBean(serviceName);
		if(service != null) {
			Method method = ReflectionUtils.findMethod(service.getClass(), methodName, HttpServletRequest.class, HttpServletResponse.class);
			if(method != null) {
				ReflectionUtils.invokeMethod(method, service, request, response);
			}
		}
	}

	public static boolean isArray(final Object object) {
		return object != null && object.getClass().isArray();
	}

	public static boolean isMap(final Object object) {
		return object instanceof Map;
	}

	public static boolean isIterable(final Object object) {
		return object instanceof Iterable;
	}

	public static String readString(final Reader reader) throws IOException {
		try {
			final char[] buf = new char[4096];
			final StringBuilder sb = new StringBuilder();
			int len;
			while ((len = reader.read(buf)) > 0) {
				sb.append(buf, 0, len);
			}
			return sb.toString();
		}
		finally {
			reader.close();
		}
	}
}
