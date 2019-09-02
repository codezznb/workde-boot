package cn.workde.core.admin.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zhujingang
 * @date 2019/9/2 1:01 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({WorkdeAdminAutoConfiguration.class})
public @interface EnableWorkdeAdmin {

}
