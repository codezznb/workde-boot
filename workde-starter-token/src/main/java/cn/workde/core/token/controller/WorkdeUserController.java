package cn.workde.core.token.controller;

import cn.workde.core.token.TokenUtil;
import cn.workde.core.token.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhujingang
 * @date 2019/9/1 12:48 PM
 */
public class WorkdeUserController {

	/**
	 * ============================     REQUEST    =================================================
	 */

	@Autowired
	private HttpServletRequest request;

	/**
	 * 获取request
	 */
	public HttpServletRequest getRequest() {
		return this.request;
	}

	public UserInfo getUserInfo() {
		UserInfo userInfo = TokenUtil.getUserInfo();
		return userInfo;
	}

}
