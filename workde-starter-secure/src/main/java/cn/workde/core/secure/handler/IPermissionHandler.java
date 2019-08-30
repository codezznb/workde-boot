package cn.workde.core.secure.handler;

/**
 * 权限校验通用接口
 * @author zhujingang
 * @date 2019/8/29 9:56 PM
 */
public interface IPermissionHandler {

	/**
	 * 判断角色是否具有接口权限
	 *
	 * @return {boolean}
	 */
	boolean permissionAll();

	/**
	 * 判断角色是否具有接口权限
	 *
	 * @param permission 权限编号
	 * @return {boolean}
	 */
	boolean hasPermission(String permission);

}
