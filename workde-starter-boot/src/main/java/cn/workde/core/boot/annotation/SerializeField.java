package cn.workde.core.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhujingang
 * @date 2019/9/1 9:25 AM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeField {

	Class clazz() ;

	/**
	 * 需要返回的字段
	 * @return
	 */
	String includes() default "";

	/**
	 * 需要去除的字段
	 * @return
	 */
	String excludes() default "";
}
