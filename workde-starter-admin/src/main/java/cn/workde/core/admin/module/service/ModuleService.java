package cn.workde.core.admin.module.service;


import cn.workde.core.admin.module.ModuleMeta;
import cn.workde.core.admin.module.define.ListField;
import cn.workde.core.admin.module.define.ModuleDefine;
import cn.workde.core.admin.module.define.ModuleField;
import cn.workde.core.admin.web.annotation.FieldDefine;
import cn.workde.core.base.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
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

	private List<ListField> getListFieldList(ModuleDefine moduleDefine, List<ModuleField> fieldDefineList) {
		List<ListField> listFields = new ArrayList<>();
		if(StringUtils.isNotEmpty(moduleDefine.getListNumberTitle())) {
			listFields.add(ListField.createNumberField(moduleDefine.getListNumberTitle()));
		}
		fieldDefineList.sort(Comparator.comparingInt(ModuleField::getOrder));
		for(ModuleField mfd : fieldDefineList) {
			FieldDefine fieldDefine = mfd.getFieldDefine();
			if(!fieldDefine.listEnable()) continue;
			ListField listField = new ListField(mfd.getName(), fieldDefine);
			listFields.add(listField);
		}
		return listFields;
	}

//	private List<ListField> getNewFieldList(List<ModuleField> fieldDefineList) {
//		List<ListField> listFields = new ArrayList<>();
//		fieldDefineList.sort(Comparator.comparingInt(ModuleField::getNewOrder));
//		for(ModuleField mfd : fieldDefineList) {
//			FieldDefine fieldDefine = mfd.getFieldDefine();
//			if(!fieldDefine.newEnabel()) continue;
//			ListField listField = new ListField(mfd.getName(), fieldDefine);
//			listFields.add(listField);
//		}
//		return listFields;
//	}

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

}
