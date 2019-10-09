package cn.workde.core.admin.module;

import cn.workde.core.admin.module.control.FormControl;
import cn.workde.core.admin.module.define.ListField;
import cn.workde.core.admin.module.define.ModuleDefine;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhujingang
 * @date 2019/9/8 3:34 PM
 */
@Data
public class ModuleMeta {

	private ModuleDefine moduleDefine;

	private String moduleName;

	private String moduleTitle;

	private String size;

	private Boolean page;

	private List<ListField> fields;

	private List<FormControl> newFields;

	private List<FormControl> edtFields;

	public ModuleMeta(ModuleDefine moduleDefine) {
		this.setModuleDefine(moduleDefine);
		this.setModuleName(moduleDefine.getModel().getSimpleName());
		this.setModuleTitle(moduleDefine.getModuleTitle());
		this.setPage(moduleDefine.getPage());
	}

	public Integer getGroupSize() {
		return moduleDefine.getFormGroups();
	}

	public String getGroupName(Integer groupIndex) {
		return moduleDefine.getFormGroupName(groupIndex);
	}

	public List<FormControl> getNewFormFields(Integer groupIndex) {
		if(getGroupSize() == 1) return newFields;
		else {
			List<FormControl> returnFields = newFields.stream().filter(field -> field.getGroup() == groupIndex).collect(Collectors.toList());
			return returnFields;
		}
	}

	public List<FormControl> getEdtFormFields(Integer groupIndex) {
		if(getGroupSize() == 1) return edtFields;
		else {
			List<FormControl> returnFields = edtFields.stream().filter(field -> field.getGroup() == groupIndex).collect(Collectors.toList());
			return returnFields;
		}
	}


}
