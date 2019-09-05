package cn.workde.core.cpanel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zhujingang
 * @date 2019/9/5 4:10 PM
 */
@Controller
@Slf4j
public class LoginController {

	@RequestMapping(value = "/cpanel/login", method = RequestMethod.GET)
	public ModelAndView login() {
		return new ModelAndView("login.html");
	}
}
