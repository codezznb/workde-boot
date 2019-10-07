package cn.workde.core.alisms.service;

import com.aliyuncs.IAcsClient;

/**
 * @author zhujingang
 * @date 2019/9/23 9:02 AM
 */
public interface IAliyunService {

	/**
	 * 获取阿里云的ACSClient
	 * @return IAcsClient
	 */
	IAcsClient getAcsClient();

}
