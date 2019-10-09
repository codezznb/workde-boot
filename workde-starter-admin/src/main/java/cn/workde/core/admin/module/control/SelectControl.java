package cn.workde.core.admin.module.control;

import cn.workde.core.base.module.constant.Inputs;
import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/10/5 10:38 AM
 */
@Data
public class SelectControl extends FormControl {

	/**
	 * 显示在第一行的Key
	 */
	private String headerKey;

	/**
	 * 显示在第一行的value
	 */
	private String headerValue;

	/**
	 * 下拉列表值，可以是 List or Map
	 */
	private Object list;

	/**
	 * 如果 下拉值 是 List 对象，设置显示的key
	 */
	private String listKey;

	/**
	 * 如果 下拉值 是 List 对象，设置显示的value
	 */
	private String listValue;

	@Override
	public String getType() {
		return Inputs.SELECT;
	}
}
