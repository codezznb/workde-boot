package cn.workde.core.builder.engine.service;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.workde.core.base.utils.FileUtils;
import cn.workde.core.base.utils.StringUtils;
import cn.workde.core.builder.engine.Builder;
import cn.workde.core.builder.engine.ControlBuffer;
import cn.workde.core.builder.engine.ModuleBuffer;
import cn.workde.core.builder.utils.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhujingang
 * @date 2019/9/18 12:55 PM
 */
public class IdeService {

	private static final String[] imageTypes;

	static {
		imageTypes = new String[] { "gif", "jpg", "png", "bmp" };
	}

	@Autowired
	private ModuleBuffer moduleBuffer;

	@Autowired
	private ControlBuffer controlBuffer;

	public void init(HttpServletRequest request, HttpServletResponse response) {
		final JSONObject config = new JSONObject();
		config.put("fileTitle", true);
		request.setAttribute("initParams", (Object) StringUtil.text(config.toString()));
	}

	public List<String> getIconList() throws FileNotFoundException {
		final File iconPath = ResourceUtils.getFile("classpath:static/images");
		final File[] files = FileUtil.listFiles(iconPath);
		final List<String> list = new ArrayList<String>();
		SortUtil.sort(files);
		File[] array;
		for (int length = (array = files).length, i = 0; i < length; ++i) {
			final File file = array[i];
			if (!file.isDirectory()) {
				list.add(String.valueOf(FileUtil.removeExtension(file.getName())) + "_icon");
			}
		}
		return list;
	}

	public List<String[]> getGlyphClasses() throws Exception {
		final File file = ResourceUtils.getFile("classpath:static/libs/fa/css/font-awesome-debug.css");
		final String script = FileUtil.readString(file);
		String[] lines = script.substring(script.indexOf(".fa-glass")).split("}");
		int j = lines.length;
		List<String[]> result = new ArrayList<>();
		for (int i = 0; i < j; i++) {
			String line = lines[i];
			String[] item = new String[2];
			item[0] = line.substring(line.indexOf("content:") + 11, line.indexOf("content:") + 15);
			item[1] = line.substring(4, line.indexOf(":"));
			result.add(item);
		}
		return result;
	}

