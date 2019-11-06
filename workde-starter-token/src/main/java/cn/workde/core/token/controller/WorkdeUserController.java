package cn.workde.core.token.controller;

import cn.workde.core.base.controller.WorkdeController;
import cn.workde.core.token.TokenUtil;
import cn.workde.core.token.UserInfo;

/**
 * @author zhujingang
 * @date 2019/9/1 12:48 PM
 */
public class WorkdeUserController extends WorkdeController {

	public UserInfo getUserInfo() {
		UserInfo userInfo = TokenUtil.getUserInfo();
		return userInfo;
	}

	public String getUserId() {
		UserInfo userInfo = getUserInfo();
		if(userInfo == null) {
			return null;
		}
		return userInfo.getId();
	}

}
