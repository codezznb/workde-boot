package cn.workde.core.admin.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.workde.core.admin.define.ModuleDefine;
import cn.workde.core.base.controller.WorkdeController;
import cn.workde.core.base.result.Result;
import cn.workde.core.base.utils.BeanUtils;
import cn.workde.core.base.utils.JsonUtils;
import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.tk.base.BaseEntity;
import cn.workde.core.tk.base.BaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhujingang
 * @date 2019/9/2 5:18 PM
 */
@ResponseBody
public class ModuleController extends WorkdeController {

	protected AdminController getAdminController(){
		AdminController adminController = this.getClass().getAnnotation(AdminController.class);
		return adminController;
	}

	protected ModuleDefine getModuleDefine() {
		AdminController adminController = getAdminController();
		return BeanUtils.newInstance(adminController.define());
	}

	protected BaseService getBaseService() {
		ModuleDefine moduleDefine = getModuleDefine();
		BaseService baseService = SpringUtils.getBean(moduleDefine.getBaseService());
		return baseService;
	}

	@GetMapping(path = "list")
	@ApiOperation(value = "列表")
	public Result list(){
		BaseService baseService = getBaseService();
		return Result.success(baseService.all());
	}

	@PostMapping(path = "new")
	@ApiOperation(value = "保存")
	public Result create(@RequestBody String body){
		ModuleDefine moduleDefine = getModuleDefine();
		BaseEntity formModel = (BaseEntity) JsonUtils.parse(body, moduleDefine.getModel());
		BaseService baseService = getBaseService();
		baseService.save(formModel);
		return Result.success();
	}

	@GetMapping(path = "{id}")
	@ApiOperation(value = "修改")
	public Result edit(@PathVariable("id") Long id){
		BaseEntity result = getBaseService().byId(id);
		return Result.success(result);
	}

	@PutMapping(path = "{id}")
	@ApiOperation(value = "更新")
	public Result update(@PathVariable("id") Long id, @RequestBody String body){
		BaseEntity updateModel = getBaseService().byId(id);
		BaseEntity formModel = (BaseEntity) JsonUtils.parse(body, getModuleDefine().getModel());
		BeanUtil.copyProperties(formModel, updateModel, CopyOptions.create().setIgnoreNullValue(true));
		getBaseService().save(updateModel);
		return Result.success();
	}

	@DeleteMapping(path = "{id}")
	@ApiOperation(value = "删除")
	public Result delete(@PathVariable("id") Long id){
		getBaseService().deleteById(id);
		return Result.success();

	}
}
