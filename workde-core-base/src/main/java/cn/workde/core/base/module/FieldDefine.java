package cn.workde.core.base.module;


import cn.workde.core.base.module.constant.DateFormat;

import java.lang.annotation.*;

/**
 * @author zhujingang
 * @date 2019/9/8 3:19 PM
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldDefine {

	String label();		//列表名称

	String width() default "10%";

	String as() default "text";

	boolean listEnable() default true;		//列表是否显示该字段

	boolean newEnabel() default true;		//新建时是否显示该字段

	boolean edtEnable() default true;		//修改时是否显示该字段

	// 是否必填
	boolean required() default false;

	// 提示
	String help() default "";

	int group() default 1;

	int order() default 100; //越小在越前面

	boolean readonly() default false;

	// 格式化
	String format() default "";
}
