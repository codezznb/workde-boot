package cn.workde.core.boot.handler;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.workde.core.base.exception.AuthorizeException;
import cn.workde.core.base.exception.ForbiddenException;
import cn.workde.core.base.exception.LoginException;
import cn.workde.core.base.exception.ResultException;
import cn.workde.core.base.result.Result;
import cn.workde.core.base.utils.ExceptionUtils;
import cn.workde.core.base.utils.WebUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhujingang
 * @date 2019/8/29 9:23 PM
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 拦截所有未处理异常
	 * @param e 异常
	 * @return 结果
	 */

	@ExceptionHandler(Exception.class)
	public Result<?> exceptionHandler(Exception e) {
		e.printStackTrace();
		return Result.error(e.toString());
	}

	/**
	 * 异常结果处理
	 * @param e 结果异常
	 * @return 结果
	 */
	@ResponseBody
	@ExceptionHandler(ResultException.class)
	public synchronized Result<?> resultExceptionHandler(ResultException e) {
		var result = e.getResult();
		log.error(result.toString());
		ExceptionUtils.printException(e);
		return result;
	}

	/**
	 * {@linkplain Valid} 验证异常统一处理
	 * @param e 验证异常
	 * @return 结果
	 */
	@ResponseBody
	@ExceptionHandler(BindException.class)
	public Result<?> bindExceptionHandler(BindException e) {
		String uri = WebUtils.getRequest().getRequestURI();
		Console.error("uri={}", uri);
		List<ObjectError> errors = e.getAllErrors();
		JSONObject paramHint = new JSONObject();
		errors.forEach(error -> {
			String str = StrUtil.subAfter(error.getArguments()[0].toString(), "[", true);
			String key = str.substring(0, str.length() - 1);
			String msg = error.getDefaultMessage();
			paramHint.put(key, msg);
			Console.error(key + " " + msg);
		});

		return Result.param_check_not_pass(paramHint.toString());
	}

	/**
	 * 验证异常统一处理
	 * @param e 验证异常
	 * @return 结果
	 */
	@ResponseBody
	@ExceptionHandler(ValidateException.class)
	public Result<?> validateExceptionHandler(ValidateException e) {
		ExceptionUtils.printException(e);
		return Result.param_check_not_pass(e.getMessage());
	}

	/**
	 * 无权限异常访问处理
	 * @param e 无权限异常
	 * @return 结果
	 */
	@ResponseBody
	@ExceptionHandler(ForbiddenException.class)
	public Result<?> forbiddenExceptionHandler(ForbiddenException e) {
		ExceptionUtils.printException(e);
		return Result.forbidden();
	}

	/**
	 * 拦截登录异常（User）
	 * @param e 登录异常
	 * @return 结果
	 */
	@ResponseBody
	@ExceptionHandler(LoginException.class)
	public Result<?> loginExceptionHandler(LoginException e) {
		ExceptionUtils.printException(e);
		return Result.unauthorized();
	}

	/**
	 * 拦截登录异常（Admin）
	 * @param e 认证异常
	 */
	@ExceptionHandler(AuthorizeException.class)
	public Result<?> authorizeExceptionHandler(AuthorizeException e) {
		ExceptionUtils.printException(e);
		return Result.forbidden();
	}
}
