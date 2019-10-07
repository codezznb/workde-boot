package cn.workde.core.base.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.workde.core.base.properties.WorkdeProperties;


/**
 * @author zhujingang
 * @date 2019/9/23 8:23 AM
 */
public class IdUtils {

	public static Long getId() {
		WorkdeProperties workdeProperties = SpringUtils.getBean(WorkdeProperties.class);
		final String serverId = workdeProperties.getServerId();
		Snowflake snowflake = IdUtil.createSnowflake(Long.parseLong(serverId.substring(0,1)), Long.parseLong(serverId.substring(1,2)));
		return snowflake.nextId();
	}

}
