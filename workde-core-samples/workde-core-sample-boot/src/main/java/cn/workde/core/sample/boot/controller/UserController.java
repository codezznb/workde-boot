package cn.workde.core.sample.boot.controller;

import cn.workde.core.admin.module.menu.annotation.AdminMenu;
import cn.workde.core.base.controller.WorkdeController;
import cn.workde.core.base.result.Result;
import cn.workde.core.boot.annotation.SerializeField;
import cn.workde.core.sample.boot.entity.User;
import cn.workde.core.sample.boot.modules.menu.ArticleModuleListener;
import cn.workde.core.sample.boot.service.UserService;
import com.github.pagehelper.PageInfo;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhujingang
 * @date 2019/8/28 10:24 PM
 */
@RestController
@RequestMapping("/admin/user")
@Api(tags = "用户")
@AllArgsConstructor
public class UserController extends WorkdeController {

	private UserService userService;

	@GetMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
	@AdminMenu(groupId = ArticleModuleListener.MENU_GROUP_USER, text = "注册用户")
	public Result index() {
		return Result.success();
	}

	@GetMapping(value = "list")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "phone", value = "手机号", paramType = "query", dataType = "string"),
		@ApiImplicitParam(name = "state", value = "状态", paramType = "query", dataType = "integer")
	})
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "列表", notes = "传入user")
	@SerializeField(clazz = User.class, includes = "id,phone,reg_remote,login_at,login_count,login_remote,state")
	public Result<List<User>> list() {
		return Result.success(userService.list(getPermitParams("phone", "state"), "id desc"));
	}


	@GetMapping(value = "page")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "pageNum", value = "页码", paramType = "query", dataType = "int"),
		@ApiImplicitParam(name = "pageSize", value = "每页数", paramType = "query", dataType = "int"),
		@ApiImplicitParam(name = "phone", value = "手机号", paramType = "query", dataType = "string"),
		@ApiImplicitParam(name = "state", value = "状态", paramType = "query", dataType = "int")
	})
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入user")
	@SerializeField(clazz = User.class, includes = "id,phone,reg_remote,login_at,login_count,login_remote,state")
	public Result<List<User>> page(Integer pageNum, Integer pageSize) {
		PageInfo<User> pageInfo = userService.page(getPermitParams("phone", "state"), "id desc", getPageNum(), getPageSize());
		return Result.success(pageInfo.getList(), pageInfo.getTotal());

	}


}
