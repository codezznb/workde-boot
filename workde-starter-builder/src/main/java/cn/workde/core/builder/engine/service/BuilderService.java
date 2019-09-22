package cn.workde.core.builder.engine.service;

import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.base.utils.StringUtils;
import cn.workde.core.base.utils.WebUtils;
import cn.workde.core.builder.controls.Control;
import cn.workde.core.builder.controls.ExtControl;
import cn.workde.core.builder.controls.ScriptControl;
import cn.workde.core.builder.controls.ServerScript;
import cn.workde.core.builder.engine.ControlBuffer;
import cn.workde.core.builder.engine.ModuleBuffer;
import cn.workde.core.builder.engine.ScriptBuffer;
import cn.workde.core.builder.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhujingang
 * @date 2019/9/17 9:50 PM
 */
@Slf4j
public class BuilderService {

	@Autowired
	private ModuleBuffer moduleBuffer;

	@Autowired
	private ControlBuffer controlBuffer;

	@Autowired
	private ScriptBuffer scriptBuffer;

	private HttpServletRequest request;
	private HttpServletResponse response;

	private StringBuilder headerHtml;
	private List<String> footerHtml;
	private int htmlPointer;
	private StringBuilder headerScript;
	private List<String> footerScript;
	private int scriptPointer;
	private boolean notLoadNone;

	public void parse(String moduleFile) throws Exception {
		this.request = WebUtils.getRequest();
		this.response = WebUtils.getResponse();
		this.headerHtml = new StringBuilder();
		this.footerHtml = new ArrayList<>(15);
		this.headerScript = new StringBuilder();
		this.footerScript = new ArrayList<>(15);
		boolean hasExcept = false;
		final boolean isInvoke = this.request.getParameter("xwlt") != null;
		int runMode  = (isInvoke ? 3 : 0);

		ConcurrentHashMap<String, Object> varMap = null;
		final Object object = this.request.getAttribute("sysx.varMap");
		if (object == null) {
			varMap = new ConcurrentHashMap<String, Object>();
			this.request.setAttribute("sysx.varMap", (Object)varMap);
		}
		try {
			this.execute(moduleFile, runMode, null, null);
		}catch (Throwable e) {
			hasExcept = true;
			// 错误处理
			throw e;
		}finally {
			this.closeObjects(varMap, hasExcept);
		}

		this.closeObjects(varMap, hasExcept);
	}

