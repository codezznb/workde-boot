package cn.workde.core.sample.boot.modules.defined;

import cn.workde.core.admin.module.define.ModuleDefine;
import cn.workde.core.sample.boot.entity.Article;
import cn.workde.core.sample.boot.service.ArticleService;
import org.springframework.stereotype.Component;

/**
 * @author zhujingang
 * @date 2019/9/9 12:28 AM
 */
@Component
public class ArticleDefine extends ModuleDefine {

	public ArticleDefine() {
		this.setModuleTitle("内容");
		this.setModel(Article.class);
		this.setListNumberTitle("序号");
		this.setCheckbox(true);
		this.setBaseService(ArticleService.class);
	}
}
