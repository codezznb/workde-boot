package cn.workde.core.builder.utils;

import cn.workde.core.base.utils.FileUtils;
import cn.workde.core.base.utils.StringUtils;
import cn.workde.core.builder.engine.Builder;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;

/**
 * @author zhujingang
 * @date 2019/9/18 1:38 PM
 */
public class FileUtil {

	public static File getUniqueFile(File file) {
		if (file.exists()) {
			final File parent = file.getParentFile();
			final String fullName = file.getName();
			final String namePart = removeExtension(fullName);
			String extPart = getFileExt(fullName);
			final boolean emptyExt = extPart.isEmpty();
			int i = 1;
			if (!emptyExt) {
				extPart = String.valueOf('.') + extPart;
			}
			do {
				if (emptyExt) {
					file = new File(parent, StringUtil.concat(namePart, Integer.toString(i)));
				}
				else {
					file = new File(parent, StringUtil.concat(namePart, Integer.toString(i), extPart));
				}
				++i;
			} while (file.exists());
		}
		return file;
	}

	public static boolean isAncestor(final File parent, final File child) throws IOException {
		return isAncestor(parent, child, true);
	}

	public static boolean isAncestor(final File parent, final File child, final boolean includeSelf) throws IOException {
		String parentPath = parent.getCanonicalPath();
		String childPath = child.getCanonicalPath();
		if (!parentPath.endsWith(new StringBuilder(String.valueOf(File.separatorChar)).toString())) {
			parentPath = String.valueOf(parentPath) + File.separatorChar;
		}
		if (!childPath.endsWith(new StringBuilder(String.valueOf(File.separatorChar)).toString())) {
			childPath = String.valueOf(childPath) + File.separatorChar;
		}
		return childPath.startsWith(parentPath) && (includeSelf || childPath.length() > parentPath.length());
	}

	public static String readString(final File file) throws Exception {
		if(file == null || !file.exists()) return "";
		return FileUtils.readString(file, "utf-8");
	}

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

	public static String getModulePath(final String url) {
		if (url.startsWith("builder?xwl=")) {
			return String.valueOf(url.substring(12)) + ".xwl";
		}
		if (url.endsWith(".xwl")) {
			return url;
		}

		return null;
	}

	public static String getModuleUrl(final File file) {
		final String path = getPath(file);
		if (path.startsWith(Builder.getInstance().getModulePath()) && path.toLowerCase().endsWith(".xwl")) {
			return "builder?xwl=" + path.substring(Builder.getInstance().getModulePath().length() + 1, path.length() - 4);
		}
		return null;
	}

	public static String getModulePath(final File file) {
		final String path = getPath(file);
		final String modulePath = Builder.getInstance().getModulePath();
		if (path.startsWith(modulePath)) {
			return path.substring(modulePath.length());
		}
		return null;
	}

	public static void syncCreate(final File file, final boolean isDir) throws IOException {
		final String name = file.getName();
//		final File syncPath = Builder.getInstance().getModuleFolder();
		if (file.exists()) {
			throw new IllegalArgumentException("\"" + name + "\" already exists.");
		}
//		if (syncPath != null && syncPath.exists()) {
//			throw new IllegalArgumentException("\"" + syncPath.getAbsolutePath() + "\" already exists.");
//		}
		if (isDir) {
			if (!file.mkdir()) {
				throw new IOException("Create \"" + name + "\" failure.");
			}
//			if (syncPath != null && !syncPath.mkdir()) {
//				throw new IOException("Create \"" + syncPath.getAbsolutePath() + "\" failure.");
//			}
		}
		else {
			if (!file.createNewFile()) {
				throw new IOException("Create \"" + name + "\" failure.");
			}
//			if (syncPath != null && !syncPath.createNewFile()) {
//				throw new IOException("Create \"" + syncPath.getAbsolutePath() + "\" failure.");
//			}
		}
//		if (syncPath != null) {
//			syncPath.setLastModified(file.lastModified());
//		}
	}

	public static void syncRename(final File file, final File newFile) throws IOException {
		if (!file.renameTo(newFile)) {
			throw new IOException("Cannot rename \"" + file.getName() + "\".");
		}
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

	public static void syncDelete(final File file) throws Exception {
		if(!FileUtils.del(file)) {
			throw new IOException("Cannot delete \"" + file.getName() + "\".");
		}
		clearFiles(file);
	}

	public static Object[] syncCopy(final File src, File dst) throws IOException {
		final String name = src.getName();
		dst = new File(dst, name);

		final boolean isDir = src.isDirectory();
		final boolean sameParent = src.getParentFile().equals(dst.getParentFile());
		if (sameParent) {
			dst = getUniqueFile(dst);
		}
		final boolean dstExists = dst.exists();
		if (isDir) {
			FileUtils.copy(src, dst, true);
		}
		else {
			FileUtils.copyFile(src, dst);
		}
		final Object[] result = { getPath(dst), dstExists };
		return result;
	}

	public static void syncCopyA(final File src, final File dst) throws IOException {
		FileUtils.copy(src, dst, false);
	}

	public static void syncCopyFiles(final File src, final File dst) {
		FileUtils.copyFilesFromDir(src, dst, false);
	}

	public static void syncMove(final File src, final File dst) throws Exception {
		FileUtils.move(src, dst, true);
		clearFiles(src);
	}

	private static void clearFiles(final File file) throws Exception {
		final File folder = file.getParentFile();
		final File configFile = new File(folder, "folder.json");
		if (configFile.exists()) {
			final JSONObject object = JsonUtil.readObject(configFile);
			final JSONArray index = object.optJSONArray("index");
			if (index != null) {
				final int j = index.length();
				for (int i = j - 1; i >= 0; --i) {
					final File indexFile = new File(folder, index.getString(i));
					if (!indexFile.exists()) {
						index.remove(i);
					}
				}
				syncSave(configFile, object.toString());
			}
		}
	}
}