	public void execute(final String moduleFile, final int runMode, final String xwlId, final String params) throws Exception {


		final JSONObject root = moduleBuffer.get(moduleFile + ".xwl");
		final JSONObject module = (JSONObject) ((JSONArray) root.opt("children")).opt(0);
		final JSONObject configs = (JSONObject) module.opt("configs");
		boolean runNormal = runMode == 0;
		boolean runInvoke = runMode == 3;

		final JSONObject events = (JSONObject)module.opt("events");
		final JSONObject emptyJson = new JSONObject();
		final JSONObject moduleGeneral = (JSONObject) controlBuffer.get("module").opt("general");

		boolean[] libTypes = null;
		final boolean hasChildren = module.has("children");
		final boolean hasEvents = events != null;

		if (runMode == 4) {
			runNormal = true;
		} else if (runMode == 5) {
			runInvoke = true;
		}

		String content = this.getString(configs, "logMessage");
		if (!content.isEmpty()) {
			log.info(content);
		}

		content = ServerScript.getScript(configs, "initScript");
		if (!content.isEmpty()) {
			scriptBuffer.run(content, this.request, this.response, moduleFile);
		}

		content = this.getString(configs, "serviceMethod");
		System.out.println(content);
		if(!content.isEmpty()) {
			String[] services = content.split("\\.");
			if(services.length == 2) {
				SysUtil.executeServiceMethod(services[0], services[1], request, response);
			}
		}

		final boolean createFrame = this.getBool(configs, "createFrame", true);
		if(createFrame && runNormal) {
			this.headerHtml.append("<!DOCTYPE html>\n<html>\n<head>\n<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\"/>\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\"/>\n<title>");
			String title = this.getString(configs, "title");
			if (title.isEmpty()) {
				title = root.optString("title");
			} else if (title.equals("-")) {
				title = null;
			}
			if (!StringUtils.isEmpty(title)) {
				this.headerHtml.append(title);
			}
			this.headerHtml.append("</title>");
			this.appendScript(this.headerHtml, this.getString(configs, "head"));

			libTypes = this.setLinks(configs);
			final String tagConfigs = this.getString(configs, "tagConfigs");
			this.appendScript(this.headerHtml, this.getString(configs, "headLast"));
			if (tagConfigs.isEmpty()) {
				this.headerHtml.append("\n</head>\n<body>");
			}
			else {
				this.headerHtml.append("\n</head>\n<body ");
				this.headerHtml.append(tagConfigs);
				this.headerHtml.append('>');
			}
			this.headerScript.append("<script language=\"javascript\" type=\"text/javascript\">");
		}

		this.appendScript(this.headerHtml, this.getString(configs, "initHtml"));

		if (createFrame) {
			if (this.headerScript.length() > 0) {
				this.headerScript.append('\n');
			}
			if (runNormal && libTypes[1]) {
				this.headerScript.append("Ext.onReady(function(contextOptions,contextOwner){");
			}else {
				this.headerScript.append("(function(contextOptions,contextOwner){");
			}
			final String namespace = (String) configs.opt("itemId");
			if (namespace.equals("module")) {
				this.headerScript.append("\nvar app={isXNS:\"");
				this.headerScript.append(SysUtil.getId());
				this.headerScript.append("\"};");
			} else {
				this.headerScript.append("\nWb.ns(\"");
				this.headerScript.append(namespace);
				this.headerScript.append("\");\nvar app=");
				this.headerScript.append(namespace);
				this.headerScript.append(";\napp.isXNS=\"");
				this.headerScript.append(SysUtil.getId());
				this.headerScript.append("\";");
			}

			this.headerScript.append("\napp.contextOwner=contextOwner;");


			if (runNormal) {
				this.headerScript.append("\nwindow.app=app;");
				if (this.notLoadNone) {
					this.headerScript.append("\nWb.init({zo:");
					this.headerScript.append("-1");
					this.headerScript.append(",lang:\"");
					this.headerScript.append("zh");
					this.headerScript.append('\"');
					//@TODO
					this.headerScript.append("});");
				}
			}else if ((runMode == 2 || runMode == 1) && xwlId != null) {
				this.headerScript.append("\ncontextOwner[");
				this.headerScript.append(StringUtil.quote(xwlId));
				this.headerScript.append("]=app;");
			}
		}

		content = this.getString(configs, "importModules");
		if (!content.isEmpty()) {
			this.importModules(content);
		}

		content = ServerScript.getScript(configs, "serverScript");
		if (!content.isEmpty()) {

			scriptBuffer.run(content, this.request, this.response, moduleFile);
		}

		if (hasEvents) {
			final String beforeunload = this.getString(events, "beforeunload");
			if (!beforeunload.isEmpty()) {
				this.appendScript(this.headerScript, StringUtil.concat("Wb.onUnload(function(){\n", beforeunload, "\n},contextOwner);"));
			}
			this.appendScript(this.headerScript, this.getString(events, "initialize"));
		}

		if (hasChildren) {
			this.scan(module, moduleGeneral, emptyJson, runNormal);
		}
		if (this.response.isCommitted()) {
			return;
		}

		this.appendScript(this.headerHtml, this.getString(configs, "finalHtml"));
		if (hasEvents) {
			this.appendScript(this.headerScript, this.getString(events, "finalize"));
		}
		if (createFrame) {
			if (runNormal) {
				if (libTypes[1]) {
					this.headerScript.append("\n});");
				} else if (libTypes[2]) {
					this.headerScript.append("\n}});");
				} else {
					this.headerScript.append("\n})({});");
				}
			}else if (runMode == 2) {
				this.headerScript.append("\nreturn Wb.optMain(app);\n})(");
				this.headerScript.append((params == null) ? "{}" : params);
				this.headerScript.append(",app)");
			} else if (runMode == 1) {
				this.headerScript.append("\n})({},app);");
			}
			else {
				this.headerScript.append("\nreturn app;\n})();");
			}
		}
		if (runNormal) {
			if (createFrame) {
				this.headerScript.append("\n</script>\n</body>\n</html>");
			}
			this.output();
		}
		else if (runInvoke) {
			this.output();
		}
	}

