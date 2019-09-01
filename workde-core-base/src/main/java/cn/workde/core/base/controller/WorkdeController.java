package cn.workde.core.base.controller;

import cn.workde.core.base.result.Kv;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

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


    public Kv getPermitParams(String ...names){
		Kv kv = Kv.create();
		Arrays.asList(names).stream().forEach(name -> {
			String value = getRequest().getParameter(name);
			if(value != null) kv.put(name, value);
		});
		return kv;
	}
}
