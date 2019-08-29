package cn.workde.core.secure.utils;

import cn.hutool.core.lang.func.Func;
import cn.hutool.core.util.StrUtil;
import cn.workde.core.base.cover.Convert;
import cn.workde.core.base.utils.WebUtils;
import cn.workde.core.secure.JwtUser;
import cn.workde.core.secure.constant.TokenConstant;
import cn.workde.core.secure.jwt.JwtUtil;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhujingang
 * @date 2019/8/29 5:14 PM
 */
public class SecureUtils {

    private static final String WORKDE_USER_REQUEST_ATTR = "_WORKDE_USER_REQUEST_ATTR_";

    private final static String HEADER = TokenConstant.HEADER;
    private final static String BEARER = TokenConstant.BEARER;
    private final static String ACCOUNT = TokenConstant.ACCOUNT;
    private final static String USER_NAME = TokenConstant.USER_NAME;
    private final static String NICK_NAME = TokenConstant.NICK_NAME;
    private final static String USER_ID = TokenConstant.USER_ID;
    private final static String DEPT_ID = TokenConstant.DEPT_ID;
    private final static String ROLE_ID = TokenConstant.ROLE_ID;
    private final static String ROLE_NAME = TokenConstant.ROLE_NAME;
    private final static String TENANT_ID = TokenConstant.TENANT_ID;
    private final static String CLIENT_ID = TokenConstant.CLIENT_ID;
    private final static Integer AUTH_LENGTH = TokenConstant.AUTH_LENGTH;

    /**
     * 获取用户信息
     *
     * @return BladeUser
     */
    public static JwtUser getUser() {
        HttpServletRequest request = WebUtils.getRequest();
        if (request == null) {
            return null;
        }
        // 优先从 request 中获取
        Object jwtUser = request.getAttribute(WORKDE_USER_REQUEST_ATTR);
        if (jwtUser == null) {
            jwtUser = getUser(request);
            if (jwtUser != null) {
                // 设置到 request 中
                request.setAttribute(WORKDE_USER_REQUEST_ATTR, jwtUser);
            }
        }
        return (JwtUser) jwtUser;
    }

    /**
     * 获取用户信息
     *
     * @param request request
     * @return BladeUser
     */
    public static JwtUser getUser(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) {
            return null;
        }
        String clientId = Convert.toStr(claims.get(SecureUtils.CLIENT_ID));
        Long userId = Convert.toLong(claims.get(SecureUtils.USER_ID));
        String tenantId = Convert.toStr(claims.get(SecureUtils.TENANT_ID));
        String deptId = Convert.toStr(claims.get(SecureUtils.DEPT_ID));
        String roleId = Convert.toStr(claims.get(SecureUtils.ROLE_ID));
        String account = Convert.toStr(claims.get(SecureUtils.ACCOUNT));
        String roleName = Convert.toStr(claims.get(SecureUtils.ROLE_NAME));
        String userName = Convert.toStr(claims.get(SecureUtils.USER_NAME));
        String nickName = Convert.toStr(claims.get(SecureUtils.NICK_NAME));
        JwtUser jwtUser = new JwtUser();
        jwtUser.setClientId(clientId);
        jwtUser.setUserId(userId);
        jwtUser.setTenantId(tenantId);
        jwtUser.setAccount(account);
        jwtUser.setDeptId(deptId);
        jwtUser.setRoleId(roleId);
        jwtUser.setRoleName(roleName);
        jwtUser.setUserName(userName);
        jwtUser.setNickName(nickName);
        return jwtUser;
    }

    /**
     * 获取Claims
     *
     * @param request request
     * @return Claims
     */
    public static Claims getClaims(HttpServletRequest request) {
        String auth = request.getHeader(SecureUtils.HEADER);
        if (StrUtil.isNotBlank(auth)) {
            String token = JwtUtil.getToken(auth);
            if (StrUtil.isNotBlank(token)) {
                return JwtUtil.parseJWT(token);
            }
        } else {
            String parameter = request.getParameter(SecureUtils.HEADER);
            if (StrUtil.isNotBlank(parameter)) {
                return JwtUtil.parseJWT(parameter);
            }
        }
        return null;
    }
}
