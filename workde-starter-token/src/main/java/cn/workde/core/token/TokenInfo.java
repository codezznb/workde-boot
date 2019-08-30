package cn.workde.core.token;

import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/8/30 5:04 PM
 */
@Data
public class TokenInfo {

	/**
	 * 令牌值
	 */
	private String token;

	/**
	 * 过期秒数
	 */
	private int expire;
}
