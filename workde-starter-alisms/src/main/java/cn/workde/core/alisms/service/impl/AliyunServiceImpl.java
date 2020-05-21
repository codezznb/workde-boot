package cn.workde.core.alisms.service.impl;

import cn.workde.core.alisms.properties.AlismsJgyProperties;
import cn.workde.core.alisms.properties.AlismsProperties;
import cn.workde.core.alisms.service.IAliyunService;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhujingang
 * @date 2019/9/23 9:08 AM
 */
public class AliyunServiceImpl implements IAliyunService {

	@Autowired
	private AlismsProperties alismsProperties;
	@Autowired
	private AlismsJgyProperties alismsJgyProperties;


	@Override
	public IAcsClient getAcsClient() {
		//初始化acsClient,暂不支持region化
		IClientProfile profile = DefaultProfile.getProfile(alismsProperties.getRegionId(), alismsProperties.getAccessKey(), alismsProperties.getSecretKey());
		DefaultProfile.addEndpoint(alismsProperties.getRegionId(), alismsProperties.getProduct(), alismsProperties.getDomain());
		IAcsClient acsClient = new DefaultAcsClient(profile);
		return acsClient;
	}

	@Override
	public IAcsClient getJgyAcsClient() {
		//初始化acsClient,暂不支持region化
		IClientProfile profile = DefaultProfile.getProfile(alismsJgyProperties.getRegionId(), alismsJgyProperties.getAccessKey(), alismsJgyProperties.getSecretKey());
		DefaultProfile.addEndpoint(alismsJgyProperties.getRegionId(), alismsJgyProperties.getProduct(), alismsJgyProperties.getDomain());
		IAcsClient acsClient = new DefaultAcsClient(profile);
		return acsClient;
	}
}
