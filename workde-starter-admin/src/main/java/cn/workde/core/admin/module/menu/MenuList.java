package cn.workde.core.admin.module.menu;

import java.util.ArrayList;

/**
 * @author zhujingang
 * @date 2019/9/5 11:51 PM
 */
public class MenuList extends ArrayList<MenuGroup> {

	@Override
	public boolean add(MenuGroup menuGroup) {
		if(contains(menuGroup)) {
			throw new RuntimeException("Menu Group:" + menuGroup + " has exist.");
		}
		return super.add(menuGroup);
	}
}
