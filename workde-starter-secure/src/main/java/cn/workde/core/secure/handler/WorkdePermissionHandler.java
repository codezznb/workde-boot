package cn.workde.core.secure.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhujingang
 * @date 2019/8/29 9:57 PM
 */
@AllArgsConstructor
@Slf4j
public class WorkdePermissionHandler implements IPermissionHandler{

	private static final String SCOPE_CACHE_CODE = "apiScope:code:";

	@Override
	public boolean permissionAll() {
		return false;
	}

	@Override
	public boolean hasPermission(String permission) {
		return false;
	}
}
