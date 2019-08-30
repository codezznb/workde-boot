package cn.workde.core.base.properties;

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

	private final List<String> skipUrl = new ArrayList<>();
}
