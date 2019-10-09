package cn.workde.core.admin.module.control;

import cn.workde.core.base.module.constant.Inputs;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/10/8 1:03 PM
 */
@Data
public class ReadonlyControl extends FormControl{

	@Override
	public String getType() {
		return Inputs.READONLY;
	}
}
