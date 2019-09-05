package cn.workde.core.sample.boot.controller.admin;

import cn.workde.core.admin.web.AdminController;
import cn.workde.core.admin.web.ModuleController;
import cn.workde.core.base.result.Result;
import cn.workde.core.sample.boot.modules.defined.UserDefine;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhujingang
 * @date 2019/9/2 5:15 PM
 */
@AdminController(define = UserDefine.class, path = "sys/users")
@Api(tags = "用户管理")
public class SysUserController extends ModuleController {

	@GetMapping(value = "hello")
	public Result<String> hello() {
		return Result.success("hello body");
	}
//	@GetMapping(value = "cpanel/login")
//	public ModelAndView userHello(){
//		return new ModelAndView("login.html");
//	}
//
//	@GetMapping(value = "cpanel")
//	public ModelAndView index() {
//		return new ModelAndView("index.html");
//	}
}
