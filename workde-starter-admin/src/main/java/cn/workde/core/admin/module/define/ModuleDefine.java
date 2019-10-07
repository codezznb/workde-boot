package cn.workde.core.admin.module.define;

import cn.workde.core.admin.module.control.FormControl;
import cn.workde.core.admin.module.logic.BaseLogic;
import cn.workde.core.admin.web.annotation.FieldDefine;
import cn.workde.core.tk.base.BaseEntity;
import cn.workde.core.tk.base.BaseService;
import lombok.Data;

import java.util.List;

/**
 * @author zhujingang
 * @date 2019/9/2 12:59 PM
 */
@Data
public class ModuleDefine {

	private Class<? extends BaseEntity> model;

	private Class<? extends BaseService> baseService;

	private Class<? extends BaseLogic> moduleLogic;

	/**
	 * 模块名
	 */
	private String moduleTitle;

	/**
	 * 表格尺寸
	 * sm 小尺寸 lg 大尺寸
	 */
	private String tableSize = "sm";
	/**
	 * 列表使用分页数据
	 * true 使用分页 false 返回all 数据
	 */
	private Boolean page = true;

	public FormControl getFormControl(String name, FieldDefine fieldDefine, Boolean isNew) {
		return null;
	}

	public Integer getFormGroups() { return 1; }

	public String getFormGroupName(Integer group) { return "基本信息"; }

}
