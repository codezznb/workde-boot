package cn.workde.core.sample.boot.modules.menu;

import cn.workde.core.admin.module.ModuleListener;
import cn.workde.core.auto.service.AutoService;
import cn.workde.core.admin.module.menu.MenuGroup;

import java.util.List;

/**
 * @author zhujingang
 * @date 2019/9/6 9:56 AM
 */
@AutoService(ModuleListener.class)
public class SysModuleListener implements ModuleListener {

	@Override
	public void onConfigMenuGroup(List<MenuGroup> menuGroupList) {

		MenuGroup systmeMenuGroup = new MenuGroup();
		systmeMenuGroup.setId("system");
		systmeMenuGroup.setText("系统");
		menuGroupList.add(systmeMenuGroup);
	}
}
