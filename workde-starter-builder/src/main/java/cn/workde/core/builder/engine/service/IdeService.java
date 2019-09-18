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
			System.out.println(line);
			System.out.println(line.length());
			System.out.println(line.indexOf("content:"));
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

	private List<Map.Entry<String, Integer>> getSortedFile(final File dir) throws Exception {
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
					System.out.println(value);
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
			content.put("index", indexArray);
			index = -1;
		}
		if (index == -1) {
			for (int i = j - 1; i >= 0; --i) {
				indexArray.put(insertFileNames.getString(i));
			}
		}
		else {
			for (int i = 0; i < j; ++i) {
				indexArray.toList().add(index, insertFileNames.getString(i));
			}
		}
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
}
