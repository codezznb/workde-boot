package cn.workde.core.sample.boot.entity;

import cn.workde.core.base.module.constant.AsType;
import cn.workde.core.base.module.FieldDefine;
import cn.workde.core.base.module.constant.DateFormat;
import cn.workde.core.tk.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name="yd_user")
public class User extends BaseEntity {

	public static final Integer STATE_LOCKED = 0;   //锁定状态
	public static final Integer STATE_NORMAL = 1;   //正常状态

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 手机号
	 */
	@FieldDefine(label = "手机号", required = true)
	private String phone;

	/**
	 * 密码盐
	 */
	@JsonIgnore
	@FieldDefine(label = "密码盐", required = true, readonly = true, listEnable = false)
	private String salt;

	/**
	 * 密码
	 */
	@JsonIgnore
	private String password;

	/**
	 * 密码修改时间
	 */
	@FieldDefine(label = "密码修改时间", as = AsType.READONLY, format = DateFormat.yyyyMMddHHmmss, listEnable = false)
	private LocalDateTime passwordAt;

	/**
	 * 注册时间
	 */
	@FieldDefine(label = "注册时间", group = 2, as = AsType.DATE, format = DateFormat.yyyyMMddHHmmss)
	private LocalDateTime regAt;

	/**
	 * 注册IP
	 */
	@FieldDefine(label = "注册IP", group = 2, as = AsType.READONLY)
	private String regRemote;

	/**
	 * 登录时间
	 */
	@FieldDefine(label = "登录时间", group = 2, as = AsType.DATE, format = DateFormat.yyyyMMddHHmmss)
	private LocalDateTime loginAt;

	/**
	 * 登录次数
	 */
	@FieldDefine(label = "登录次数", group = 2, as = AsType.READONLY)
	private Integer loginCount;

	/**
	 * 登录IP
	 */
	@FieldDefine(label = "登录IP", group = 2, as = AsType.READONLY)
	private String loginRemote;

	/**
	 * 状态
	 */
	@FieldDefine(label = "状态", as = AsType.SWTICH)
	private Integer state;

	/**
	 * 手机绑定极光id；
	 */
	private String pushId;

	/**
	 * 手机系统类型
	 */
	private String platform;
}
