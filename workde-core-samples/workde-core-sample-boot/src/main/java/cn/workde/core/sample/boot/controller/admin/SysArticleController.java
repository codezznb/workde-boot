package cn.workde.core.sample.boot.controller.admin;

import cn.workde.core.admin.controller.ModuleController;
import cn.workde.core.admin.module.menu.annotation.AdminMenu;
import cn.workde.core.admin.web.annotation.AdminController;
import cn.workde.core.sample.boot.modules.defined.ArticleDefine;
import cn.workde.core.sample.boot.modules.menu.ArticleModuleListener;

/**
 * @author zhujingang
 * @date 2019/9/9 12:28 AM
 */
@AdminController(define = ArticleDefine.class, path = "sys/article", adminMenus = {
	@AdminMenu(groupId = ArticleModuleListener.MENU_GROUP_CONTENT, text = "内容管理", methodName = "index")
})
public class SysArticleController extends ModuleController {
}
