package cn.workde.core.sso.qiniu.service.impl;

import cn.workde.core.sso.qiniu.properties.WorkdeQiniuProperties;
import cn.workde.core.sso.qiniu.service.IQiniuService;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhujingang
 * @date 2019/9/17 3:28 PM
 */
public class QiniuServiceImpl implements IQiniuService {

	@Autowired
	private WorkdeQiniuProperties workdeQiniuProperties;

	public String getUploadToken() {
		return getUploadToken(workdeQiniuProperties.getBucket());
	}

	public String getUploadToken(String bucket) {
		StringMap putPolicy = new StringMap()
			.put("callbackBody", "{\"key\":$(key),\"hash\":$(etag),\"size\":$(fsize),\"width\":$(imageInfo.width),\"height\":$(imageInfo.height)}");
		return getUploadToken(bucket, putPolicy);
	}

	public String getUploadToken(String bucket, StringMap putPolicy) {
		Auth auth = getAuth();
		long expireSeconds = workdeQiniuProperties.getExpireSeconds();
		String uploadToken = auth.uploadToken(bucket, null, expireSeconds, putPolicy);
		return uploadToken;
	}

	public Auth getAuth() {
		return Auth.create(workdeQiniuProperties.getAccessKey(), workdeQiniuProperties.getSecretKey());
	}
}
