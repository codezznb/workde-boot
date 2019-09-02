package cn.workde.core.base.utils;

/**
 * @author zhujingang
 * @date 2019/9/2 4:56 PM
 */
public class PathUtils {

	public static String normalizePath(String path) {
		if (!StringUtils.hasText(path)) {
			return path;
		}
		String normalizedPath = path;
		if (!normalizedPath.startsWith("/")) {
			normalizedPath = "/" + normalizedPath;
		}
		if (normalizedPath.endsWith("/")) {
			normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
		}
		return normalizedPath;
	}
}
