package cn.workde.core.builder.utils;

import cn.workde.core.base.utils.FileUtils;
import cn.workde.core.base.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * @author zhujingang
 * @date 2019/9/17 10:45 PM
 */
public class JsonUtil {

	public static JSONObject readObject(File file) {
		String text = FileUtils.readString(file, "UTF-8");
		if(text.isEmpty()) return new JSONObject();
		try {
			return new JSONObject(text);
		}catch (Throwable e) {
			throw new JSONException("错误的JSON格式: " + StringUtils.sub(text, 0, 50));
		}
	}

	public static String join(final JSONArray array, final String separator) {
		final int j = array.length();
		if (j == 0) {
			return "";
		}
		final StringBuilder buf = new StringBuilder();
		for (int i = 0; i < j; ++i) {
			if (i > 0) {
				buf.append(separator);
			}
			buf.append(array.getString(i));
		}
		return buf.toString();
	}

	public static JSONObject findObject(final JSONObject jo, final String itemsKey, final String key, final String value) {
		if (jo.optString(key).equals(value)) {
			return jo;
		}
		final JSONArray ja = jo.optJSONArray(itemsKey);
		if (ja != null) {
			for (int j = ja.length(), i = 0; i < j; ++i) {
				final JSONObject item = ja.optJSONObject(i);
				if (item != null) {
					final JSONObject result = findObject(item, itemsKey, key, value);
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}

	public static boolean addAll(final JSONArray dest, final JSONArray source) {
		final int j = source.length();
		for (int i = 0; i < j; ++i) {
			dest.put(source.get(i));
		}
		return j != 0;
	}
}
