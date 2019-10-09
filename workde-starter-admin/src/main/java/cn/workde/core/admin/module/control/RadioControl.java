package cn.workde.core.admin.module.control;

import cn.workde.core.base.module.constant.Inputs;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/10/5 11:58 AM
 */
@Data
public class RadioControl  extends FormControl{


	/**
	 * 选项值，可以是 List or Map
	 */
	private Object list;

	/**
	 * 如果 选项值 是 List 对象，设置显示的key
	 */
	private String listKey;

	/**
	 * 如果 选项值 是 List 对象，设置显示的value
	 */
	private String listValue;

	@Override
	public String getType() {
		return Inputs.RADIO;
	}
}
