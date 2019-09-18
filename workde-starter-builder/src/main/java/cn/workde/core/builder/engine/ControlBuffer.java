package cn.workde.core.builder.engine;

import cn.workde.core.builder.utils.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhujingang
 * @date 2019/9/18 8:54 AM
 */
public class ControlBuffer {

	private ConcurrentHashMap<String, JSONObject> buffer;
	private File file;

	public JSONObject get(String id) throws IOException{
		final JSONObject object = buffer.get(id);
		if(object == null) throw new IOException("控件 \"" + id + "\" 没有找到.");
		return object;
	}

	public synchronized void saveToBuffer(final JSONObject json) {
		final JSONArray ja = json.getJSONArray("children");
		for(int j = ja.length(), i = 0; i < j; i++ ) {
			final JSONObject jo = ja.getJSONObject(i);
			if(jo.optBoolean("leaf")) {
				buffer.put(jo.getString("id"), jo);
			}else {
				saveToBuffer(jo);
			}
		}
	}

	@PostConstruct
	public void initialize() {
		this.buffer = new ConcurrentHashMap<>();
		this.file = Builder.getInstance().getControlFile();
		this.saveToBuffer(JsonUtil.readObject(this.file));
	}
}
