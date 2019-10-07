package cn.workde.core.admin.module.control;

import cn.workde.core.admin.module.constant.Inputs;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/9/30 1:54 PM
 */
@Data
public class TextControl extends FormControl {

	private Boolean readonly;

	@Override
	public String getType() {
		return Inputs.DEFAULT;
	}
}
