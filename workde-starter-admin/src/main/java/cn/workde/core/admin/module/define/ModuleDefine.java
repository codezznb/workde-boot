package cn.workde.core.admin.module.define;

import cn.workde.core.admin.module.control.FormControl;
import cn.workde.core.admin.module.logic.BaseLogic;
import cn.workde.core.base.module.FieldDefine;
import cn.workde.core.base.utils.StringUtils;
import cn.workde.core.tk.base.BaseEntity;
import cn.workde.core.tk.base.BaseService;
import lombok.Data;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Map;

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
	 * 搜索的字段 多个用,号隔开
	 * 比如 phone,usenrame,password
	 */
	private String search;

	private String orderBy;

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

	/**
	 * 列表查询
	 * @param keyword
	 * @return
	 */
	public Example getExample(String keyword) {
		Example example = new Example(getModel());
		if(StringUtils.isNotEmpty(search)) {
			String[] columns = search.split(",");
			example.createCriteria();
			for(String column : columns) {

			}
		}
		if(StringUtils.isNotEmpty(getOrderBy())) example.setOrderByClause(getOrderBy());
		return example;
	}
}
