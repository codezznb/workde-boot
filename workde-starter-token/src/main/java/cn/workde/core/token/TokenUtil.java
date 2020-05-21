package cn.workde.core.token;

import cn.hutool.core.convert.Convert;
import cn.workde.core.base.result.Kv;
import cn.workde.core.base.utils.StringUtils;
import cn.workde.core.base.utils.WebUtils;
import cn.workde.core.token.constant.TokenConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;

/**
 * 认证工具类
 * @author zhujingang
 * @date 2019/8/30 4:58 PM
 */
public class TokenUtil {

	private static final String WORKDE_USER_REQUEST_ATTR = "_WORKDE_USER_REQUEST_ATTR_";

	public final static String TENANT_HEADER_KEY = "Tenant-Id";
	public final static String DEFAULT_TENANT_ID = "000000";
	public final static String USER_TYPE_HEADER_KEY = "User-Type";
	public final static String DEFAULT_USER_TYPE = "web";
	public final static String USER_NOT_FOUND = "用户名或密码错误";
	public final static String HEADER_KEY = "Authorization";
	public final static String HEADER_PREFIX = "Basic ";
	public final static String DEFAULT_AVATAR = "https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png";


	/**
	 * 创建认证token
	 *
	 * @param userInfo 用户信息
	 * @return token
	 */
	public static Kv createAuthInfo(UserInfo userInfo) {
		Kv authInfo = Kv.create();
		//设置jwt参数
		Map<String, Object> param = userInfo.getUserParam();

		//拼装accessToken
		try {
			TokenInfo accessToken = createJWT(param, "audience", "issuser", TokenConstant.ACCESS_TOKEN);
			//返回accessToken
			return authInfo.set(TokenConstant.NICK_NAME, userInfo.getRealName())
				.set(TokenConstant.ACCESS_TOKEN, accessToken.getToken())
				.set(TokenConstant.REFRESH_TOKEN, createRefreshToken(userInfo).getToken())
				.set(TokenConstant.TOKEN_TYPE, TokenConstant.BEARER)
				.set(TokenConstant.EXPIRES_IN, accessToken.getExpire())
				.set(TokenConstant.LICENSE, TokenConstant.LICENSE_NAME);
		} catch (Exception ex) {
			return authInfo.set("error_code", HttpServletResponse.SC_UNAUTHORIZED).set("error_description", ex.getMessage());
		}
	}

	/**
	 * 创建refreshToken
	 *
	 * @param userInfo 用户信息
	 * @return refreshToken
	 */
	private static TokenInfo createRefreshToken(UserInfo userInfo) {
		Map<String, Object> param = new HashMap<>(16);
		param.put(TokenConstant.TOKEN_TYPE, TokenConstant.REFRESH_TOKEN);
		param.put(TokenConstant.USER_ID, Convert.toStr(userInfo.getId()));
		return createJWT(param, "audience", "issuser", TokenConstant.REFRESH_TOKEN);
	}

	/**
	 * 解析jsonWebToken
	 *
	 * @param jsonWebToken jsonWebToken
	 * @return Claims
	 */
	public static Claims parseJWT(String jsonWebToken) {
		return JwtUtil.parseJWT(jsonWebToken);
	}

	public static TokenInfo createJWT(Map<String, Object> user, String audience, String issuer, String tokenType) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		//生成签名密钥
		byte[] apiKeySecretBytes = Base64.getDecoder().decode(JwtUtil.BASE64_SECURITY);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//添加构成JWT的类
		JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JsonWebToken")
			.setIssuer(issuer)
			.setAudience(audience)
			.signWith(signatureAlgorithm, signingKey);

		//设置JWT参数
		if(user != null) {
			user.forEach(builder::claim);
		}

		//添加Token过期时间
		long expireMillis;
		if (tokenType.equals(TokenConstant.ACCESS_TOKEN)) {
			expireMillis = 24 * 60 * 60 * 1000L;
			// expireMillis = 1 * 60 * 1000L;
		} else if (tokenType.equals(TokenConstant.REFRESH_TOKEN)) {
			expireMillis = 30 * 24 * 60 * 60 * 1000L;
		} else {
			expireMillis = getExpire();
		}
		long expMillis = nowMillis + expireMillis;
		Date exp = new Date(expMillis);
		builder.setExpiration(exp).setNotBefore(now);
		// 组装Token信息
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setToken(builder.compact());
		tokenInfo.setExpire((int) expireMillis / 1000);


		//生成JWT
		return tokenInfo;
	}

	/**
	 * 获取过期时间(次日凌晨3点)
	 *
	 * @return expire
	 */
	public static long getExpire() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 3);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis() - System.currentTimeMillis();
	}


	public static UserInfo getUserInfo() {
		HttpServletRequest request = WebUtils.getRequest();
		if(request == null) return null;

		// 优先从 request 中获取
		Object userInfo = request.getAttribute(WORKDE_USER_REQUEST_ATTR);
		if (userInfo == null) {
			userInfo = getUserInfo(request);
			if (userInfo != null) {
				// 设置到 request 中
				request.setAttribute(WORKDE_USER_REQUEST_ATTR, userInfo);
			}
		}
		return (UserInfo) userInfo;

	}

	public static UserInfo getUserInfo(HttpServletRequest request) {
		Claims claims = getClaims(request);
		if (claims == null) {
			return null;
		}

		String userId = Convert.toStr(claims.get(TokenConstant.USER_ID));
		Long expiresIn = claims.getExpiration().getTime();
		UserInfo userInfo = new UserInfo();
		userInfo.setId(userId);
		userInfo.setExpiresIn(expiresIn);
		userInfo.setUserParam(claims);

		return userInfo;
	}

	/**
	 * 获取Claims
	 *
	 * @param request request
	 * @return Claims
	 */
	public static Claims getClaims(HttpServletRequest request) {
		String auth = request.getHeader(TokenConstant.HEADER);
		if (StringUtils.isNotBlank(auth)) {
			String token = JwtUtil.getToken(auth);
			if (StringUtils.isNotBlank(token)) {
				return parseJWT(token);
			}
		} else {
			String parameter = request.getParameter(TokenConstant.HEADER);
			if (StringUtils.isNotBlank(parameter)) {
				return parseJWT(parameter);
			}
		}
		return null;
	}

	public static void main(String[] args) {
		String token = "eyJ0eXAiOiJKc29uV2ViVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpc3N1c2VyIiwiYXVkIjoiYXVkaWVuY2UiLCJ1c2VyX2lkIjoiMTE3NzEyNTAzOTA4NzAyNjE3NiIsInRva2VuX3R5cGUiOiJhY2Nlc3NfdG9rZW4iLCJhY2NvdW50IjoiMTgwNjczNDExODQiLCJleHAiOjE1NzA3ODA3NzAsIm5iZiI6MTU3MDY5NDM3MH0._BkMpB1t7EH6jxX-UnEInuLBqt7_G8Nc5RvDH71fF1o";
		System.out.println(parseJWT(token));
	}
}
