package cn.workde.core.admin.controller;

import cn.workde.core.admin.web.annotation.AdminController;
import cn.workde.core.admin.module.menu.MenuGroup;
import cn.workde.core.admin.module.menu.MenuManager;
import cn.workde.core.base.controller.WorkdeController;
import cn.workde.core.base.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author zhujingang
 * @date 2019/9/5 4:10 PM
 */
@Slf4j
@AdminController(path = "cpanel")
public class IndexController extends WorkdeController {

	@GetMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView index() {
		return new ModelAndView("cpanel/index");
	}

	@GetMapping(value = "top", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView menu() {
		ModelAndView mv = new ModelAndView("cpanel/top");
		mv.addObject("menuGroupList", MenuManager.getInstance().getMenuGroups());
		return mv;
	}

	@GetMapping(value = "left", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView left(String group_id) {
		ModelAndView mv = new ModelAndView("cpanel/left");
		List<MenuGroup> menuGroupList = MenuManager.getInstance().getMenuGroups();
		MenuGroup menuGroup = menuGroupList.get(0);
		if(StringUtils.isNotEmpty(group_id)) {
			for(MenuGroup group : menuGroupList) {
				if(group.getId().equals(group_id)) {
					menuGroup = group;
					break;
				}
			}
		}
		if(menuGroup != null) mv.addObject("menuGroup", menuGroup);
		System.out.println(menuGroup);
		return mv;
	}

	@GetMapping(value = "main", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView main() {
		return new ModelAndView("cpanel/main");
	}
}
