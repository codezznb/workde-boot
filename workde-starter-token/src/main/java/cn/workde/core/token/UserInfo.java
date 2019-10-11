package cn.workde.core.token;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zhujingang
 * @date 2019/8/30 3:42 PM
 */
@Data
public class UserInfo implements Serializable {

	@ApiModelProperty(value = "用户ID")
	private String id;
	@ApiModelProperty(value = "令牌")
	private String accessToken;
	@ApiModelProperty(value = "令牌类型")
	private String tokenType;
	@ApiModelProperty(value = "刷新令牌")
	private String refreshToken;
	@ApiModelProperty(value = "角色名")
	private String authority;
	@ApiModelProperty(value = "用户名")
	private String realName;
	@ApiModelProperty(value = "过期时间")
	private long expiresIn;
	@ApiModelProperty(value = "许可证")
	private String license = "powered by blade";

	private Map<String, Object> userParam;

}
