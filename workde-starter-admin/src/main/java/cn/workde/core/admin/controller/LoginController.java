package cn.workde.core.admin.controller;

import cn.workde.core.admin.web.annotation.AdminController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zhujingang
 * @date 2019/9/5 4:10 PM
 */
@Slf4j
@AdminController(path = "login")
public class LoginController {

	@GetMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView login() {
		log.info("come login page");
		return new ModelAndView("login");
	}
}
