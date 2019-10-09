package cn.workde.core.admin.module.control;

import cn.workde.core.base.module.constant.DateFormat;
import cn.workde.core.base.module.constant.Inputs;
import cn.workde.core.base.utils.StringUtils;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/10/8 1:04 PM
 */
@Data
public class DateControl extends FormControl {

	private String format;

	public DateControl(String format) {
		if(StringUtils.isEmpty(format)) this.format = DateFormat.yyyyMMdd;
		else this.format = format;
	}

	@Override
	public String getType() {
		return Inputs.DATE;
	}
}
