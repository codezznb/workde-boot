package cn.workde.core.admin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhujingang
 * @date 2019/9/2 1:04 PM
 */
@Data
@ConfigurationProperties("workde.admin")
public class WorkdeAdminProperties {

	private String contextPath = "admin";
}
