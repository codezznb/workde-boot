package cn.workde.core.builder.utils;

import cn.workde.core.base.utils.SpringUtils;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author zhujingang
 * @date 2019/9/18 9:46 AM
 */
public class SysUtil {

	private static long currentId;
	private static byte serverId0;
	private static byte serverId1;
	private static Object lock;
	public static final byte[] digits;
	public static final int bufferSize = 4096;

	static {
		SysUtil.currentId = 0L;
		SysUtil.lock = new Object();
		digits = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90 };
	}

	public static String getId() {
		final long id;
		synchronized (SysUtil.lock) {
			if (SysUtil.currentId == 0L) {
				SysUtil.currentId = new Date().getTime() * 10000L;
				final String serverId = "00";
				SysUtil.serverId0 = (byte)serverId.charAt(0);
				SysUtil.serverId1 = (byte)serverId.charAt(1);
			}
			id = SysUtil.currentId++;
		}
		return numToString(id);
	}

	private static String numToString(long num) {
		final byte[] buf = new byte[13];
		byte charPos = 12;
		buf[0] = SysUtil.serverId0;
		buf[1] = SysUtil.serverId1;
		long val;
		while ((val = num / 36L) > 0L) {
			final byte[] array = buf;
			final byte b = charPos;
			charPos = (byte)(b - 1);
			array[b] = SysUtil.digits[(byte)(num % 36L)];
			num = val;
		}
		buf[charPos] = SysUtil.digits[(byte)num];
		return new String(buf);
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
}
