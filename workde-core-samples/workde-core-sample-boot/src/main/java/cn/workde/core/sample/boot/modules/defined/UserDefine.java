package cn.workde.core.sample.boot.modules.defined;

import cn.workde.core.admin.module.define.ModuleDefine;
import cn.workde.core.sample.boot.entity.User;
import cn.workde.core.sample.boot.modules.logic.UserLogic;
import cn.workde.core.sample.boot.service.UserService;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Map;

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
		this.setOrderBy("reg_at desc");
	}

	@Override
	public Example getExample(String keyword) {
		Example example = new Example(getModel());
		if(StringUtil.isNotEmpty(keyword)) {
			example.createCriteria().orLike("phone", "%"+keyword+"%");
		}
		if(StringUtil.isNotEmpty(getOrderBy())) example.setOrderByClause(getOrderBy());
		return example;
	}
}
