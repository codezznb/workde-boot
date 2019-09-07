package cn.workde.core.admin.module.menu;

import cn.workde.core.base.utils.ObjectUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhujingang
 * @date 2019/9/5 11:45 PM
 */
@Data
public class MenuGroup {

	private int order = 100;

	private String text;

	private String icon;

	private String id;

	private List<MenuItem> items = new ArrayList<>();

	public String getUrl() {
		if(ObjectUtils.isEmpty(getItems())) return "";
		return getItems().get(0).getUrl();
	}
}
