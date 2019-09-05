package cn.workde.core.secure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhujingang
 * @date 2019/8/29 10:02 PM
 */
@Data
@ConfigurationProperties("workde.secure")
public class WorkdeSecureProperties {

	private Boolean enable = true;
	private String loginPage = "/cpanel/login";
	private final List<String> skip = new ArrayList<>();
}