	public void getSysData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> sysData = new HashMap<>();
		sysData.put("iconData", getIconList());
		sysData.put("glyphClasses", getGlyphClasses());
		WebUtil.send(response, new JSONObject(sysData));
	}

	public void getList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if ("root".equals(request.getParameter("node"))) {
			getBaseList(request, response);
		} else if ("module".equals(request.getParameter("type"))) {
			getModuleList(request, response);
		} else {
			// getFileList(request, response);
		}
	}

	private void getModuleList(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final String path = request.getParameter("path");
		final File base = new File(path);
		final List<Map.Entry<String, Integer>> fileNames = getSortedFile(base);
		final JSONArray fileArray = new JSONArray();

		for (final Map.Entry<String, Integer> entry : fileNames) {
			final String fileName = entry.getKey();
			if ("folder.json".equalsIgnoreCase(fileName)) {
				continue;
			}

			final File file = new File(base, fileName);
			if (!file.exists()) {
				continue;
			}

			final boolean isFolder = file.isDirectory();
			JSONObject content = null;
			if (isFolder) {
				final File configFile = new File(file, "folder.json");
				if (configFile.exists()) {
					content = JsonUtil.readObject(configFile);
				}
			} else if (fileName.endsWith(".xwl")) {
				content = moduleBuffer.get(file);
			}

			if (content == null) {
				content = new JSONObject();
				if (!isFolder) {
					content.put("icon", ("builder?xwl=dev/ide/get-file-icon&file=" + WebUtil.encode(FileUtil.getPath(file))));
				}
			}

			final JSONObject fileObject = new JSONObject();
			fileObject.put("text", fileName);
			fileObject.put("title", content.optString("title"));
			final boolean hidden = Boolean.TRUE.equals(content.opt("hidden"));
			fileObject.put("hidden", hidden);
			if (hidden) {
				fileObject.put("cls", "x-highlight");
			}
			fileObject.put("inframe", Boolean.TRUE.equals(content.opt("inframe")));
			fileObject.put("pageLink", content.optString("pageLink"));
			final String iconCls = content.optString("iconCls");
			if (!StringUtils.isEmpty(iconCls)) {
				fileObject.put("iconCls", iconCls);
			}
			final String icon = content.optString("icon");
			if (!StringUtils.isEmpty(icon)) {
				fileObject.put("icon", icon);
			}
			if (isFolder) {
				if (!hasChildren(file)) {
					fileObject.put("children", new JSONArray());
				}
			} else {
				fileObject.put("leaf", true);
			}
			fileArray.put(fileObject);
		}
		WebUtil.send(response, new JSONObject().put("children", fileArray));
	}

	private void getBaseList(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final JSONArray list = new JSONArray();
		JSONObject node = new JSONObject();
		node.put("text", "模块");
		node.put("iconCls", "module_icon");
		node.put("expanded", true);
		node.put("base", Builder.getInstance().getModulePath() + '/');
		node.put("type", "module");
		list.put(node);
		WebUtil.send(response, list);
	}

	public List<Map.Entry<String, Integer>> getSortedFile(final File dir) throws Exception {
		final Map<String, Integer> jsonMap = new HashMap<String, Integer>();
		final String[] fileNames = dir.list();
		SortUtil.sort(fileNames);
		for (int j = fileNames.length, i = 0; i < j; ++i) {
			jsonMap.put(fileNames[j - i - 1], Integer.MAX_VALUE - i);
		}
		final File configFile = new File(dir, "folder.json");
		if (configFile.exists()) {
			final JSONArray indexArray = JsonUtil.readObject(configFile).getJSONArray("index");
			for (int j = indexArray.length(), i = 0; i < j; ++i) {
				jsonMap.put(indexArray.getString(i), i);
			}
		}
		return SortUtil.sortValue(jsonMap, true);
	}

	private static boolean hasChildren(final File dir) {
		final File[] files = FileUtil.listFiles(dir);
		if (files == null) {
			return false;
		}
		File[] array;
		for (int length = (array = files).length, i = 0; i < length; ++i) {
			final File file = array[i];
			if (file.isDirectory()) {
				return true;
			}
			if (!file.getName().equals("folder.json")) {
				return true;
			}
		}
		return false;
	}

	public synchronized void addModule(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		String name = request.getParameter("name");
		final String title = request.getParameter("title");
		final String iconCls = request.getParameter("iconCls");
		String url = null;
		final boolean hidden = Boolean.parseBoolean(request.getParameter("hidden"));
		final boolean isDir = Boolean.parseBoolean(request.getParameter("isDir"));
		final JSONObject content = new JSONObject();
		final JSONObject moduleMeta;
		JSONObject moduleConfigs = moduleMeta = controlBuffer.get("module").optJSONObject("configs");
		content.put("title", title);
		content.put("iconCls", iconCls);
		content.put("hidden", hidden);
		if (!isDir) {
			content.put("roles", new JSONObject().put("default", 1));
			content.put("inframe", Boolean.parseBoolean(request.getParameter("inframe")));
			content.put("pageLink", request.getParameter("pageLink"));
			url = request.getParameter("url");
//			if (!UrlBuffer.exists(url, null)) {
//				throw new IllegalArgumentException("URL\u6377\u5f84 \"" + url + "\" \u5df2\u7ecf\u5b58\u5728\u3002");
//			}
			if (!name.endsWith(".xwl")) {
				if (name.toLowerCase().endsWith(".xwl")) {
					name = String.valueOf(name.substring(0, name.length() - 3)) + "xwl";
				}
				else {
					name = String.valueOf(name) + ".xwl";
				}
			}
			final JSONObject module = new JSONObject();
			moduleConfigs = new JSONObject();
			moduleConfigs.put("itemId", "module");
			final Set<String> moduleConfigEntries = moduleMeta.keySet();
			for (final String key : moduleConfigEntries) {
				final Object value = moduleMeta.optJSONObject(key).opt("value");
				if (value != null) {
					moduleConfigs.put(key, value.toString());
				}
			}
			module.put("children", new JSONArray());
			module.put("configs", moduleConfigs);
			module.put("type", "module");
			content.put("children", new JSONArray().put(module));
		}
		final File base = new File(request.getParameter("path"));
		final File file = addModule(base, name, isDir, content);
		setFileIndex(base, request.getParameter("indexName"), new JSONArray().put((Object)name), request.getParameter("type"));
		if (isDir) {
			final JSONObject fileInfo = new JSONObject();
			fileInfo.put("file", name);
			fileInfo.put("title", title);
			fileInfo.put("iconCls", iconCls);
			fileInfo.put("hidden", hidden);
			WebUtil.send(response, fileInfo);
		}
		else {
//			if (!url.isEmpty()) {
//				UrlBuffer.put(String.valueOf('/') + url, FileUtil.getModulePath(file));
//				UrlBuffer.save();
//			}
			doOpen(new JSONArray().put(FileUtil.getPath(file)), null, null, request, response);
		}
	}

	private void setFileIndex(final File folder, final String indexFileName, final JSONArray insertFileNames, final String type) throws Exception {
		final File file = new File(folder, "folder.json");
		final int j = insertFileNames.length();
		JSONObject content;
		JSONArray indexArray;
		int index;
		if (file.exists()) {
			content = JsonUtil.readObject(file);
			indexArray = content.getJSONArray("index");
			for (int i = 0; i < j; ++i) {
				index = indexArray.toList().indexOf(insertFileNames.getString(i));
				if (index != -1) {
					indexArray.remove(index);
				}
			}
			final int k = indexArray.length();
			for (int i = k - 1; i >= 0; --i) {
				final File checkFile = new File(folder, indexArray.getString(i));
				if (!checkFile.exists()) {
					indexArray.remove(i);
				}
			}
			if (StringUtil.isEmpty(indexFileName) || "append".equals(type)) {
				index = -1;
			}
			else {
				index = indexArray.toList().indexOf(indexFileName);
				if (index != -1 && "after".equals(type)) {
					++index;
				}
			}
		}
		else {
			content = new JSONObject();
			indexArray = new JSONArray();
			index = -1;
		}
		if (index == -1) {
			for (int i = j - 1; i >= 0; --i) {
				indexArray.put(insertFileNames.getString(i));
			}
		}
		else {
			for (int i = 0; i < j; ++i) {
				indexArray = JsonUtil.add(indexArray, index, insertFileNames.getString(i));
			}
		}
		content.put("index", indexArray);
		FileUtil.syncSave(file, content.toString());
	}

	private File addModule(final File base, final String name, final boolean isDir, final JSONObject content) throws Exception {
		final File file = new File(base, name);
		if (isDir) {
			FileUtil.syncCreate(file, true);
			final File configFile = new File(file, "folder.json");
			content.put("index", new JSONArray());
			FileUtil.syncSave(configFile, content.toString());
		}
		else {
			FileUtil.syncCreate(file, false);
			FileUtil.syncSave(file, content.toString());
		}
		return file;
	}

	public void openFile(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		doOpen(new JSONArray(request.getParameter("fileNames")), request.getParameter("charset"), request.getParameter("type"), request, response);
	}

	private void doOpen(final JSONArray fileNames, String charset, final String type, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final JSONArray result = new JSONArray();
		final boolean fromEditor = "1".equals(type);
		for (int j = fileNames.length(), i = 0; i < j; ++i) {
			final String filename = fileNames.getString(i);
			final String shortFilename = FileUtil.getFilename(filename);
			final String fileExt = FileUtil.getFileExt(filename).toLowerCase();
			final File file = new File(filename);
			//limitDemoUser(request, file);
			JSONObject content;
			if (!fromEditor && fileExt.equals("xwl")) {
				content = JsonUtil.readObject(file);
				fillProperties(content);
				content.put("file", (Object)shortFilename);
			}
			else {
				content = new JSONObject();
				content.put("file", (Object)shortFilename);
				content.put("icon", (Object)("builder?xwl=dev/ide/get-file-icon&file=" + WebUtil.encode(filename)));
				String fileText;
				if (StringUtil.indexOf(imageTypes, fileExt) == -1) {
					if (StringUtil.isEmpty(charset)) {
						charset = "utf-8";
					} else if ("default".equals(charset)) {
						charset = null;
					}
					if (StringUtil.isEmpty(charset)) {
						fileText = FileUtils.readString(file, charset);
						content.put("charset", "default");
					} else {
						fileText = FileUtils.readString(file, charset);
						content.put("charset", (Object)charset);
					}
				}
				else {
					fileText = StringUtil.encodeBase64(new FileInputStream(file));
				}
				content.put("content", (Object)fileText);
			}
			content.put("lastModified", (Object)DateUtil.getTimestamp(file.lastModified()));
			content.put("path", (Object)filename);
			result.put((Object)content);
		}
		WebUtil.send(response, result);
	}

	public synchronized void saveFile(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final boolean confirm = !Boolean.parseBoolean(request.getParameter("noConfirm"));
		final JSONArray files = new JSONArray(StringUtil.getString((InputStream)request.getInputStream()));
		final JSONArray lastModifiedData = new JSONArray();
		String filename = null;
		final int j = files.length();
		int modifiedCount = 0;
		for (int i = 0; i < j; ++i) {
			final JSONObject content = files.getJSONObject(i);
			final File file = new File(content.getString("file"));

			if (confirm && file.lastModified() != DateUtil.strToDate(content.getString("lastModified")).getTime()) {
				if (filename == null) {
					filename = content.getString("file");
				}
				++modifiedCount;
			}
		}

		if (modifiedCount > 0) {
			throw new RuntimeException("文件 \"" + FileUtil.getFilename(filename) + "\"" + ((modifiedCount > 1) ? (" 等 " + modifiedCount + " 项") : " ") + "已经被修改，确定要保存吗?");
		}

		for (int i = 0; i < j; ++i) {
			final JSONObject content = files.getJSONObject(i);
			filename = content.getString("file");
			final String saveContent = content.getString("content");
			final File file = new File(filename);
			final String fileExt = FileUtil.getFileExt(filename).toLowerCase();
			if (StringUtil.indexOf(imageTypes, fileExt) == -1) {
				if ("xwl".equals(fileExt)) {
					updateModule(file, new JSONObject(saveContent), null, false);
				} else {
					FileUtil.syncSave(file, saveContent, content.optString("charset"));
					this.clearModuleBuffer(file);
				}
			} else {
				FileUtil.syncSave(file, StringUtil.decodeBase64(saveContent));
				this.clearModuleBuffer(file);
			}
			lastModifiedData.put(DateUtil.getTimestamp(file.lastModified()));
		}
		WebUtil.send(response, lastModifiedData);
	}

	private void fillProperties(final JSONObject json) throws IOException {
		final JSONArray controls = json.getJSONArray("children");
		for (int j = controls.length(), i = 0; i < j; ++i) {
			final JSONObject control = controls.getJSONObject(i);
			final String type = control.getString("type");
			final JSONObject fillControl = controlBuffer.get(type);
			if (fillControl == null) {
				throw new NullPointerException("控件 \"" + type + "\"  没有找到.");
			}
			final JSONObject configs = control.optJSONObject("configs");
			if (configs != null) {
				control.put("text", (Object)configs.optString("itemId"));
			}
			final JSONObject fillGeneral = fillControl.getJSONObject("general");
			control.put("iconCls", (Object)fillGeneral.getString("iconCls"));
			if (control.has("children") && control.length() > 0) {
				fillProperties(control);
			}
		}
	}

	public synchronized void updateModule(final File file, final JSONObject moduleData, final String[] roles, final boolean addRoles) throws Exception {
		if (moduleData == null && roles == null) {
			return;
		}
		JSONObject data = JsonUtil.readObject(file);
		final JSONObject oldRoles = (JSONObject)data.opt("roles");
		if (moduleData != null) {
			data = moduleData;
			data.put("roles", (Object)oldRoles);
		}
		if (roles != null) {
			for (final String role : roles) {
				if (addRoles) {
					oldRoles.put(role, 1);
				}
				else {
					oldRoles.remove(role);
				}
			}
		}
		FileUtil.syncSave(file, data.toString());
		this.clearModuleBuffer(file);
	}

	private void clearModuleBuffer(final File file) {
		final String relPath = FileUtil.getModulePath(file);
		if(relPath != null) {
			moduleBuffer.clear(relPath);
		}
	}

	public synchronized void deleteFiles(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final JSONArray files = new JSONArray(request.getParameter("files"));
		for (int j = files.length(), i = 0; i < j; ++i) {
			final String filename = files.getString(i);
			final File file = new File(filename);
			FileUtil.syncDelete(file);
			clearModuleBuffer(file);
		}
	}

	public synchronized void setProperty(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final JSONObject map = WebUtil.fetch(request);
		File configFile = null;
		final File oldFile = new File(map.getString("path"));
		final String url = map.optString("url");
		final String oldName = oldFile.getName();
		final String newName = map.getString("text");
		final boolean nameModified = !oldName.equals(newName);
		final boolean urlValid = map.optBoolean("urlValid");
		JSONObject indexContent = null;
		final boolean isDir = oldFile.isDirectory();
//		if (urlValid && !UrlBuffer.exists(url, oldFile)) {
//			throw new IllegalArgumentException("URL\u6377\u5f84 \"" + url + "\" \u5df2\u7ecf\u5b58\u5728\u3002");
//		}
		File newFile;
		if (nameModified) {
			newFile = new File(oldFile.getParent(), newName);
		}
		else {
			newFile = oldFile;
		}
		if (nameModified) {
			FileUtil.syncRename(oldFile, newFile);
		}
		final File indexFile = new File(newFile.getParentFile(), "folder.json");
		final boolean indexFileExists = indexFile.exists();
		final boolean isModule = map.getBoolean("isModule");
		final boolean needConfig = isModule || indexFileExists;
		if (needConfig) {
			if (indexFileExists) {
				indexContent = JsonUtil.readObject(indexFile);
			}
			else if (isModule) {
				indexContent = new JSONObject();
				indexContent.put("index", (Object)new JSONArray());
			}
			if (isModule) {
				JSONObject content;
				if (isDir) {
					configFile = new File(newFile, "folder.json");
					if (configFile.exists()) {
						content = JsonUtil.readObject(configFile);
					}
					else {
						content = new JSONObject();
						content.put("index", (Object)new JSONArray());
					}
				}
				else {
					content = JsonUtil.readObject(newFile);
				}
				content.put("title", (Object)map.getString("title"));
				content.put("iconCls", (Object)map.getString("iconCls"));
				content.put("hidden", map.getBoolean("hidden"));
				if (isDir) {
					FileUtil.syncSave(configFile, content.toString());
				}
				else {
					content.put("inframe", map.getBoolean("inframe"));
					content.put("pageLink", (Object)map.getString("pageLink"));
					updateModule(newFile, content, null, false);
				}
			}
			if (nameModified && indexFileExists) {
				final JSONArray idxArray = indexContent.getJSONArray("index");
				final int index = idxArray.toList().indexOf((Object)oldName);
				if (index != -1) {
					idxArray.put(index, (Object)newName);
					FileUtil.syncSave(indexFile, indexContent.toString());
				}
			}
		}
		final JSONObject result = new JSONObject();
		result.put("lastModified", DateUtil.getTimestamp(newFile.lastModified()));
		result.put("path", FileUtil.getPath(newFile));
		if (nameModified) {
			final JSONObject resp = new JSONObject();
			final JSONArray src = new JSONArray();
			final JSONArray moveTo = new JSONArray();
			src.put(FileUtil.getPath(oldFile));
			moveTo.put(FileUtil.getPath(newFile));
			final Object[] changeInfo = changePath(src, moveTo);
			resp.put("files", changeInfo[0]);
			resp.put("change", changeInfo[1]);
			resp.put("moveTo", moveTo);
			result.put("refactorInfo", resp);
		}
		WebUtil.send(response, result);
	}

	private Object[] changePath(final JSONArray source, final JSONArray dest) throws Exception {
		final JSONArray rows = new JSONArray();
		final ArrayList<Object[]> changes = new ArrayList<Object[]>();
		boolean changed = false;
		for (int j = source.length(), i = 0; i < j; ++i) {
			final File srcFile = new File(source.getString(i));
			final Object value = dest.opt(i);
			File dstFile;
			if (value instanceof String) {
				dstFile = new File((String)value);
			}
			else {
				dstFile = new File((String)((Object[])value)[0]);
			}
			final boolean isDir = dstFile.isDirectory();
			if (isDir || (srcFile.getName().endsWith(".xwl") && dstFile.getName().endsWith(".xwl"))) {
				String srcPath = FileUtil.getModulePath(srcFile);
				String dstPath = FileUtil.getModulePath(dstFile);
				if (!isDir) {
					srcPath = srcPath.substring(0, srcPath.length() - 4);
					dstPath = dstPath.substring(0, dstPath.length() - 4);
				}
				final Object[] change = new Object[2];
				if (isDir) {
					change[0] = Pattern.compile("\\bxwl=" + srcPath + "\\b/");
					change[1] = "xwl=" + dstPath + "/";
				}
				else {
					change[0] = Pattern.compile("(\\bxwl=" + srcPath + "\\b)(?![/\\-\\.])");
					change[1] = "xwl=" + dstPath;
				}
				changes.add(change);
			}
		}
		doChangePath(Builder.getInstance().getModuleFolder(), changes, rows);
		//doChangePath(new File(Base.path, "script"), changes, rows);
		final Object[] result = { rows, changes };
		return result;
	}

	private void doChangePath(final File folder, final ArrayList<Object[]> changes, final JSONArray rows) throws Exception {
		final File[] files = FileUtil.listFiles(folder);
		File[] array;
		for (int length = (array = files).length, i = 0; i < length; ++i) {
			final File file = array[i];
			if (file.isDirectory()) {
				doChangePath(file, changes, rows);
			}
			else {
				final String fileExt = FileUtil.getFileExt(file).toLowerCase();
				if (fileExt.equals("xwl") || fileExt.equals("js")) {
					String replacedText;
					final String text = replacedText = FileUtil.readString(file);
					for (final Object[] change : changes) {
						replacedText = ((Pattern)change[0]).matcher(replacedText).replaceAll(Matcher.quoteReplacement((String)change[1]));
					}
					if (!replacedText.equals(text)) {
						FileUtil.syncSave(file, replacedText);
						final JSONObject row = new JSONObject();
						row.put("path", (Object)FileUtil.getPath(file));
						row.put("lastModified", (Object)DateUtil.getTimestamp(file.lastModified()));
						rows.put((Object)row);
					}
				}
			}
		}
	}

	public synchronized void moveFiles(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final String isCopyStr = request.getParameter("isCopy");
		final boolean fromPaste = !StringUtil.isEmpty(isCopyStr);
		final boolean isCopy = "true".equals(isCopyStr);
		final boolean confirm = !Boolean.parseBoolean(request.getParameter("noConfirm"));
		final JSONArray src = new JSONArray(request.getParameter("src"));
		JSONArray moveTo = null;
		final File dstFile = new File(request.getParameter("dst"));
		final String dropPosition = request.getParameter("dropPosition");
		String overwriteFilename = null;
		int j = src.length();
		int overwriteCount = 0;
		File folder;
		if (dropPosition.equals("append")) {
			folder = dstFile;
		} else {
			folder = dstFile.getParentFile();
		}
		if (folder == null) {
			throw new Exception("无法复制到此目录.");
		}

		for (int i = 0; i < j; ++i) {
			if (src.getString(i).isEmpty()) {
				throw new IllegalArgumentException("复制源含无效目录.");
			}
			final File srcFile = new File(src.getString(i));
			final String filename = srcFile.getName();
			final File newDstFile = new File(folder, filename);
			if (FileUtil.isAncestor(srcFile, newDstFile, false)) {
				throw new IllegalArgumentException("上级目录 \"" + filename + "\" 不能复制到下级目录.");
			}
			if (newDstFile.exists()) {
				final boolean sameFolder = folder.equals(srcFile.getParentFile());
				if (fromPaste) {
					if (!isCopy && sameFolder) {
						throw new IllegalArgumentException("同一目录内剪切 \"" + filename + "\" 无效.");
					}
					if (confirm && !sameFolder) {
						if (overwriteFilename == null) {
							overwriteFilename = filename;
						}
						++overwriteCount;
					}
				} else if (!sameFolder) {
					throw new IllegalArgumentException("\"" + filename + "\" 已经存在.");
				}
			}
		}

		final JSONArray srcNames = new JSONArray();
		if (fromPaste) {
			moveTo = copyFiles(src, folder, !isCopy);
			j = moveTo.length();
			for (int i = j - 1; i >= 0; --i) {
				final Object[] moveInfo = (Object[])moveTo.get(i);
				if (!(boolean)moveInfo[1]) {
					srcNames.put(FileUtil.getFilename((String)moveInfo[0]));
				}
			}
		}
		else {
			for (int i = j - 1; i >= 0; --i) {
				srcNames.put(FileUtil.getFilename(src.getString(i)));
				if (folder.equals(new File(src.getString(i)).getParentFile())) {
					src.remove(i);
				}
			}
			moveTo = doMoveFiles(src, folder);
		}
		if ("module".equals(request.getParameter("type"))) {
			setFileIndex(folder, dstFile.getName(), srcNames, dropPosition);
		}
		final JSONObject resp = new JSONObject();
		if (!isCopy) {
			final Object[] result = changePath(src, moveTo);
			resp.put("files", result[0]);
			resp.put("change", result[1]);
		}
		resp.put("moveTo", (Object)moveTo);
		WebUtil.send(response, resp);

	}

	private JSONArray copyFiles(final JSONArray src, final File dstFolder, final boolean deleteOld) throws Exception {
		final int j = src.length();
		final JSONArray newNames = new JSONArray();
		for (int i = 0; i < j; ++i) {
			final File file = new File(src.getString(i));
			final Object[] object = FileUtil.syncCopy(file, dstFolder);
			if (deleteOld) {
				FileUtil.syncDelete(file);
			}
			newNames.put((Object)object);
		}
		return newNames;
	}

	private JSONArray doMoveFiles(final JSONArray src, final File dstFile) throws Exception {
		final int j = src.length();
		final JSONArray result = new JSONArray();
		for (int i = 0; i < j; ++i) {
			final File file = new File(src.getString(i));
			FileUtil.syncMove(file, dstFile);
			final Object[] object = { FileUtil.getPath(new File(dstFile, file.getName())), false };
			result.put((Object)object);
		}
		return result;
	}

	public void total(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final File file = new File(request.getParameter("path"));
		final JSONObject result = new JSONObject();
		final boolean isDir = file.isDirectory();
		result.put("lastModified", (Object)DateUtil.getTimestamp(file.lastModified()));
		result.put("fileSize", file.length());
		if (isDir && FileUtil.isAncestor(Builder.getInstance().getModuleFolder(), file)) {
			final int[] info = new int[4];
			total(file, info);
			result.put("total", (Object)info);
		}
		WebUtil.send(response, result);
	}

	private void total(final File folder, final int[] info) {
		final File[] files = FileUtil.listFiles(folder);
		File[] array;
		for (int length = (array = files).length, i = 0; i < length; ++i) {
			final File file = array[i];
			if (file.isDirectory()) {
				final int n = 2;
				++info[n];
				total(file, info);
			}
			else {
				if (file.getName().endsWith(".xwl")) {
					final int n2 = 0;
					++info[n2];
				}
				final int n3 = 1;
				++info[n3];
				final int n4 = 3;
				info[n4] += (int)file.length();
			}
		}
	}
}
