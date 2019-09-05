package cn.workde.core.sample.boot.modules.menu;

import cn.workde.core.auto.service.AutoService;
import cn.workde.core.cpanel.module.ModuleListener;
import cn.workde.core.base.menu.MenuGroup;

import java.util.List;


/**
 * @author zhujingang
 * @date 2019/9/6 12:56 AM
 */
@AutoService(ModuleListener.class)
public class ArticleModuleListener implements ModuleListener {


	@Override
	public void onConfigSystemMenu(List<MenuGroup> systemMenus) {

	}

	@Override
	public void onConfigAdminMenu(List<MenuGroup> adminMenus) {
		MenuGroup companyMenuGroup = new MenuGroup();
		companyMenuGroup.setId("company");
		companyMenuGroup.setText("企业");
		adminMenus.add(companyMenuGroup);

		MenuGroup contentMenuGroup = new MenuGroup();
		contentMenuGroup.setId("content");
		contentMenuGroup.setText("内容");
		adminMenus.add(contentMenuGroup);
	}
}
