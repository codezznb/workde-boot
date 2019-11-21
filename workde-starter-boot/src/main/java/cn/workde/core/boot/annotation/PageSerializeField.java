package cn.workde.core.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhujingang
 * @date 2019/11/21 10:14 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageSerializeField {

	String includes() default "pageNum,pageSize,size,pages";
	
}
