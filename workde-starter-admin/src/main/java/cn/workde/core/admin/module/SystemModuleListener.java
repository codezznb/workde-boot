package cn.workde.core.admin.module;

import cn.workde.core.auto.service.AutoService;
import cn.workde.core.admin.module.menu.MenuGroup;

import java.util.List;


/**
 * @author zhujingang
 * @date 2019/9/6 12:11 AM
 */
@AutoService(ModuleListener.class)
public class SystemModuleListener implements ModuleListener{

	public static final String MENU_GROUP_HOME = "HOME";

	@Override
	public void onConfigMenuGroup(List<MenuGroup> menuGroupList) {
		MenuGroup homeMenuGroup = new MenuGroup();
		homeMenuGroup.setId("HOME");
		homeMenuGroup.setText("首页");
		homeMenuGroup.setOrder(10);
		menuGroupList.add(homeMenuGroup);
	}

}
