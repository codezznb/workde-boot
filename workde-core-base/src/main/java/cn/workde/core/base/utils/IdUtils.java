package cn.workde.core.base.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.workde.core.base.properties.WorkdeProperties;

import javax.annotation.PostConstruct;


/**
 * @author zhujingang
 * @date 2019/9/23 8:23 AM
 */
public class IdUtils {

	private static Snowflake snowflake;

	public static Long getId() {
		if(snowflake == null) {
			WorkdeProperties workdeProperties = SpringUtils.getBean(WorkdeProperties.class);
			final String serverId = workdeProperties.getServerId();
			snowflake = IdUtil.createSnowflake(Long.parseLong(serverId.substring(0,1)), Long.parseLong(serverId.substring(1,2)));
			System.out.println("come init");
		}
		Long id = snowflake.nextId();
		System.out.println(id);
		return id;
	}
//
//	public static void main(String[] args) {
//		for (int i = 0; i < 1000; i++){
//			getId();
//		}
//	}

}
