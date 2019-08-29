package cn.workde.core.base.exception;

import cn.workde.core.base.result.Result;
import lombok.Getter;

/**
 * @author zhujingang
 * @date 2019/8/28 6:10 PM
 */
@Getter
public class ResultException extends RuntimeException{

	Result<?> result;

	public <T> ResultException(String msg) {
		this.result = Result.dev_defined(msg);
	}

	public <T> ResultException(Result<T> result) {
		this.result = result;
	}
}
