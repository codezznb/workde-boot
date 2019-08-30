package cn.workde.core.boot.ctrl;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * 控制器封装类
 * @author zhujingang
 * @date 2019/8/28 6:23 PM
 */
public class WorkdeController {

    /**
     * ============================     REQUEST    =================================================
     */

    @Autowired
    private HttpServletRequest request;

    /**
     * 获取request
     */
    public HttpServletRequest getRequest() {
        return this.request;
    }
}
