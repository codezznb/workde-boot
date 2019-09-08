package cn.workde.core.admin.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.workde.core.admin.module.ModuleMeta;
import cn.workde.core.admin.module.define.ModuleDefine;
import cn.workde.core.admin.module.logic.IModuleLogic;
import cn.workde.core.admin.module.service.ModuleService;
import cn.workde.core.admin.properties.WorkdeAdminProperties;
import cn.workde.core.admin.web.annotation.AdminController;
import cn.workde.core.auto.service.AutoService;
import cn.workde.core.base.controller.WorkdeController;
import cn.workde.core.base.result.Result;
import cn.workde.core.base.utils.JsonUtils;
import cn.workde.core.base.utils.PathUtils;
import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.tk.base.BaseEntity;
import cn.workde.core.tk.base.BaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zhujingang
 * @date 2019/9/2 5:18 PM
 */
@ResponseBody
public class ModuleController extends WorkdeController {

	@Autowired
	private ModuleService moduleService;
	@Autowired
	private WorkdeAdminProperties workdeAdminProperties;

	protected AdminController getAdminController(){
		AdminController adminController = this.getClass().getAnnotation(AdminController.class);
		return adminController;
	}

	protected ModuleDefine getModuleDefine() {
		AdminController adminController = getAdminController();
		return SpringUtils.getBean(adminController.define());
	}

	protected IModuleLogic getModuleLogic() {
		ModuleDefine moduleDefine = getModuleDefine();
		if(moduleDefine.getModuleLogic() != null) return SpringUtils.getBean(moduleDefine.getModuleLogic());
		return null;
	}

	protected BaseService getBaseService() {
		ModuleDefine moduleDefine = getModuleDefine();
		BaseService baseService = SpringUtils.getBean(moduleDefine.getBaseService());
		return baseService;
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView index() {
		AdminController adminController = getAdminController();
		String modulePath = PathUtils.normalizePath(workdeAdminProperties.getContextPath() + PathUtils.normalizePath(adminController.path()));
		ModelAndView mv = new ModelAndView("crud/index");
		mv.addObject("modulePath", modulePath);
		return mv;
	}

	@GetMapping(path = "meta")
	@ApiOperation(value = "模块结构")
	public Result<ModuleMeta> meta() {
		ModuleDefine moduleDefine = getModuleDefine();
		return Result.success(moduleService.getModuleMeta(moduleDefine));
	}

	@GetMapping(path = "list")
	@ApiOperation(value = "列表")
	public Result list(){
		BaseService baseService = getBaseService();
		baseService.page(getPageNum(), getPageSize());
		if(getModuleDefine().getPage()) {
			return Result.success(baseService.page(getPageNum(), getPageSize()));
		}
		return Result.success(baseService.all());
	}

	@GetMapping(path = "new_default")
	@ApiOperation(value = "默认值")
	public Result newDefault() {
		IModuleLogic moduleLogic = getModuleLogic();
		if(moduleLogic != null) return Result.success(moduleLogic.getNewDefultValue());
		return Result.success();
	}

	@PostMapping(path = "new")
	@ApiOperation(value = "保存")
	public Result create(@RequestBody String body){
		ModuleDefine moduleDefine = getModuleDefine();
		BaseEntity formModel = (BaseEntity) JsonUtils.parse(body, moduleDefine.getModel());
		IModuleLogic moduleLogic = getModuleLogic();
		if(moduleLogic != null) moduleLogic.beforeInsert(formModel);
		BaseService baseService = getBaseService();
		if(moduleLogic != null) moduleLogic.afterInsert(formModel);
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
		IModuleLogic moduleLogic = getModuleLogic();
		if(moduleLogic != null) moduleLogic.beforeUpdate(formModel);
		getBaseService().save(updateModel);
		if(moduleLogic != null) moduleLogic.afterUpdate(formModel);
		return Result.success();
	}

	@DeleteMapping(path = "{id}")
	@ApiOperation(value = "删除")
	public Result delete(@PathVariable("id") Long id){
		BaseEntity deleteModel = getBaseService().byId(id);
		IModuleLogic moduleLogic = getModuleLogic();
		if(moduleLogic != null) moduleLogic.beforeDelete(deleteModel);
		getBaseService().delete(deleteModel);
		if(moduleLogic != null) moduleLogic.afterDelete(deleteModel);
		return Result.success();

	}
}
