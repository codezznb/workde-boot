package cn.workde.core.admin.module;

import cn.workde.core.admin.module.define.ListField;
import cn.workde.core.admin.module.define.ModuleDefine;
import lombok.Data;

import java.util.List;

/**
 * @author zhujingang
 * @date 2019/9/8 3:34 PM
 */
@Data
public class ModuleMeta {

	private String moduleName;

	private String moduleTitle;

	private String size;

	private Boolean page;

	private List<ListField> fields;


	public ModuleMeta(ModuleDefine moduleDefine) {
		this.setModuleName(moduleDefine.getModel().getSimpleName());
		this.setModuleTitle(moduleDefine.getModuleTitle());
		this.setSize(moduleDefine.getTableSize());
		this.setPage(moduleDefine.getPage());
	}
}
