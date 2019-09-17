package cn.workde.core.base.utils;


import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Predicate;

/**
 * @author zhujingang
 * @date 2019/8/29 5:15 PM
 */
public class WebUtils extends org.springframework.web.util.WebUtils {

    /**
     * 获取 HttpServletRequest
     *
     * @return {HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (requestAttributes == null) ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取ip
     *
     * @return {String}
     */
    public static String getIP() {
        return getIP(WebUtils.getRequest());
    }

	public static boolean isAjax() {
		HttpServletRequest request = getRequest();
		return (request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString()));
	}

    private static final String[] IP_HEADER_NAMES = new String[]{
            "x-forwarded-for",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    private static final Predicate<String> IP_PREDICATE = (ip) -> StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip);

    /**
     * 获取ip
     *
     * @param request HttpServletRequest
     * @return {String}
     */
    @Nullable
    public static String getIP(@Nullable HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ip = null;
        for (String ipHeader : IP_HEADER_NAMES) {
            ip = request.getHeader(ipHeader);
            if (!IP_PREDICATE.test(ip)) {
                break;
            }
        }
        if (IP_PREDICATE.test(ip)) {
            ip = request.getRemoteAddr();
        }
        return StringUtils.isBlank(ip) ? null : StringUtils.splitTrim(ip, ",").get(0);
    }

	public static void send(HttpServletResponse response, Object object) throws IOException {
		final InputStream inputStream = (object instanceof InputStream) ? ((InputStream)object) : null;
		try {
			if (response.isCommitted()) {
				return;
			}

			final OutputStream outputStream = (OutputStream) response.getOutputStream();

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
				response.flushBuffer();
			}
		}finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		if (inputStream != null) {
			inputStream.close();
		}
	}

}
