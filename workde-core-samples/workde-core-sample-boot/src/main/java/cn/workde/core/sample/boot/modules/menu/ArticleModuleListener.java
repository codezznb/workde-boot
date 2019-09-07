package cn.workde.core.sample.boot.modules.menu;

import cn.workde.core.auto.service.AutoService;
import cn.workde.core.admin.module.ModuleListener;
import cn.workde.core.admin.module.menu.MenuGroup;

import java.util.List;


/**
 * @author zhujingang
 * @date 2019/9/6 12:56 AM
 */
@AutoService(ModuleListener.class)
public class ArticleModuleListener implements ModuleListener {

	public static final String MENU_GROUP_COMPANY = "company";
	public static final String MENU_GROUP_CONTENT = "content";
	public static final String MENU_GROUP_USER = "user";

	@Override
	public void onConfigMenuGroup(List<MenuGroup> menuGroupList) {
		MenuGroup companyMenuGroup = new MenuGroup();
		companyMenuGroup.setId(MENU_GROUP_COMPANY);
		companyMenuGroup.setText("企业");
		menuGroupList.add(companyMenuGroup);

		MenuGroup userMenuGroup = new MenuGroup();
		userMenuGroup.setId(MENU_GROUP_USER);
		userMenuGroup.setText("用户");
		menuGroupList.add(userMenuGroup);

		MenuGroup contentMenuGroup = new MenuGroup();
		contentMenuGroup.setId(MENU_GROUP_CONTENT);
		contentMenuGroup.setText("内容");
		menuGroupList.add(contentMenuGroup);
	}
}
