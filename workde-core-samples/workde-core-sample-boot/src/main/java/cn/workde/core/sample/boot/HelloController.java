package cn.workde.core.sample.boot;

import cn.workde.core.base.exception.LoginException;
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
    	throw new LoginException("请登录");
    	//return "Hello Workde";
    }

    @GetMapping(value = "说")
    public String say() {
        return "Good say";
    }
}
