package cn.workde.core.sample.boot;

import cn.workde.core.base.result.Result;
import cn.workde.core.boot.annotation.SerializeField;
import cn.workde.core.sample.boot.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhujingang
 * @date 2019/8/28 10:24 PM
 */
@RestController
@RequestMapping("hello")
@Api(tags = "我的")
public class HelloController {

    @GetMapping
    @ApiOperation(value = "首页")
    public String index() {
    	return "Hello Workde";
    }

    @GetMapping(value = "say")
	@ApiOperation(value = "说什么")
    public String say() {
        return "Good say";
    }

	@GetMapping(value = "profile_all")
	@ApiOperation(value = "个人信息")
    public Result<User> profileAll() {
    	return Result.success(User.create());
	}

	@GetMapping(value = "profile")
	@ApiOperation(value = "个人信息")
	@SerializeField(clazz = User.class, includes = "username,loginAt,registerAt")
	public Result<User> profile() {
		return Result.success(User.create());
	}
}
