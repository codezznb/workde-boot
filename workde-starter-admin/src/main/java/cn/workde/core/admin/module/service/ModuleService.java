package cn.workde.core.admin.module.service;


import cn.workde.core.admin.module.ModuleMeta;
import cn.workde.core.admin.module.control.FormControl;
import cn.workde.core.admin.module.control.SwitchControl;
import cn.workde.core.admin.module.control.TextControl;
import cn.workde.core.admin.module.define.ListField;
import cn.workde.core.admin.module.define.ModuleDefine;
import cn.workde.core.admin.module.define.ModuleField;
import cn.workde.core.admin.web.annotation.FieldDefine;
import cn.workde.core.base.utils.StringUtils;
import cn.workde.core.tk.base.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author zhujingang
 * @date 2019/9/8 3:30 PM
 */
@Slf4j
public class ModuleService {

	public ModuleMeta getModuleMeta(ModuleDefine moduleDefine) {
		ModuleMeta moduleMeta = new ModuleMeta(moduleDefine);
		List<ModuleField> moduleFieldList = getModuleFieldList(moduleDefine);
		moduleMeta.setFields(getListFieldList(moduleDefine, moduleFieldList));
		return moduleMeta;
	}

	public ModuleMeta getModuleMeta(ModuleDefine moduleDefine, BaseEntity model) {
		ModuleMeta moduleMeta = new ModuleMeta(moduleDefine);
		List<ModuleField> moduleFieldList = getModuleFieldList(moduleDefine);
		moduleMeta.setNewFields(getNewFieldList(moduleDefine, moduleFieldList, model));
		moduleMeta.setEdtFields(getEdtFieldList(moduleDefine, moduleFieldList, model));
		return moduleMeta;
	}

	private List<ListField> getListFieldList(ModuleDefine moduleDefine, List<ModuleField> fieldDefineList) {
		List<ListField> listFields = new ArrayList<>();
		fieldDefineList.sort(Comparator.comparingInt(ModuleField::getOrder));
		for(ModuleField mfd : fieldDefineList) {
			FieldDefine fieldDefine = mfd.getFieldDefine();
			if(!fieldDefine.listEnable()) continue;
			ListField listField = new ListField(mfd.getName(), fieldDefine);
			listFields.add(listField);
		}
		return listFields;
	}

	public List<FormControl> getNewFieldList(ModuleDefine moduleDefine, List<ModuleField> fieldDefineList, BaseEntity model) {
		List<FormControl> formFields = new ArrayList<>();
		fieldDefineList.sort(Comparator.comparingInt(ModuleField::getNewOrder));
		for(ModuleField mfd : fieldDefineList) {
			FieldDefine fieldDefine = mfd.getFieldDefine();
			if(!fieldDefine.newEnabel()) continue;
			FormControl formControl = getFormControl(moduleDefine, mfd, fieldDefine, true, model);
			formFields.add(formControl);
		}
		return formFields;
	}

	public List<FormControl> getEdtFieldList(ModuleDefine moduleDefine, List<ModuleField> fieldDefineList, BaseEntity model) {
		List<FormControl> formFields = new ArrayList<>();
		fieldDefineList.sort(Comparator.comparingInt(ModuleField::getNewOrder));
		for(ModuleField mfd : fieldDefineList) {
			FieldDefine fieldDefine = mfd.getFieldDefine();
			if(!fieldDefine.edtEnable()) continue;
			FormControl formControl = getFormControl(moduleDefine, mfd, fieldDefine, false, model);
			formFields.add(formControl);
		}
		return formFields;
	}

	private FormControl getFormControl(ModuleDefine moduleDefine, ModuleField mfd, FieldDefine fieldDefine, Boolean isNew, BaseEntity model) {
		FormControl formControl = moduleDefine.getFormControl(mfd.getName(), fieldDefine, isNew);
		if(formControl == null) {
			if(fieldDefine.as().equals("text")) formControl = new TextControl();
			else if(fieldDefine.as().equals("switch")) formControl = new SwitchControl();
			formControl.init(mfd.getName(), fieldDefine);
		}
		Object value = getValue(model, mfd.getName());
		if(value != null) formControl.setValue(value);
		return formControl;
	}

	private List<ModuleField> getModuleFieldList(ModuleDefine moduleDefine) {
		Set<Field> fields = new LinkedHashSet<Field>();
		Class<?> clazz = moduleDefine.getModel();
		while (!clazz.equals(Object.class)) {
			Collections.addAll(fields, clazz.getDeclaredFields());
			clazz = clazz.getSuperclass();
		}

		List<ModuleField> fieldDefineList = new ArrayList<>();
		for (Field f : fields) {
			if(f.isAnnotationPresent(FieldDefine.class)) {
				ModuleField fieldDefine = new ModuleField(f);
				fieldDefineList.add(fieldDefine);
			}
		}

		return fieldDefineList;

	}

	private static Object getValue(Object obj, String name) {
		if(obj == null) return null;
		Method method = ReflectionUtils.findMethod(obj.getClass(), "get" + StringUtils.upperFirst(name));
		if(method != null) return ReflectionUtils.invokeMethod(method, obj);
		return null;
	}

}
