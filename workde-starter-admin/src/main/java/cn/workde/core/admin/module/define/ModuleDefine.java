package cn.workde.core.admin.module.define;

import cn.workde.core.admin.module.logic.BaseLogic;
import cn.workde.core.tk.base.BaseEntity;
import cn.workde.core.tk.base.BaseService;
import lombok.Data;

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
	 * 设置序号列名称
	 */
	private String listNumberTitle;

	/**
	 * 模块名
	 */
	private String moduleTitle;

	/**
	 * 列表页显示复选框
	 */
	private Boolean checkbox;
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

	/**
	 * 新增的时候只保存哪些字段
	 * null 或 空 表示全部
	 */
	private String createFields;

	/**
	 * 修改的时候只修改哪些字段
	 * null 或 空 表示全部
	 */
	private String updateFields;
}
