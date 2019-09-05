package cn.workde.core.admin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zhujingang
 * @date 2019/9/4 11:23 PM
 */
@Controller
public class IndexController {

	@RequestMapping("login")
	public ModelAndView login() {
		ModelAndView view = new ModelAndView("login.html");
		return view;
	}

}
