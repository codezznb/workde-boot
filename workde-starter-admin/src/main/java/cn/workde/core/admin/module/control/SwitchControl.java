package cn.workde.core.admin.module.control;

import cn.workde.core.admin.module.constant.Inputs;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/10/7 10:23 PM
 */
@Data
public class SwitchControl extends FormControl{

	private String text;

	@Override
	public String getType() {
		return Inputs.SWITCH;
	}

	public Boolean getChecked() {
		return getValue() != null && (getValue().toString().equals("1") || Boolean.TRUE.equals(getValue()));
	}

}
