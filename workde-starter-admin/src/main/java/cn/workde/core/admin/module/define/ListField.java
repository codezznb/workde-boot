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

	public ListField() { }

	public ListField(String fieldName, FieldDefine fieldDefine) {
		this.setName(fieldName);
		this.setLabel(fieldDefine.label());
		this.setWidth(fieldDefine.width());
		this.setType(fieldDefine.as());
		this.setFormat(fieldDefine.format());
	}

	/**
	 * 创建序号列
	 * @return
	 */
	public static ListField createCheckboxField() {
		ListField listField = new ListField();
		listField.setWidth("10%");
		listField.setType("checkbox");
		return listField;
	}

	/**
	 * 创建序号列
	 * @param label
	 * @return
	 */
	public static ListField createNumberField(String label) {
		ListField listField = new ListField();
		listField.setLabel(label);
		listField.setWidth("10%");
		listField.setType("numbers");
		return listField;
	}

}
