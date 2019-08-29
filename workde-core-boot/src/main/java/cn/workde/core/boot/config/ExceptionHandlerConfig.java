package cn.workde.core.boot.config;

import cn.workde.core.boot.handler.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.annotation.PostConstruct;

/**
 * @author zhujingang
 * @date 2019/8/29 9:30 PM
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandlerConfig extends GlobalExceptionHandler {

	@PostConstruct
	private void init() {
		log.info("【初始化配置-全局统一异常处理】拦截所有Controller层异常，返回HTTP请求最外层对象 ... 已初始化完毕。");
	}
}
