package cn.workde.core.sample.boot.entity;

import cn.workde.core.tk.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.Table;

/**
 * 登录记录
 * @author zhujingang
 * @date 2019/9/9 9:56 AM
 */
@Data
@Table(name = "yd_user_login")
public class UserLogin extends BaseEntity {

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 登录IP
	 */
	private String ip;

	/**
	 * 登录方式
	 */
	private String type;

	/**
	 * 平台 web版 手机版
	 */
	private String platform;

	/**
	 * 设备 手机 or 电脑
	 */
	private String device;

	/**
	 * 设备名称  MacOS android iphone
	 */
	private String deviceName;

	@Tolerate
	public UserLogin(String userId, String ip, String type, String device) {
		this.setUserId(userId);
		this.setIp(ip);
		this.setType(type);
		this.setDevice(device);
	}
}
