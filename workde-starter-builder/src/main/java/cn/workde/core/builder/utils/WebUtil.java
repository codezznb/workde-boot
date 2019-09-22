package cn.workde.core.builder.utils;

import cn.workde.core.base.utils.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhujingang
 * @date 2019/9/18 9:51 AM
 */
public class WebUtil {


	public static String encode(final String string) throws IOException {
		return StringUtil.replaceAll(URLEncoder.encode(string, "utf-8"), "+", "%20");
	}

	public static String replaceParams(final HttpServletRequest request, final String text) {
		if (request == null || StringUtils.isEmpty(text)) {
			return text;
		}
		int start = 0;
		int startPos = text.indexOf("${", start);
		int endPos = text.indexOf("}", startPos + 2);
		if (startPos != -1 && endPos != -1) {
			final StringBuilder buf = new StringBuilder(text.length());
			while (startPos != -1 && endPos != -1) {
				final String paramName = text.substring(startPos + 2, endPos);
				String paramValue;
				paramValue = fetch(request, paramName);
				buf.append(text.substring(start, startPos));
				if (paramValue != null) {
					buf.append(paramValue);
				}
				start = endPos + 1;
				startPos = text.indexOf("${", start);
				endPos = text.indexOf("}", startPos + 2);
			}
			buf.append(text.substring(start));
			return buf.toString();
		}
		return text;
	}

	public static Object fetchObject(final HttpServletRequest request, final String name) {
		final HttpSession session = request.getSession(false);
		Object value;
		if (session != null && (value = session.getAttribute(name)) != null) {
			return value;
		}
		value = request.getAttribute(name);
		if (value == null) {
			return request.getParameter(name);
		}
		return value;
	}

	public static String fetch(final HttpServletRequest request, final String name) {
		final Object object = fetchObject(request, name);
		if (object == null) {
			return null;
		}
		return object.toString();
	}

	public static JSONObject fetch(final HttpServletRequest request) {
		final JSONObject json = new JSONObject();
		final Iterator<?> requestParams = (Iterator<?>)request.getParameterMap().entrySet().iterator();
		final Enumeration<?> requestAttrs = (Enumeration<?>)request.getAttributeNames();
		while (requestParams.hasNext()) {
			final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)requestParams.next();
			json.put((String)entry.getKey(), (Object)((String[])(Object)entry.getValue())[0]);
		}
		while (requestAttrs.hasMoreElements()) {
			final String name = requestAttrs.nextElement().toString();
			if (!name.startsWith("sysx.")) {
				json.put(name, request.getAttribute(name));
			}
		}
		final HttpSession session = request.getSession(false);
		if (session != null) {
			final Enumeration<?> sessionAttrs = (Enumeration<?>)session.getAttributeNames();
			while (sessionAttrs.hasMoreElements()) {
				final String name = sessionAttrs.nextElement().toString();
				if (!name.startsWith("sysx.")) {
					json.put(name, session.getAttribute(name));
				}
			}
		}
		return json;
	}

	public static void sendObject(final HttpServletResponse response, Object o) throws IOException {
		send(response, new JSONObject(o));
	}

	public static void send(final HttpServletResponse response, final Object object) throws IOException {
		final InputStream inputStream = (object instanceof InputStream) ? ((InputStream)object) : null;
		try {
			if (response.isCommitted()) {
				return;
			}
			final OutputStream outputStream = response.getOutputStream();
			if (inputStream == null) {
				byte[] bytes;
				if (object instanceof byte[]) {
					bytes = (byte[]) object;
				} else {
					String text;
					if (object == null) {
						text = "";
					} else if( object instanceof  Exception) {
						text = ((Exception) object).getMessage();
						System.out.println(text);
						response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
					} else {
						text = object.toString();
					}
					bytes = text.getBytes("utf-8");
					if (StringUtils.isEmpty(response.getContentType())) {
						response.setContentType("text/html;charset=utf-8");
					}
				}
				final int len = bytes.length;
				response.setContentLength(len);
				outputStream.write(bytes);
			} else {
				IOUtils.copy(inputStream, outputStream);
			}
			response.flushBuffer();
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		if (inputStream != null) {
			inputStream.close();
		}
	}

	public static void setObject(final HttpServletRequest request, final String name, final Object value) {
		final Object object = request.getAttribute("sysx.varMap");
		final ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) object;
		if (map.containsKey(name)) {
			throw new IllegalArgumentException("Key \"" + name + "\" already exists.");
		}
		map.put(name, value);
	}

	public static Object getObject(final HttpServletRequest request, final String name) {
		final Object object = request.getAttribute("sysx.varMap");
		if (object != null) {
			final ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) object;
			return map.get(name);
		}
		return null;
	}

}
