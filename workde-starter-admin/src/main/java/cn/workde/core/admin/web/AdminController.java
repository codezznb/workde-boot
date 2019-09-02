package cn.workde.core.admin.web;

import cn.workde.core.admin.define.ModuleDefine;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author zhujingang
 * @date 2019/9/2 4:55 PM
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface AdminController {

	String path();

	Class<? extends ModuleDefine> define();
}
