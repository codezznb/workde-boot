package cn.workde.core.admin.module;

import cn.workde.core.admin.module.menu.MenuGroup;
import org.springframework.core.Ordered;

import java.util.List;


/**
 * Module 监听器
 * @author zhujingang
 * @date 2019/9/5 11:56 PM
 */
public interface ModuleListener extends Ordered, Comparable<ModuleListener>{


	/**
	 * 配置顶部导航菜单
	 *
	 */
	default void onConfigMenuGroup(List<MenuGroup> menuGroupList) {};

	/**
	 * 获取排列顺序
	 *
	 * @return order
	 */
	@Override
	default int getOrder() {
		return 0;
	}

	/**
	 * 对比排序
	 *
	 * @param o LauncherService
	 * @return compare
	 */
	@Override
	default int compareTo(ModuleListener o) {
		return Integer.compare(this.getOrder(), o.getOrder());
	}
}
