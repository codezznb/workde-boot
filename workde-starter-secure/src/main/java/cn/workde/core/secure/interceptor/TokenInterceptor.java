package cn.workde.core.secure.interceptor;

import cn.workde.core.base.result.Result;
import cn.workde.core.base.utils.WebUtils;
import cn.workde.core.secure.utils.SecureUtils;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt拦截器校验
 * @author zhujingang
 * @date 2019/8/29 5:13 PM
 */
@Slf4j
@AllArgsConstructor
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (null != SecureUtils.getUser()) {
            return true;
        } else {
            log.warn("签名认证失败，请求接口：{}，请求IP：{}，请求参数：{}", request.getRequestURI(), WebUtils.getIP(request), JSON.toJSONString(request.getParameterMap()));
            Result result = Result.unauthorized();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("application/json", MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);
            try {
                response.getWriter().write(JSON.toJSONString(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
