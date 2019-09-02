package cn.workde.core.admin.define;

import cn.workde.core.admin.logic.BaseLogic;
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
	 * 列表使用分页数据
	 * true 使用分页 false 返回all 数据
	 */
	private Boolean listPage = true;

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
