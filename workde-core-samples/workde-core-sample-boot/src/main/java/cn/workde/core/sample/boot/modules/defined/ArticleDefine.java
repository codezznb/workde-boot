package cn.workde.core.sample.boot.modules.defined;

import cn.workde.core.admin.module.control.FormControl;
import cn.workde.core.admin.module.control.RadioControl;
import cn.workde.core.admin.module.control.SelectControl;
import cn.workde.core.admin.module.control.TextAreaControl;
import cn.workde.core.admin.module.define.ModuleDefine;
import cn.workde.core.admin.web.annotation.FieldDefine;
import cn.workde.core.sample.boot.entity.Article;
import cn.workde.core.sample.boot.service.ArticleService;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author zhujingang
 * @date 2019/9/9 12:28 AM
 */
@Component
public class ArticleDefine extends ModuleDefine {

	public ArticleDefine() {
		this.setModuleTitle("内容");
		this.setModel(Article.class);
		this.setBaseService(ArticleService.class);
	}

	public Integer getFormGroups() { return 2; }

	public String getFormGroupName(Integer group) {
		if(group == 1) return "基本信息";
		else return "内容信息";
	}

	@Override
	public FormControl getFormControl(String name, FieldDefine fieldDefine, Boolean isNew) {
		if("channelId".equals(name)) {
			SelectControl selectControl = new SelectControl();
			selectControl.init(name, fieldDefine);
			selectControl.setHeaderKey("");
			selectControl.setHeaderValue("--请选择---");
			Map<String, String> list = new LinkedHashMap();
			list.put("6", "公告");
			list.put("7", "政策");
			list.put("8", "发文");
			list.put("9", "热门服务");
			selectControl.setList(list);
			return selectControl;
		}

		if("status".equals(name)) {
			RadioControl radioControl = new RadioControl();
			radioControl.init(name, fieldDefine);
			radioControl.setValue("0");

			Map<String, String> list = new LinkedHashMap();
			list.put("0", "正常");
			list.put("1", "未审核");
			list.put("2", "锁定");
			radioControl.setList(list);
			return radioControl;
		}

		if("zhaiyao".equals(name)) {
			TextAreaControl textAreaControl = new TextAreaControl();
			textAreaControl.init(name, fieldDefine);
			textAreaControl.setRows(4);
			return textAreaControl;
		}

		if("content".equals(name)) {
			TextAreaControl textAreaControl = new TextAreaControl();
			textAreaControl.init(name, fieldDefine);
			textAreaControl.setRows(8);
			return textAreaControl;
		}
		return null;
	}
}
