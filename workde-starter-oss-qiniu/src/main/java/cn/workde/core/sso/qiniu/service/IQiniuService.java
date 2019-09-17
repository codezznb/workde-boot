package cn.workde.core.sso.qiniu.service;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

/**
 * @author zhujingang
 * @date 2019/9/17 3:57 PM
 */
public interface IQiniuService {

	Auth getAuth();

	String getUploadToken();

	String getUploadToken(String bucket);

	String getUploadToken(String bucket, StringMap putPolicy);
}
