package cn.workde.core.base.controller;

import cn.hutool.core.convert.Convert;
import cn.workde.core.base.result.Kv;
import cn.workde.core.base.utils.StringUtils;
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
	protected HttpServletRequest getRequest() {
        return this.request;
    }


	protected Kv getPermitParams(String ...names){
		Kv kv = Kv.create();
		Arrays.asList(names).stream().forEach(name -> {
			String value = getRequest().getParameter(name);
			if(value != null) kv.put(name, value);
		});
		return kv;
	}

	protected Integer getPageNum() {
		String pageNum = getRequest().getParameter("page");
		if(StringUtils.isEmpty(pageNum)) return 1;
		return Convert.toInt(pageNum);
	}

	protected Integer getPageSize() {
		String limit = getRequest().getParameter("limit");
		if(StringUtils.isEmpty(limit)) return 10;
		return Convert.toInt(limit);
	}
}
