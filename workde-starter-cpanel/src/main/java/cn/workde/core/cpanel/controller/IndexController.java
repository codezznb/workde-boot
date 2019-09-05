package cn.workde.core.cpanel.controller;

import cn.workde.core.base.menu.MenuGroup;
import cn.workde.core.base.menu.MenuManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author zhujingang
 * @date 2019/9/5 4:10 PM
 */
@Slf4j
@Controller
@RequestMapping(value = "cpanel")
public class IndexController {

	@ModelAttribute(name = "systemMenuList", binding = false)
	public List<MenuGroup> getSystemMenuList() {
		return MenuManager.getInstance().getSystemMenus();
	}

	@ModelAttribute(name = "moduleMenuList", binding = false)
	public List<MenuGroup> getModuleMenuList() {
		System.out.println("come here");
		return MenuManager.getInstance().getModuleMenus();
	}

	@GetMapping
	public ModelAndView index() {
		log.info("come cpanel index page");
		return new ModelAndView("index.html");
	}
}
