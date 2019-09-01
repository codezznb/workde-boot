package cn.workde.core.boot.json;

import cn.workde.core.base.utils.ObjectUtils;
import cn.workde.core.base.utils.jackson.JacksonObjectMapper;
import cn.workde.core.boot.annotation.SerializeField;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author zhujingang
 * @date 2019/9/1 9:29 AM
 */
public class JsonSerializer {

	private ObjectMapper mapper = new JacksonObjectMapper();

	JacksonJsonFilter jacksonFilter = new JacksonJsonFilter();

	/**
	 * @param clazz target type
	 * @param include include fields
	 * @param filter filter fields
	 */
	public void filter(Class<?> clazz, String include, String filter) {
		if (clazz == null) return;
		if (!ObjectUtils.isEmpty(include)) {
			jacksonFilter.include(clazz, include.split(","));
		}
		if (!ObjectUtils.isEmpty(filter)) {
			jacksonFilter.filter(clazz, filter.split(","));
		}
		mapper.addMixIn(clazz, jacksonFilter.getClass());
	}

	public String toJson(Object object) throws JsonProcessingException {

		mapper.setFilterProvider(jacksonFilter);
		return mapper.writeValueAsString(object);
	}

	public void filter(SerializeField serializeField) {
		this.filter(serializeField.clazz(), serializeField.includes(), serializeField.excludes());
	}
}
