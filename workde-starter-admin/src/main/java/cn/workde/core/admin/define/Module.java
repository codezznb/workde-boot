package cn.workde.core.admin.define;

import cn.workde.core.tk.base.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * @author zhujingang
 * @date 2019/9/2 12:58 PM
 */
@Data
public class Module {

	private String moduleName;
	private String title;

	private Boolean pageList;

	private List<Button> buttons;


	public static Module getModule(Class<BaseEntity> model) {
		Module module = new Module();
		module.moduleName = model.getName();
		return module;
	}

}
