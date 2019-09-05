package cn.workde.core.cpanel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhujingang
 * @date 2019/9/2 1:04 PM
 */
@Data
@ConfigurationProperties("workde.cpanel")
public class WorkdeCpanelProperties {

	private String loginUrl = "/cpanel/login";

	private String cpanelUrl = "cpanel";

}
