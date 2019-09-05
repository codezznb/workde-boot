package cn.workde.core.cpanel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zhujingang
 * @date 2019/9/5 4:10 PM
 */
@Slf4j
@Controller
@RequestMapping(value = "cpanel/login")
public class LoginController {

	@GetMapping
	public ModelAndView login() {

		log.info("come login page");
		return new ModelAndView("login.html");
	}
}
