package cn.workde.core.builder.engine.service;

import cn.workde.core.builder.engine.Builder;
import cn.workde.core.builder.engine.ModuleBuffer;
import cn.workde.core.builder.utils.FileUtil;
import cn.workde.core.builder.utils.JsonUtil;
import cn.workde.core.builder.utils.StringUtil;
import cn.workde.core.builder.utils.WebUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author zhujingang
 * @date 2019/9/22 11:54 AM
 */
public class PortalService {

	@Autowired
	private IdeService ideService;

	@Autowired
	private ModuleBuffer moduleBuffer;

	private JSONArray getModuleList(HttpServletRequest request, String path) throws Exception {
		File moduleFolder = null;
		if(StringUtil.isEmpty(path)) {
			moduleFolder = Builder.getInstance().getModuleFolder();
			path = "";
		}else {
			moduleFolder = new File(Builder.getInstance().getModulePath(), path);
		}
		path = String.valueOf(path) + "/";
		final List<Map.Entry<String, Integer>> fileNames =  ideService.getSortedFile(moduleFolder);
		final JSONArray fileArray = new JSONArray();
		JSONObject content = null;
		for(Map.Entry<String, Integer> entry : fileNames) {
			final String fileName = entry.getKey();
			final File file = new File(moduleFolder, fileName);
			if(!file.exists()) continue;
			if(!moduleBuffer.canDisplay(file)) continue;
			final boolean isFolder = file.isDirectory();
			if(isFolder) {
				final File configFile = new File(file, "folder.json");
				if(configFile.exists()) content = JsonUtil.readObject(configFile);
				else content = new JSONObject();
			}else {
				content = moduleBuffer.get(path + fileName);
			}
			final JSONObject fileObject = new JSONObject();
			final String title = content.optString("title");
			fileObject.put("text", title);
			final String relPath = FileUtil.getModulePath(file);
			fileObject.put("path", relPath);
			fileObject.put("fileName", fileName);
			fileObject.put("inframe", Boolean.TRUE.equals(content.opt("inframe")));
			if(isFolder) {
				fileObject.put("children", getModuleList(request, relPath));
			}else {
				final String pageLink = (String)content.opt("pageLink");
				if (!StringUtil.isEmpty(pageLink)) {
					fileObject.put("pageLink", (Object)pageLink);
				}
				fileObject.put("leaf", true);
			}
			fileObject.put("cls", "wb_pointer");
			final String iconCls = content.optString("iconCls");
			if(!iconCls.isEmpty()) fileObject.put("iconCls", iconCls);
			fileArray.put(fileObject);
		}
		return fileArray;
	}

	private JSONObject getAppListText(HttpServletRequest request) throws Exception {
		final JSONObject result = new JSONObject();
		result.put("fileName", "root");
		result.put("children", getModuleList(request, request.getParameter("path")));
		return result;
	}

	public void getAppList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		WebUtil.send(response, getAppListText(request));
	}

	public void initHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject treeNodes = getAppListText(request);
		request.setAttribute("treeNodes", treeNodes);
	}

}
