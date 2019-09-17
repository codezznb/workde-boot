package cn.workde.core.admin.module.define;

import cn.workde.core.admin.web.annotation.FieldDefine;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/9/8 10:42 PM
 */
@Data
public class ListField {



	private String field;

	private String title;

	private String width;

	private String type;

	private String templet;

	public ListField() { }

	public ListField(String fieldName, FieldDefine fieldDefine) {
		this.setField(fieldName);
		this.setTitle(fieldDefine.title());
		this.setWidth(fieldDefine.width());
		this.setType(fieldDefine.listType());
		this.setTemplet(fieldDefine.listTemplet());
	}

	/**
	 * 创建序号列
	 * @param title
	 * @return
	 */
	public static ListField createCheckboxField() {
		ListField listField = new ListField();
		listField.setType("checkbox");
		return listField;
	}

	/**
	 * 创建序号列
	 * @param title
	 * @return
	 */
	public static ListField createNumberField(String title) {
		ListField listField = new ListField();
		listField.setTitle(title);
		listField.setWidth("10%");
		listField.setType("numbers");
		return listField;
	}

}
