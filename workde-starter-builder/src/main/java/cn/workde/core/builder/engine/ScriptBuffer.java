package cn.workde.core.builder.engine;

import cn.workde.core.base.utils.StringUtils;
import cn.workde.core.builder.utils.FileUtil;

import javax.annotation.PostConstruct;
import javax.script.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhujingang
 * @date 2019/9/18 1:30 PM
 */
public class ScriptBuffer {

	public ArrayList<String> globalMembers;
	private ScriptEngineManager manager;
	private ScriptEngine engine;
	private Compilable compilable;

	private static ConcurrentHashMap<String, CompiledScript> buffer;

	public void run(final String id, final String scriptText, final HttpServletRequest request, final HttpServletResponse response, final String sourceURL) throws Exception {
		CompiledScript script = ScriptBuffer.buffer.get(id);
		if (script == null) {
			script = compilable.compile(encScript(scriptText, sourceURL, true));
			ScriptBuffer.buffer.put(id, script);
		}
		final Bindings bindings = engine.createBindings();
		bindings.put("request", (Object)request);
		bindings.put("response", (Object)response);
		script.eval(bindings);
	}

	private static String encScript(final String scriptText, final String sourceURL, final boolean addApp) {
		final StringBuilder buf = new StringBuilder(scriptText.length() + 100);
		buf.append("(function main(){");
		if (addApp) {
			buf.append("var app=Wb.getApp(request,response);");
		}
		buf.append(scriptText);
		buf.append("\n})();");
		if (!StringUtils.isEmpty(sourceURL)) {
			buf.append("\n//# sourceURL=");
			buf.append(sourceURL);
		}
		return buf.toString();
	}

	@PostConstruct
	public void initialize() {
		try {
			this.manager = new ScriptEngineManager();
			this.engine = manager.getEngineByName("javascript");
			this.compilable = (Compilable) engine;
			this.buffer = new ConcurrentHashMap<String, CompiledScript>();
			this.globalMembers = new ArrayList<String>();

			this.loadUtils();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void loadUtils() throws Exception {
		String text = FileUtil.readString(new File(Builder.getInstance().getSystemFolder(), "server.js"));
		text = String.valueOf(text) + "\n//# sourceURL=server.js";
		final CompiledScript script = compilable.compile(text);
		final Bindings bindings = engine.createBindings();
		script.eval(bindings);
		final Set<Map.Entry<String, Object>> es = bindings.entrySet();
		for (final Map.Entry<String, Object> e : es) {
			final String key = e.getKey();
			manager.put(e.getKey(), e.getValue());
			globalMembers.add(key);
		}
	}
}