	private void importModules(final String modules) throws Exception {
		final JSONArray moduleArray = new JSONArray(modules);
		for (int j = moduleArray.length(), i = 0; i < j; ++i) {
			this.execute(FileUtil.getModulePath((String)moduleArray.opt(i)), 1, "importXwl" + (i + 1), null);
		}
	}

	private void scan(final JSONObject parentNode, final JSONObject parentGeneral, final JSONObject emptyJson, final boolean normalType) throws Exception {
		final JSONArray ja = (JSONArray)parentNode.opt("children");
		final int j = ja.length();
		final int k = j - 1;
		for (int i = 0; i < j; ++i) {
			final JSONObject jo = (JSONObject)ja.opt(i);
			final String type = (String)jo.opt("type");
			final JSONObject meta = controlBuffer.get(type);
			final JSONObject general = (JSONObject)meta.opt("general");
			String className = (String)general.opt("class");
			Control control;
			boolean isScriptControl;
			if (className == null) {
				control = new ExtControl();
				isScriptControl = true;
			}else {
				if (className.indexOf(".") == -1) {
					className = "cn.workde.core.builder.controls." + className;
				}
				control = (Control)Class.forName(className).newInstance();
				isScriptControl = (control instanceof ScriptControl);
			}

			if (control != null) {
				control.init(this.request, this.response, jo, meta, parentGeneral, i == k, normalType);
				control.create();
			}
			if (isScriptControl) {
				final ScriptControl sc = (ScriptControl)control;
				this.appendScript(this.headerHtml, sc.getHeaderHtml());
				this.pushHtml(sc.getFooterHtml());
				this.appendScript(this.headerScript, sc.getHeaderScript());
				this.pushScript(sc.getFooterScript());
			}
			if (jo.has("children")) {
				this.scan(jo, general, emptyJson, normalType);
			}
			if (isScriptControl) {
				this.appendScript(this.headerHtml, this.popHtml());
				final String lastScript = this.popScript();
				final int quoteIndex = lastScript.lastIndexOf(125);
				final JSONObject configItems;
				if (quoteIndex != -1 && (configItems = (JSONObject)jo.opt("__configs")) != null) {
					this.appendScript(this.headerScript, lastScript.substring(0, quoteIndex));
					this.headerScript.append(',');
					this.scan(configItems, emptyJson, emptyJson, normalType);
					this.appendScript(this.headerScript, lastScript.substring(quoteIndex));
				}
				else {
					this.appendScript(this.headerScript, lastScript);
				}
			}
		}
	}

	private void output() throws IOException {
		if (this.headerHtml.length() > 0 && this.headerScript.length() > 0) {
			this.headerHtml.append('\n');
		}
		this.headerHtml.append((CharSequence)this.headerScript);
		if (this.headerHtml.length() > 0) {
			WebUtil.send(this.response, this.headerHtml);
		}
	}

