package cn.workde.core.admin.controller;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.workde.core.base.controller.WorkdeController;
import cn.workde.core.base.result.Kv;
import cn.workde.core.base.utils.WebUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author zhujingang
 * @date 2019/10/7 9:40 PM
 */
public class WorkdeAdminController extends WorkdeController {

	protected void renderScript(String file) {
		renderScript(file, null);
	}

	protected void renderScript(String file, Kv kv) {
		String content = ResourceUtil.readUtf8Str(file.startsWith("/") ? "" : "templates" + File.separator + (file.startsWith("/") ? "" : File.separator) + file + (file.endsWith(".js") ? "" : ".js"));
		StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
		stringTemplateLoader.putTemplate(content, content);

		Configuration stringLoaderConf = new Configuration(Configuration.VERSION_2_3_23);
		stringLoaderConf.setDefaultEncoding("UTF-8");
		stringLoaderConf.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		stringLoaderConf.setTemplateLoader(stringTemplateLoader);
		try {
			HttpServletResponse response = WebUtils.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/javascript");
			Template template = stringLoaderConf.getTemplate(content);
			template.process(kv, response.getWriter());
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
		}

	}
}
