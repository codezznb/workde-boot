package cn.workde.core.admin.web.annotation;

import java.lang.annotation.*;

/**
 * @author zhujingang
 * @date 2019/9/8 3:19 PM
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldDefine {

	String title();		//列表名称

	String width() default "10%";

	int order() default 100; //越小在越前面

	String listType() default "normal";

	/**
	 * table 自定义列模板
	 * @return
	 */
	String listTemplet() default "";

	String inputType() default "text";		//字段类型

	boolean listEnable() default true;		//列表是否显示该字段

	boolean newEnabel() default true;		//新建时是否显示该字段

	boolean edtEnable() default true;		//修改时是否显示该字段

	String newTitle() default "";		//新建时显示名称，默认等于列表名称

	String edtTitle() default "";		//编辑时显示名称，默认等于列表名称

	int newOrder() default -1;			//新建时排序，默认等于列表排序

	int edtOrder() default -1;			//编辑时排序，默认等于列表排序
}
