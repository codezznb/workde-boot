package cn.workde.core.base.utils;

/**
 * @author zhujingang
 * @date 2019/8/29 9:24 PM
 */
public class ExceptionUtils {

	public synchronized static void printException(Exception e) {
		System.err.println(e);
		StackTraceElement[] stackTraceElementArray = e.getStackTrace();
		for (int i = 0; i < 4; i++) {
			StackTraceElement stackTraceElement = stackTraceElementArray[i];
			String fileName = stackTraceElement.getFileName();
			String className = stackTraceElement.getClassName();
			String methodName = stackTraceElement.getMethodName();
			int lineNumber = stackTraceElement.getLineNumber();
			System.err.println("	at " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")");
		}
	}

}
