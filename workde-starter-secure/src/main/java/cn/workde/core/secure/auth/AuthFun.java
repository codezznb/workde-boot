package cn.workde.core.secure.auth;

import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.secure.handler.IPermissionHandler;

/**
 * 权限判断
 * @author zhujingang
 * @date 2019/8/29 9:54 PM
 */
public class AuthFun {

	/**
	 * 权限校验处理器
	 */
	private static IPermissionHandler permissionHandler;

	static {
		permissionHandler = SpringUtils.getBean(IPermissionHandler.class);
	}

	/**
	 * 判断角色是否具有接口权限
	 *
	 * @return {boolean}
	 */
	public boolean permissionAll() {
		return permissionHandler.permissionAll();
	}

	/**
	 * 判断角色是否具有接口权限
	 *
	 * @param permission 权限编号
	 * @return {boolean}
	 */
	public boolean hasPermission(String permission) {
		return permissionHandler.hasPermission(permission);
	}

	/**
	 * 放行所有请求
	 *
	 * @return {boolean}
	 */
	public boolean permitAll() {
		return true;
	}
}
