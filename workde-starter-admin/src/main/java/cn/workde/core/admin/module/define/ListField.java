package cn.workde.core.admin.module.define;

import cn.workde.core.base.module.FieldDefine;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/9/8 10:42 PM
 */
@Data
public class ListField {

	private String name;

	private String label;

	private String width;

	private String type;

	private String format;

	private ModuleDefine moduleDefine;

	public ListField() { }

	public ListField(String fieldName, FieldDefine fieldDefine, ModuleDefine moduleDefine) {
		this.setName(fieldName);
		this.setLabel(fieldDefine.label());
		this.setWidth(fieldDefine.width());
		this.setType(fieldDefine.as());
		this.setFormat(fieldDefine.format());
		this.setModuleDefine(moduleDefine);
	}

	public Object getValue(Object value) {
		return moduleDefine.getListFieldValue(this, value);
	}

}
