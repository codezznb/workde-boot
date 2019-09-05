package cn.workde.core.base.menu;

import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/9/5 11:45 PM
 */
@Data
public class MenuGroup {

	private int order = 100;

	private String text;

	private String icon;

	private String id;


}
