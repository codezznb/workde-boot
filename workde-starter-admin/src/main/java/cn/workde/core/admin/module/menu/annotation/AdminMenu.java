package cn.workde.core.admin.module.menu.annotation;

import java.lang.annotation.*;

/**
 * 用来给Controller的方法进行标注，申明此方法为一个后台菜单
 * 后台菜单被包含在 group里，而 group 是由module来定义的
 * <p>
 * groupId 用来标识 这个方法被放在哪个group里
 * @author zhujingang
 * @date 2019/9/5 11:47 PM
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AdminMenu {

	String groupId();

	String text();

	String url() default "";

	String icon() default "layui-icon-list";

	String target() default "";

	int order() default 100; //越小在越前面

	String methodName() default "";		//对应CRUD方法名
}
