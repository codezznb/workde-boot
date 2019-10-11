package cn.workde.core.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.workde.core.admin.module.ModuleMeta;
import cn.workde.core.admin.module.define.ModuleDefine;
import cn.workde.core.admin.module.logic.IModuleLogic;
import cn.workde.core.admin.module.service.ModuleService;
import cn.workde.core.admin.properties.WorkdeAdminProperties;
import cn.workde.core.admin.web.annotation.AdminController;
import cn.workde.core.base.result.Kv;
import cn.workde.core.base.result.Result;
import cn.workde.core.base.utils.JsonUtils;
import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.tk.base.BaseEntity;
import cn.workde.core.tk.base.BaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileNotFoundException;

/**
 * @author zhujingang
 * @date 2019/9/2 5:18 PM
 */
@ResponseBody
public class ModuleController extends WorkdeAdminController {

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

	protected String getControllerName() {
		String name = this.getClass().getSimpleName();
		if(name.endsWith("Controller")) name = name.substring(0,name.indexOf("Controller"));
		return name;
	}

	protected String getTemplateFileName(String name) {
		String controllerName = getControllerName();
		String fileName = controllerName + "/" + name;
		try {
			ResourceUtils.getFile("classpath:templates/" + fileName + ".html");
			return fileName;
		} catch (FileNotFoundException e) {
			return "crud/" + name;
		}
	}

	@GetMapping(path = "list")
	@ApiOperation(value = "列表")
	public ModelAndView list(String keyword){
		getControllerName();
		ModuleDefine moduleDefine = getModuleDefine();
		ModuleMeta moduleMeta = moduleService.getModuleMeta(moduleDefine);
		BaseService baseService = getBaseService();
		String fileName = getTemplateFileName("list");
		ModelAndView mv = new ModelAndView(fileName);
		mv.addObject("mm", moduleMeta);
		if(moduleDefine.getPage()) {
			mv.addObject("value", baseService.page(moduleDefine.getExample(keyword), getPageNum(), getPageSize()));
		}else {
			mv.addObject("value", baseService.list(moduleDefine.getExample(keyword)));
		}
		mv.addObject("keyword", keyword);
		return mv;
	}

	@RequestMapping(value = "new", method = RequestMethod.GET)
	public ModelAndView create() {
		IModuleLogic moduleLogic = getModuleLogic();
		BaseEntity model = null;
		if(moduleLogic != null) model = (BaseEntity) moduleLogic.getNewDefultValue();
		ModuleDefine moduleDefine = getModuleDefine();
		ModuleMeta moduleMeta = moduleService.getModuleMeta(moduleDefine, model);
		ModelAndView mv = new ModelAndView("crud/create");
		mv.addObject("mm", moduleMeta);
		return mv;
	}

	@PostMapping(path = "new")
	@ApiOperation(value = "保存")
	public Result save(@RequestBody String body){
		ModuleDefine moduleDefine = getModuleDefine();
		BaseEntity formModel = JsonUtils.parse(body, moduleDefine.getModel());
		IModuleLogic moduleLogic = getModuleLogic();
		if(moduleLogic != null) moduleLogic.beforeInsert(formModel);
		BaseService baseService = getBaseService();
		if(moduleLogic != null) moduleLogic.afterInsert(formModel);
		baseService.save(formModel);
		return Result.success();
	}

	@GetMapping(path = "{id}")
	@ApiOperation(value = "修改")
	public ModelAndView edit(@PathVariable("id") String id){
		BaseEntity model = getBaseService().byId(id);
		ModuleDefine moduleDefine = getModuleDefine();
		ModuleMeta moduleMeta = moduleService.getModuleMeta(moduleDefine, model);
		ModelAndView mv = new ModelAndView("crud/edit");
		mv.addObject("mm", moduleMeta);
		mv.addObject("model", model);
		return mv;
	}

	@PostMapping(path = "{id}")
	@ApiOperation(value = "更新")
	public Result update(@PathVariable("id") String id, @RequestBody String body){
		BaseEntity updateModel = getBaseService().byId(id);
		BaseEntity formModel = JsonUtils.parse(body, getModuleDefine().getModel());
		BeanUtil.copyProperties(formModel, updateModel, CopyOptions.create().setIgnoreNullValue(true));
		IModuleLogic moduleLogic = getModuleLogic();
		if(moduleLogic != null) moduleLogic.beforeUpdate(formModel);
		getBaseService().save(updateModel);
		if(moduleLogic != null) moduleLogic.afterUpdate(formModel);
		return Result.success();
	}

	@DeleteMapping(path = "{id}")
	@ApiOperation(value = "删除")
	public void delete(@PathVariable("id") String id){
		BaseEntity deleteModel = getBaseService().byId(id);
		IModuleLogic moduleLogic = getModuleLogic();
		if(moduleLogic != null) moduleLogic.beforeDelete(deleteModel);
		getBaseService().delete(deleteModel);
		if(moduleLogic != null) moduleLogic.afterDelete(deleteModel);
		renderScript("crud/delete", Kv.create().set("id", id));
	}
}
