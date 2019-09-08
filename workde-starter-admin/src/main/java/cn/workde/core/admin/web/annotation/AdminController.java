package cn.workde.core.admin.web.annotation;

import cn.workde.core.admin.module.define.ModuleDefine;
import cn.workde.core.admin.module.define.NullDefine;
import cn.workde.core.admin.module.menu.annotation.AdminMenu;
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

	Class<? extends ModuleDefine> define() default NullDefine.class;

	AdminMenu[] adminMenus() default {};

}