	private boolean[] setLinks(final JSONObject configs) {
		final ArrayList<String> cssArray = new ArrayList<String>();
		final ArrayList<String> jsArray = new ArrayList<String>();
		JSONArray cssLinks = null;
		JSONArray jsLinks = null;
		String loadJS = this.getString(configs, "loadJS");
		final boolean[] libTypes = new boolean[4];
		final String cssLinksText = this.getString(configs, "cssLinks");
		final String jsLinksText = this.getString(configs, "jsLinks");

		if(!cssLinksText.isEmpty()) {
			cssLinks = new JSONArray();
			JsonUtil.addAll(cssLinks, new JSONArray(cssLinksText));
		}

		if(!jsLinksText.isEmpty()) {
			jsLinks = new JSONArray();
			JsonUtil.addAll(jsLinks, new JSONArray(jsLinksText));
		}
		this.notLoadNone = !"none".equals(loadJS);
		if (loadJS.isEmpty()) {
			loadJS = "ext";
		}

		if (this.notLoadNone) {
			jsArray.add("scripts/locale/wb-lang-zh.js");
		}

		if (loadJS.indexOf("ext") != -1) {
			libTypes[1] = true;
			cssArray.add("libs/ext/resources/ext-theme-modern/ext-theme-modern-all.css");
			jsArray.add("libs/ext/ext-all.js");
			jsArray.add("libs/ext/locale/ext-lang-zh.js");
		}

		if (this.notLoadNone) {
			cssArray.add("styles/style.css");
			jsArray.add("scripts/wb.js");
		}

		if (cssLinks != null) {
			for (int j = cssLinks.length(), i = 0; i < j; ++i) {
				final String value = cssLinks.getString(i);
				final int index = cssArray.indexOf(value);
				if (index != -1) {
					cssArray.remove(index);
				}
				cssArray.add(value);
			}
		}
		if (jsLinks != null) {
			for (int j = jsLinks.length(), i = 0; i < j; ++i) {
				final String value = jsLinks.getString(i);
				final int index = jsArray.indexOf(value);
				if (index != -1) {
					jsArray.remove(index);
				}
				jsArray.add(value);
			}
		}
		for (final String css : cssArray) {
			this.headerHtml.append("\n<link type=\"text/css\" rel=\"stylesheet\" href=\"");
			this.headerHtml.append(css);
			this.headerHtml.append("\"/>");
		}
		for (final String js : jsArray) {
			this.headerHtml.append("\n<script type=\"text/javascript\" src=\"");
			this.headerHtml.append(js);
			this.headerHtml.append("\"></script>");
		}
		return libTypes;

	}


	private void pushHtml(final String script) {
		++this.htmlPointer;
		if (this.footerHtml.size() < this.htmlPointer) {
			this.footerHtml.add(script);
		}
		else {
			this.footerHtml.set(this.htmlPointer - 1, script);
		}
	}

	private String popHtml() {
		--this.htmlPointer;
		return this.footerHtml.get(this.htmlPointer);
	}

	private void pushScript(final String script) {
		++this.scriptPointer;
		if (this.footerScript.size() < this.scriptPointer) {
			this.footerScript.add(script);
		}
		else {
			this.footerScript.set(this.scriptPointer - 1, script);
		}
	}

	private String popScript() {
		--this.scriptPointer;
		return this.footerScript.get(this.scriptPointer);
	}

	private void appendScript(final StringBuilder buf, final String script) {
		if (!StringUtils.isEmpty(script)) {
			if (buf.length() > 0) {
				buf.append('\n');
			}
			buf.append(script);
		}
	}

	private String getString(final JSONObject object, final String name) {
		final String value = (String)object.opt(name);
		if (value == null) {
			return "";
		}
		return WebUtil.replaceParams(request, value);
	}

	private boolean getBool(final JSONObject object, final String name, final boolean defaultValue) {
		final String value = this.getString(object, name);
		if (value.isEmpty()) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	private void closeObjects(final ConcurrentHashMap<String, Object> map, final boolean isExcept) {
		final Set<Map.Entry<String, Object>> es = map.entrySet();
		final ArrayList<Connection> connList = new ArrayList<Connection>();
		final ArrayList<Statement> stList = new ArrayList<Statement>();

		for (final Map.Entry<String, Object> e : es) {
			final Object object = e.getValue();

			if (object != null) {
				if (object instanceof ResultSet) {
					JdbcUtils.closeResultSet((ResultSet) object);
				}
				else if (object instanceof Statement) {
					stList.add((Statement)object);
				}
				else if (object instanceof Connection) {
					connList.add((Connection)object);
				}
				else if (object instanceof InputStream) {
					IOUtils.closeQuietly((InputStream)object);
				}
				else {
					if (!(object instanceof OutputStream)) {
						continue;
					}
					IOUtils.closeQuietly((OutputStream)object);
				}
			}

		}

		for (final Statement st : stList) {
			JdbcUtils.closeStatement(st);
		}
		final JdbcTemplate jdbcTemplate = SpringUtils.getBean(JdbcTemplate.class);
		for (final Connection conn : connList) {
			if (isExcept) {
				DataSourceUtils.releaseConnection(conn, jdbcTemplate.getDataSource());
			} else {
				DbUtil.closeCommit(conn, jdbcTemplate.getDataSource());
			}
		}
	}

}
