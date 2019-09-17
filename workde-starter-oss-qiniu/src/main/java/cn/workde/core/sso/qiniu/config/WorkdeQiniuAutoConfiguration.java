package cn.workde.core.sso.qiniu.config;

import cn.workde.core.base.utils.StringUtils;
import cn.workde.core.sso.qiniu.properties.WorkdeQiniuProperties;
import cn.workde.core.sso.qiniu.service.IQiniuService;
import cn.workde.core.sso.qiniu.service.impl.QiniuServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhujingang
 * @date 2019/9/17 3:26 PM
 */
@Configuration
@EnableConfigurationProperties(WorkdeQiniuProperties.class)
@Slf4j
public class WorkdeQiniuAutoConfiguration {

	@Autowired
	private WorkdeQiniuProperties workdeQiniuProperties;

	@Bean
	public IQiniuService qiniuService() {
		if(StringUtils.isEmpty(workdeQiniuProperties.getAccessKey()) || StringUtils.isEmpty(workdeQiniuProperties.getSecretKey())) {
			log.warn("七牛云AccessKey或者SecretKey为空.");
		}
		return new QiniuServiceImpl();
	}
}
