package cn.workde.core.builder.utils;

import cn.workde.core.base.utils.FileUtils;
import cn.workde.core.base.utils.StringUtils;
import cn.workde.core.builder.engine.Builder;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * @author zhujingang
 * @date 2019/9/18 1:38 PM
 */
public class FileUtil {

	public static String getPath(final String path) {
		return StringUtil.replaceAll(path, "\\", "/");
	}

	public static String getPath(final File file) {
		return getPath(file.getAbsolutePath());
	}

	public static String getFileExt(final String fileName) {
		if (fileName != null) {
			final int i = fileName.lastIndexOf(46);
			if (i != -1) {
				return fileName.substring(i + 1);
			}
		}
		return "";
	}

	public static String getFileExt(final File file) {
		return getFileExt(file.getName());
	}

	public static String getFileType(final File file) {
		String type;
		try {
			type = FileSystemView.getFileSystemView().getSystemTypeDescription(file);
		} catch (Throwable e) {
			type = null;
		}
		if (StringUtils.isEmpty(type)) {
			return getFileExt(file.getName());
		}

		return type;
	}

	public static String getFilename(final String path) {
		if (StringUtils.isEmpty(path)) {
			return "";
		}
		final int p = Math.max(path.lastIndexOf(47), path.lastIndexOf(92));
		if (p == -1) {
			return path;
		}
		return path.substring(p + 1);
	}

	public static String removeExtension(final String fileName) {
		final String s = getFilename(fileName);
		final int i = s.lastIndexOf(46);
		if (i != -1) {
			return s.substring(0, i);
		}
		return s;
	}

	public static File[] listFiles(final File file) {
		if (!file.exists()) {
			throw new RuntimeException("\"" + file.getName() + "\" does not exist.");
		}
		final File[] fs = file.listFiles();
		if (fs == null) {
			return new File[0];
		}
		return fs;
	}

	public static String getModulePath(final File file) {
		final String path = getPath(file);
		final String modulePath = Builder.getInstance().getModulePath();
		if (path.startsWith(modulePath)) {
			return path.substring(modulePath.length());
		}
		return null;
	}

	public static void syncSave(final File file, final String content) throws Exception {
		syncSave(file, content, "utf-8");
	}

	public static void syncSave(final File file, final String content, final String charset) throws Exception {
		if (StringUtil.isEmpty(charset)) {
			FileUtils.writeString(content, file, "utf-8");
		} else {
			FileUtils.writeString(content, file, charset);
		}
	}

	public static void syncSave(final File file, final byte[] content) throws Exception {
		FileUtils.writeBytes(content, file);
	}
}
