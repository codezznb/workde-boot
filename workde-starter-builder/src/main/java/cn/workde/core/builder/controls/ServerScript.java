package cn.workde.core.builder.controls;

import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.builder.engine.ScriptBuffer;
import org.json.JSONObject;

/**
 * @author zhujingang
 * @date 2019/9/18 1:29 PM
 */
public class ServerScript extends Control{

	@Override
	public void create() throws Exception {
		final String script = getScript(this.configs, "script");
		if (!script.isEmpty()) {
			ScriptBuffer scriptBuffer = SpringUtils.getBean(ScriptBuffer.class);
			scriptBuffer.run(script, this.request, this.response, this.gs("sourceURL"));
		}
	}

	public static String getScript(final JSONObject object, final String name) {
		final Object value = object.opt(name);
		if (value == null) {
			return "";
		}
		final String script = (String)value;
		if (script.indexOf("{#") != -1) {
			throw new IllegalArgumentException("ServerScript does not support {#param#} feature, please use app.get(param) instead.");
		}
		return script;
	}
}
