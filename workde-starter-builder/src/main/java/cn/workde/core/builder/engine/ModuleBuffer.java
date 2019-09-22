package cn.workde.core.builder.engine;


import cn.workde.core.builder.utils.FileUtil;
import cn.workde.core.builder.utils.JsonUtil;
import cn.workde.core.builder.utils.StringUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhujingang
 * @date 2019/9/17 10:05 PM
 */
public class ModuleBuffer {

	@Autowired
	private ScriptBuffer scriptBuffer;

	private ConcurrentHashMap<String, Object[]> buffer;

	public JSONObject get(final File file) throws Exception {
		return get(FileUtil.getModulePath(file));
	}

	public JSONObject get(final String path) {
		if(path == null) throw new NullPointerException("Module 路径未指定.");
		String pathKey = path.toLowerCase();
		File file = new File(Builder.getInstance().getModuleFolder(), pathKey);
		if(file == null || !file.exists()) throw new NullPointerException("Module \"" + path + "\" 找不到.");
		long lastModified = file.lastModified();
		Object[] obj = buffer.get(pathKey);
		if(obj != null && lastModified == (long) obj[1]) return (JSONObject) obj[0];

		JSONObject root;
		try {
			root = JsonUtil.readObject(file);
		}catch (Exception e) {
			throw new RuntimeException("Module \"" + file.getAbsolutePath() + "\" 格式有误.");
		}
		if(!root.has("children")) {
			throw new RuntimeException("Module \"" + file.getAbsolutePath() + "\" children 为空.");
		}

		obj = new Object[]{root, lastModified};
		buffer.put(pathKey, obj);
		return root;
	}

	public void clear(final String path) {
		final Set<Map.Entry<String, Object[]>> es = buffer.entrySet();
		final String delPath = StringUtil.concat(path, "/").toLowerCase();
		for (final Map.Entry<String, Object[]> e : es) {
			final String key = e.getKey();
			final String modulePath = StringUtil.concat(key, "/");
			if (modulePath.startsWith(delPath)) {
				final Object[] value = e.getValue();
				scriptBuffer.remove(key);
				buffer.remove(key);
			}
		}
	}

	public boolean canDisplay(final File file) {
		if(file.isDirectory()) {
			final File configFile = new File(file, "folder.json");
			if(configFile.exists()) {
				final JSONObject content = JsonUtil.readObject(configFile);
				if(Boolean.TRUE.equals(content.opt("hidden"))) {
					return false;
				}
				return true;
			}
		}else if(file.getName().endsWith(".xwl")) {
			return true;
		}

		return false;
	}

	@PostConstruct
	public void initialize() {
		this.buffer = new ConcurrentHashMap<>();
	}

}
