package cn.workde.core.sample.boot;

import cn.hutool.core.date.DateUtil;
import cn.workde.core.base.result.Result;
import cn.workde.core.base.utils.DateUtils;
import cn.workde.core.base.utils.StringUtils;
import cn.workde.core.boot.annotation.SerializeField;
import cn.workde.core.cache.constant.CacheConstant;
import cn.workde.core.cache.utils.CacheUtils;
import cn.workde.core.sample.boot.entity.User;
import cn.workde.core.sso.qiniu.service.IQiniuService;
import cn.workde.core.sso.qiniu.service.impl.QiniuServiceImpl;
import cn.workde.core.token.TokenUtil;
import cn.workde.core.token.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author zhujingang
 * @date 2019/8/28 10:24 PM
 */
@RestController
@RequestMapping("hello")
@Api(tags = "我的")
@Slf4j
public class HelloController {

	@Autowired
	private IQiniuService qiniuServiceImpl;

	@GetMapping(value = "cache")
	public String cache(String v) {
		String key = "dataScoe:v:";
		String result = CacheUtils.get(CacheConstant.HOUR, key, v, String.class);
		if(StringUtils.isEmpty(result)) {
			log.info("cache key {} is empty", key + v);
			result = DateUtil.formatDate(new Date());
			CacheUtils.put(CacheConstant.HOUR, key, v, result);
		}
		return result;
	}

    @GetMapping
    @ApiOperation(value = "首页")
    public String index() {
    	return qiniuServiceImpl.getUploadToken();
    }

    @GetMapping(value = "say")
	@ApiOperation(value = "说什么")
    public String say() {
        return "Good say";
    }

	@GetMapping(value = "profile_all")
	@ApiOperation(value = "个人信息")
    public Result<User> profileAll() {
    	log.info("hello#profileAll");
    	return Result.success(User.create());
	}

	@GetMapping(value = "profile")
	@ApiOperation(value = "个人信息")
	@SerializeField(clazz = User.class, includes = "username,login_at,register_at")
	public Result<User> profile() {
    	UserInfo userInfo = TokenUtil.getUserInfo();
		log.info("hello#profile {}", userInfo);
		return Result.success(User.create());
	}

}
