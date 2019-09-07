package cn.workde.core.sample.boot.controller.admin;


import cn.workde.core.admin.module.menu.annotation.AdminMenu;
import cn.workde.core.admin.web.AdminController;
import cn.workde.core.admin.controller.ModuleController;
import cn.workde.core.sample.boot.modules.defined.UserDefine;
import cn.workde.core.sample.boot.modules.menu.ArticleModuleListener;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zhujingang
 * @date 2019/9/2 5:15 PM
 */
@AdminController(define = UserDefine.class, path = "sys/users")
@Api(tags = "用户管理")
public class SysUserController extends ModuleController {

	@GetMapping(value = "hello", produces = MediaType.TEXT_HTML_VALUE)
	@AdminMenu(groupId = ArticleModuleListener.MENU_GROUP_COMPANY, text = "公司管理")
	public String hello() {
		return "企业管理";
	}

}
