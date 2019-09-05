package cn.workde.core.base.menu;

import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;

/**
 * @author zhujingang
 * @date 2019/9/5 11:59 PM
 */
@Slf4j
public class MenuManager {

	private static final MenuManager me = new MenuManager();

	private MenuList systemMenus = new MenuList();
	private MenuList moduleMenus = new MenuList();

	private MenuManager() { }

	public static MenuManager getInstance() {
		return me;
	}

	public List<MenuGroup> getSystemMenus() {
		systemMenus.sort(Comparator.comparingInt(MenuGroup::getOrder));
		return systemMenus;
	}

	public List<MenuGroup> getModuleMenus() {
		moduleMenus.sort(Comparator.comparingInt(MenuGroup::getOrder));
		return moduleMenus;
	}
}
