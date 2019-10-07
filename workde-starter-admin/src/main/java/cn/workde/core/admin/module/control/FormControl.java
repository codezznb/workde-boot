package cn.workde.core.admin.module.control;

import cn.workde.core.admin.module.constant.Inputs;
import cn.workde.core.admin.web.annotation.FieldDefine;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/9/30 1:50 PM
 */
@Data
public class FormControl extends Control {

	private String id;

	private Integer group;

	private String clazz;

	private String style;

	private String title;

	private Integer size;

	private Boolean required;

	private Boolean disabled;

	private String accesskey;

	private Integer tabindex;

	private String vld;

	private Integer maxlength;

	private Integer minlength;

	private String help;

	private String onclick;

	private String ondbclick;

	private String onmousedown;

	private String onmouseup;

	private String onmouseover;

	private String onmousemove;

	private String onmouseout;

	private String onfocus;

	private String onblur;

	private String onkeypress;

	private String onkeydown;

	private String onkeyup;

	private String onselect;

	private String onchange;

	private String value;

	public void init(String name, FieldDefine fieldDefine) {
		this.setId(name);
		this.setName(name);
		this.setGroup(fieldDefine.group());
		this.setLabel(fieldDefine.label());
		this.setRequired(fieldDefine.required());
		this.setHelp(fieldDefine.help());
	}

	public String getType() {
		return Inputs.DEFAULT;
	};

}
