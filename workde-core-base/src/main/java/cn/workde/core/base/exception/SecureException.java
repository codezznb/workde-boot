package cn.workde.core.base.exception;

import lombok.NoArgsConstructor;

/**
 * @author zhujingang
 * @date 2019/8/30 4:13 PM
 */
@NoArgsConstructor
public class SecureException extends RuntimeException{

	public SecureException(String msg) {
		super(msg);
	}
}
