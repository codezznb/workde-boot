package cn.workde.core.sample.boot.modules.logic;

import cn.workde.core.admin.module.logic.BaseLogic;
import cn.workde.core.base.exception.BusinessException;
import cn.workde.core.sample.boot.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author zhujingang
 * @date 2019/9/2 11:11 PM
 */
@Component
public class UserLogic extends BaseLogic<User> {

	public boolean beforeUpdate(User updated, String errorMessage) {
		System.out.println("come before updated");
		System.out.println(updated.getPhone());
		errorMessage = "您不能修改当前数据";
		throw new BusinessException(errorMessage);
		//return false;
	}
}
