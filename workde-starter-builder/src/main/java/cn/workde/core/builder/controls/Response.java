package cn.workde.core.builder.controls;

import cn.workde.core.builder.utils.WebUtil;

import java.util.Map;
import java.util.Set;

/**
 * @author zhujingang
 * @date 2019/9/20 11:00 PM
 */
public class Response extends Control {
	@Override
	public void create() throws Exception {
		final Set<Map.Entry<String, Object>> es = (Set<Map.Entry<String, Object>>)this.configs.toMap().entrySet();
		for (final Map.Entry<String, Object> entry : es) {
			final String key = entry.getKey();
			if (key.equals("itemId")) {
				continue;
			}
			WebUtil.send(this.response, WebUtil.replaceParams(this.request, entry.getValue().toString()));
		}
	}
}
