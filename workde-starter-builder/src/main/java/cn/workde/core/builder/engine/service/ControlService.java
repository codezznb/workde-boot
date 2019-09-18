package cn.workde.core.builder.engine.service;

import cn.workde.core.builder.engine.Builder;
import cn.workde.core.builder.utils.JsonUtil;
import cn.workde.core.builder.utils.WebUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhujingang
 * @date 2019/9/18 11:11 AM
 */
public class ControlService {

	public void open(HttpServletRequest request, HttpServletResponse response) throws Exception{
		final JSONObject json = JsonUtil.readObject(Builder.getInstance().getControlFile());
		final JSONArray data = new JSONArray();
		final JSONArray nodes = new JSONArray(request.getParameter("controls"));
		for (int j = nodes.length(), i = 0; i < j; ++i) {
			final String id = nodes.getString(i);
			final JSONObject control = JsonUtil.findObject(json, "children", "id", id);
			if (control == null) {
				throw new IOException("\"" + id + "\" 没有找到");
			}
			data.put((Object)control);
		}
		WebUtil.send(response, data);
	}

	public void getControlTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		final JSONObject json = JsonUtil.readObject(Builder.getInstance().getControlFile());
		if ("ide".equals(request.getParameter("type"))) {
			setControlNode(json);
		}
		WebUtil.send(response, json);
	}

	private static void setControlNode(final JSONObject node) {
		if (node.has("leaf")) {
			node.remove("leaf");
			node.put("type", node.get("id"));
			node.put("control", true);
			node.put("children", (Object)new JSONArray());
		}
		else {
			final JSONArray children = node.getJSONArray("children");
			for (int j = children.length(), i = 0; i < j; ++i) {
				setControlNode(children.getJSONObject(i));
			}
		}
	}
}
