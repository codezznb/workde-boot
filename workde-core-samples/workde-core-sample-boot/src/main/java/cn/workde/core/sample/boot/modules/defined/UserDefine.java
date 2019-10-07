package cn.workde.core.sample.boot.modules.defined;

import cn.workde.core.admin.module.define.ModuleDefine;
import cn.workde.core.sample.boot.entity.User;
import cn.workde.core.sample.boot.modules.logic.UserLogic;
import cn.workde.core.sample.boot.service.UserService;
import org.springframework.stereotype.Component;

/**
 * @author zhujingang
 * @date 2019/9/2 5:16 PM
 */
@Component
public class UserDefine extends ModuleDefine {

	public UserDefine() {
		this.setModuleTitle("用户");
		this.setModel(User.class);
		this.setBaseService(UserService.class);
		this.setModuleLogic(UserLogic.class);
	}
}
