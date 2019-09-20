package cn.workde.core.builder.utils;

import cn.workde.core.base.utils.FileUtils;
import cn.workde.core.base.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhujingang
 * @date 2019/9/17 10:45 PM
 */
public class JsonUtil {

	public static JSONObject fromCSV(final String text) {
		final JSONObject jo = new JSONObject();
		if (StringUtil.isEmpty(text)) {
			return jo;
		}
		final String[] items = StringUtil.split(text, ',', true);
		String[] array;
		for (int length = (array = items).length, i = 0; i < length; ++i) {
			final String item = array[i];
			jo.put(item, true);
		}
		return jo;
	}

	public static JSONObject readObject(File file) {
		String text = FileUtils.readString(file, "UTF-8");
		if(text.isEmpty()) return new JSONObject();
		try {
			return new JSONObject(text);
		}catch (Throwable e) {
			throw new JSONException("错误的JSON格式: " + StringUtils.sub(text, 0, 50));
		}
	}

	public static JSONArray add(JSONArray ja, int index, Object value) {
		List<Object> list = ja.toList();
		list.add(index, value);
		return new JSONArray(list);
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

	public static JSONObject apply(final JSONObject source, final JSONObject dest) {
		final Set<Map.Entry<String, Object>> es = (Set<Map.Entry<String, Object>>)dest.toMap().entrySet();
		for (final Map.Entry<String, Object> e : es) {
			source.put((String)e.getKey(), e.getValue());
		}
		return source;
	}

	public static JSONObject applyIf(final JSONObject source, final JSONObject dest) {
		final Set<Map.Entry<String, Object>> es = (Set<Map.Entry<String, Object>>)dest.toMap().entrySet();
		for (final Map.Entry<String, Object> e : es) {
			final String key = e.getKey();
			if (!source.has(key)) {
				source.put(key, e.getValue());
			}
		}
		return source;
	}

	public static JSONArray getArray(final Object value) {
		if (value instanceof JSONArray) {
			return (JSONArray)value;
		}
		final String stringValue = (value == null) ? null : value.toString();
		if (StringUtil.isEmpty(stringValue)) {
			return null;
		}
		return new JSONArray(stringValue);
	}

	public static JSONObject getObject(final Object value) {
		if (value instanceof JSONObject) {
			return (JSONObject)value;
		}
		final String stringValue = (value == null) ? null : value.toString();
		if (StringUtil.isEmpty(stringValue)) {
			return null;
		}
		return new JSONObject(stringValue);
	}

	public static JSONArray toArray(final Object value) {
		if (value == null || value instanceof JSONArray) {
			return (JSONArray)value;
		}
		return new JSONArray().put(value);
	}

	public static Object opt(final JSONObject jo, final String key) {
		if (jo.isNull(key)) {
			return null;
		}
		return jo.opt(key);
	}

	public static Object opt(final JSONArray ja, final int index) {
		if (ja.isNull(index)) {
			return null;
		}
		return ja.opt(index);
	}

	public static JSONObject clone(final JSONObject source) {
		return new JSONObject(source.toString());
	}

	public static JSONArray clone(final JSONArray source) {
		return new JSONArray(source.toString());
	}
}
