package cn.workde.core.sample.boot.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhujingang
 * @date 2019/9/1 10:00 AM
 */
@Data
public class User {

	private String username;
	private String password;

	private LocalDate registerAt;
	private LocalDateTime loginAt;


	public static User create() {
		User user = new User();
		user.setUsername("zhujingang");
		user.setPassword("123123");
		user.setRegisterAt(LocalDate.now());
		user.setLoginAt(LocalDateTime.now());
		return user;
	}
}
