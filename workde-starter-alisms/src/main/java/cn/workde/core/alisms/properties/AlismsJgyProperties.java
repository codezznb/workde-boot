package cn.workde.core.alisms.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhujingang
 * @date 2019/9/23 9:06 AM
 */
@Data
@ConfigurationProperties("workde.alismsJgy")
public class AlismsJgyProperties {

	private String accessKey;
	private String secretKey;

	//产品名称:云通信短信API产品,开发者无需替换
	private String product = "Dysmsapi";

	//产品域名,开发者无需替换
	private String domain = "dysmsapi.aliyuncs.com";

	private String regionId = "cn-hangzhou";
}
