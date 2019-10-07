package cn.workde.core.admin.module.define;

import cn.workde.core.admin.web.annotation.FieldDefine;
import cn.workde.core.base.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

/**
 * @author zhujingang
 * @date 2019/9/8 3:20 PM
 */
@Setter
@Getter
public class ModuleField {

	private String name;
	private FieldDefine fieldDefine;

	private int order;

	private int newOrder;

	private int edtOrder;

	public ModuleField(Field field) {
		FieldDefine fieldDefine = field.getDeclaredAnnotation(FieldDefine.class);
		this.setFieldDefine(fieldDefine);
		this.setName(field.getName());
		this.setOrder(fieldDefine.order());
	}

}
