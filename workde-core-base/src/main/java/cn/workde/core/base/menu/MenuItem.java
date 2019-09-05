package cn.workde.core.base.menu;

import cn.workde.core.base.menu.annotation.AdminMenu;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/9/5 11:45 PM
 */
@Data
public class MenuItem {

	private String id;
	private String text;
	private String icon;
	private String groupId;
	private String url;
	private int order = 100;
	private String target;

	public MenuItem(AdminMenu adminMenu) {
		this.setText(adminMenu.text());
		this.setIcon(adminMenu.icon());
		this.setGroupId(adminMenu.groupId());
		this.setOrder(adminMenu.order());
		this.setTarget(adminMenu.target());
	}

	public String getId() {
		return id != null ? id: text + "--" + url;
	}
}
