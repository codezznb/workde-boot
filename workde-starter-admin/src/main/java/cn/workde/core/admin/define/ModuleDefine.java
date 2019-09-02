package cn.workde.core.admin.define;

import cn.workde.core.tk.base.BaseEntity;
import cn.workde.core.tk.base.BaseService;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/9/2 12:59 PM
 */
@Data
public class ModuleDefine {

	private Class<? extends BaseEntity> model;

	private Class<? extends BaseService> baseService;
}
