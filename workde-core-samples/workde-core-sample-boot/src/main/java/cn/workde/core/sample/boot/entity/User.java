package cn.workde.core.sample.boot.entity;

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

	private String phone;
	private String password;

	private String regRemote;

	private LocalDateTime loginAt;

	private Integer loginCount;

	private String loginRemote;

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
