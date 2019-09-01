package cn.workde.core.sample.boot.service;

import cn.workde.core.sample.boot.entity.User;
import cn.workde.core.sample.boot.mapper.UserMapper;
import cn.workde.core.tk.base.BaseService;
import org.springframework.stereotype.Service;

/**
 * @author zhujingang
 * @date 2019/9/1 9:18 PM
 */
@Service
public class UserService extends BaseService<User, UserMapper> {
}
