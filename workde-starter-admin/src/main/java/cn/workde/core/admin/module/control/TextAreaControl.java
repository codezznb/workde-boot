package cn.workde.core.admin.module.control;

import cn.workde.core.admin.module.constant.Inputs;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/10/5 12:07 PM
 */
@Data
public class TextAreaControl extends FormControl {

	private Integer rows;

	private Integer cols;

	@Override
	public String getType() {
		return Inputs.TEXTAREA;
	}
}
