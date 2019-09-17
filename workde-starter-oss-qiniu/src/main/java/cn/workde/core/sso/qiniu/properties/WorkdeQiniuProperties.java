package cn.workde.core.sso.qiniu.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhujingang
 * @date 2019/9/17 3:24 PM
 */
@Data
@ConfigurationProperties("workde.qiniu")
public class WorkdeQiniuProperties {

	private String accessKey;
	private String secretKey;
	private String bucket;
	private Long expireSeconds = 3600l;
}
