package cn.workde.core.base.utils;

import cn.workde.core.base.utils.jackson.JacksonObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhujingang
 * @date 2019/9/1 10:43 AM
 */
@Slf4j
public class JsonUtils {

	/**
	 * 将对象序列化成json字符串
	 *
	 * @param value javaBean
	 * @return jsonString json字符串
	 */
	public static <T> String toJson(T value) {
		try {
			return getInstance().writeValueAsString(value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	/**
	 * 将json反序列化成对象
	 *
	 * @param content   content
	 * @param valueType class
	 * @param <T>       T 泛型标记
	 * @return Bean
	 */
	public static <T> T parse(String content, Class<T> valueType) {
		try {
			return getInstance().readValue(content, valueType);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static ObjectMapper getInstance() {
		return JacksonHolder.INSTANCE;
	}

	private static class JacksonHolder {
		private static ObjectMapper INSTANCE = new JacksonObjectMapper();
	}

}
