package cn.workde.core.sample.boot.entity;

import cn.workde.core.admin.module.constant.Templets;
import cn.workde.core.admin.web.annotation.FieldDefine;
import cn.workde.core.tk.base.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author zhujingang
 * @date 2019/9/1 10:00 AM
 */
@Data
@Builder
@Table(name="yd_user")
public class User extends BaseEntity {

	@FieldDefine(label = "手机号", width = "15%")
	public String phone;

	@FieldDefine(label = "密码", listEnable = false)
	private String password;

	@FieldDefine(label = "注册IP")
	private String regRemote;

	@FieldDefine(label = "登录时间", width = "15%")
	private LocalDateTime loginAt;

	@FieldDefine(label = "登录次数")
	private Integer loginCount;

	@FieldDefine(label = "登录IP")
	private String loginRemote;

	@FieldDefine(label = "状态", listTemplet = Templets.TEMPLET_SWITCH_STATE)
	private Integer state;

	@Tolerate
	public User() {}

	public static User create() {
		User user = new User();
		user.setPassword("123123");
		user.setLoginAt(LocalDateTime.now());
		return user;
	}
}
