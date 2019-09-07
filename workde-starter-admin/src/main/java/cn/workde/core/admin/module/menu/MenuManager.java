package cn.workde.core.admin.module.menu;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author zhujingang
 * @date 2019/9/5 11:59 PM
 */
@Slf4j
public class MenuManager {

	private static final MenuManager me = new MenuManager();

	private MenuList menuGroups = new MenuList();

	private List<MenuItem> menuItems = new ArrayList<>();

	private MenuManager() { }

	public static MenuManager getInstance() {
		return me;
	}

	public List<MenuGroup> getMenuGroups() {
		menuGroups.sort(Comparator.comparingInt(MenuGroup::getOrder));
		return menuGroups;
	}

	public void addMenuItem(MenuItem menuItem) {
		this.menuItems.add(menuItem);
	}

	public List<MenuItem> getMenuItems() {
		return this.menuItems;
	}

	public void buildMenuItem() {
		menuItems.forEach(menuItem -> {
			for(MenuGroup menuGroup : menuGroups) {
				if(menuGroup.getId().equals(menuItem.getGroupId())) {
					menuGroup.getItems().add(menuItem);
					break;
				}
			}
		});
	}
}
