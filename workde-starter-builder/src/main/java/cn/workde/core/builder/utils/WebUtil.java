package cn.workde.core.builder.utils;

import cn.workde.core.base.utils.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

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

	public static void sendObject(final HttpServletResponse response, Object o) throws IOException {
		send(response, new JSONObject(o));
	}

	public static void send(final HttpServletResponse response, final Object object) throws IOException {
		final InputStream inputStream = (object instanceof InputStream) ? ((InputStream)object) : null;
		try {
			if (response.isCommitted()) {
				return;
			}
			final OutputStream outputStream = (OutputStream)response.getOutputStream();
			if (inputStream == null) {
				byte[] bytes;
				if (object instanceof byte[]) {
					bytes = (byte[]) object;
				} else {
					String text;
					if (object == null) {
						text = "";
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
			}
			else {
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


}
