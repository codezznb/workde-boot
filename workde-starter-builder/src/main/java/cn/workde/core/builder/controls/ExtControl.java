package cn.workde.core.builder.controls;

import cn.workde.core.builder.utils.JsonUtil;
import cn.workde.core.builder.utils.StringUtil;
import cn.workde.core.builder.utils.WebUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * @author zhujingang
 * @date 2019/9/18 10:26 AM
 */
public class ExtControl extends ScriptControl{

	public boolean normalMode;
	protected boolean hasItems;
	protected boolean hasMediaItems;
	private StringBuilder mediaScript;

	public ExtControl() {
		this.normalMode = true;
		this.mediaScript = new StringBuilder();
	}

	@Override
	public void create() throws Exception {
		if (this.directOutput()) {
			return;
		}
		boolean typeAdded = false;
		final boolean parentRoot = Boolean.TRUE.equals(this.parentGeneral.opt("root"));
		final String userXtype = this.gs("xtype");
		String xtype;
		boolean hasXtype;
		if (userXtype.isEmpty()) {
			xtype = (String)this.generalMeta.opt("xtype");
			hasXtype = (xtype != null);
		}
		else {
			xtype = userXtype;
			hasXtype = true;
		}
		final String type = (String)this.generalMeta.opt("type");
		if (parentRoot) {
			final String itemId = this.gs("itemId");
			if (this.normalMode) {
				this.headerScript.append("app.");
				this.headerScript.append(itemId);
				this.headerScript.append("=app._");
				this.headerScript.append(itemId);
			}
			if (this.gb("createInstance", !Boolean.FALSE.equals(this.generalMeta.opt("autoCreate")))) {
				if (this.normalMode) {
					if (type == null) {
						this.headerScript.append("={");
						this.footerScript.insert(0, "};");
					}
					else {
						this.headerScript.append("=new ");
						if (!this.normalRunType && type.endsWith(".Viewport")) {
							this.headerScript.append("Ext.container.Container");
						}
						else if (!this.normalRunType && type.equals("tviewport")) {
							this.headerScript.append("Ext.Container");
						}
						else {
							this.headerScript.append(type);
						}
						this.headerScript.append("({");
						if (this.normalRunType && hasXtype && !Boolean.FALSE.equals(this.generalMeta.opt("render"))) {
							this.headerScript.append("renderTo:document.body");
							this.hasItems = true;
						}
						this.footerScript.insert(0, "});");
						typeAdded = true;
					}
				}
				else {
					this.headerScript.append("{");
					this.footerScript.insert(0, "}");
				}
			}
			else {
				this.headerScript.append("={");
				this.footerScript.insert(0, "};");
			}
		}
		else {
			if (!Boolean.TRUE.equals(this.parentGeneral.opt("container"))) {
				this.headerScript.append((String)this.configs.opt("itemId"));
				this.headerScript.append(':');
			}
			this.headerScript.append('{');
			if (this.lastNode) {
				this.footerScript.insert(0, '}');
			}
			else {
				this.footerScript.insert(0, "},");
			}
		}
		if (this.hasItems) {
			this.headerScript.append(',');
		}
		else {
			this.hasItems = true;
		}
		if (this.normalMode) {
			this.headerScript.append("appScope:app");
		}
		else {
			this.headerScript.append("appScope:null");
		}
		if ("tviewport".equals(type)) {
			this.headerScript.append(",isViewport:true");
			if (this.gs("layout").isEmpty()) {
				this.headerScript.append(",layout:\"card\"");
			}
		}
		if (!typeAdded && hasXtype) {
			if (this.hasItems) {
				this.headerScript.append(',');
			}
			else {
				this.hasItems = true;
			}
			this.headerScript.append("xtype:\"");
			this.headerScript.append(xtype);
			this.headerScript.append("\"");
		}
		this.extendConfig();
		this.processConfigs();
		if (this.events != null) {
			this.processEvents();
		}
		if (this.controlData.has("children") && this.controlData.optJSONArray("children").length() > 0) {
			this.addMedia(true);
			if (this.normalMode) {
				if (Boolean.TRUE.equals(this.generalMeta.opt("container"))) {
					if (this.hasItems) {
						this.headerScript.append(',');
					}
					this.headerScript.append("items:[");
					this.footerScript.insert(0, ']');
				}
				else {
					this.headerScript.append(',');
				}
			}
		}
		else {
			this.addMedia(false);
		}
	}

	private boolean directOutput() {
		final JSONObject config = (JSONObject)this.generalMeta.opt("directOutput");
		if (config == null) {
			return false;
		}
		final String value = this.gs((String)config.opt("name"));
		if (!value.isEmpty()) {
			final JSONArray array = config.optJSONArray("values");
			if (array.toList().indexOf(value) != -1) {
				this.headerScript.append('\"');
				this.headerScript.append(value);
				this.headerScript.append('\"');
				if (!this.lastNode) {
					this.headerScript.append(',');
				}
				return true;
			}
		}
		return false;
	}

	protected void addMedia(final boolean hasChildren) {
		final JSONObject media = (JSONObject) this.generalMeta.opt("media");
		if (media == null) {
			return;
		}
		final String xtypeName = (String)media.opt("xtypeName");
		String xtype = null;
		boolean xtypeEmpty;
		if (xtypeName == null) {
			xtypeEmpty = true;
		} else {
			xtype = this.gs(xtypeName);
			xtypeEmpty = xtype.isEmpty();
		}
		if (!hasChildren && !this.hasMediaItems && xtypeEmpty) {
			return;
		}
		if (this.hasItems) {
			this.headerScript.append(',');
		}
		this.headerScript.append((String)media.opt("name"));
		this.headerScript.append(":{");
		this.footerScript.insert(0, '}');
		if (this.hasMediaItems) {
			this.headerScript.append(this.mediaScript.toString());
			this.hasItems = true;
		} else {
			this.hasItems = false;
		}
		if (!xtypeEmpty) {
			if (this.hasMediaItems) {
				this.headerScript.append(',');
			}
			else {
				this.hasMediaItems = true;
			}
			this.headerScript.append("xtype:\"");
			this.headerScript.append(xtype);
			this.headerScript.append('\"');
			this.hasItems = true;
		}
	}

	protected void processConfigs() {
		final Set<Map.Entry<String, Object>> es = (Set<Map.Entry<String, Object>>)this.configs.toMap().entrySet();
		boolean addComma = this.hasItems;
		boolean addMediaComma = false;
		for (final Map.Entry<String, Object> entry : es) {
			final String key = entry.getKey();
			final String value = (String) entry.getValue();
			final JSONObject itemObject = (JSONObject)this.configsMeta.opt(key);
			if (itemObject != null) {
				if (Boolean.TRUE.equals(itemObject.opt("hidden"))) {
					continue;
				}
				StringBuilder script;
				if (Boolean.TRUE.equals(itemObject.opt("media"))) {
					if (addMediaComma) {
						this.mediaScript.append(',');
					}
					else {
						addMediaComma = true;
						this.hasMediaItems = true;
					}
					script = this.mediaScript;
				}
				else {
					if (addComma) {
						this.headerScript.append(',');
					}
					else {
						addComma = true;
						this.hasItems = true;
					}
					script = this.headerScript;
				}
				final String rename = (String)itemObject.opt("rename");
				if (rename == null) {
					script.append(key);
				}
				else {
					script.append(rename);
				}
				script.append(':');
				final char firstChar = value.charAt(0);
				if (firstChar == '@') {
					script.append(WebUtil.replaceParams(this.request, value.substring(1)));
				}
				else {
					final String type = (String)itemObject.opt("type");
					if (type.startsWith("exp")) {
						script.append(WebUtil.replaceParams(this.request, value));
					}
					else if (type.equals("glyph")) {
						script.append("0x");
						script.append(WebUtil.replaceParams(this.request, value));
					}
					else if (type.equals("js")) {
						script.append("function(");
						final JSONArray params = (JSONArray)itemObject.opt("params");
						if (params != null) {
							script.append(JsonUtil.join(params, ","));
						}
						script.append("){\n");
						script.append(WebUtil.replaceParams(this.request, value));
						script.append("\n}");
					}
					else {
						script.append(StringUtil.quote(WebUtil.replaceParams(this.request, value)));
					}
				}
			}
		}
		this.addTags(this.configs, this.hasItems);
	}

	protected void processEvents() {
		final Set<Map.Entry<String, Object>> es = (Set<Map.Entry<String, Object>>)this.events.toMap().entrySet();
		boolean addComma = false;
		boolean addMediaComma = false;
		for (final Map.Entry<String, Object> entry : es) {
			final String key = entry.getKey();
			final String value = (String) entry.getValue();
			final JSONObject itemObject = (JSONObject)this.eventsMeta.opt(key);
			if (itemObject != null) {
				if (Boolean.TRUE.equals(itemObject.opt("hidden"))) {
					continue;
				}
				StringBuilder script;
				if (Boolean.TRUE.equals(itemObject.opt("media"))) {
					if (addMediaComma) {
						this.mediaScript.append(',');
					}
					else {
						addMediaComma = true;
						if (this.hasMediaItems) {
							this.mediaScript.append(',');
						}
						else {
							this.hasMediaItems = true;
						}
						this.mediaScript.append("listeners:{");
					}
					script = this.mediaScript;
				}
				else {
					if (addComma) {
						this.headerScript.append(',');
					}
					else {
						addComma = true;
						if (this.hasItems) {
							this.headerScript.append(',');
						}
						else {
							this.hasItems = true;
						}
						this.headerScript.append("listeners:{");
					}
					script = this.headerScript;
				}
				script.append('\n');
				final String rename = (String)itemObject.opt("rename");
				if (rename == null) {
					script.append(key);
				}
				else {
					script.append(rename);
				}
				script.append(":function(");
				final JSONArray params = (JSONArray)itemObject.opt("params");
				if (params != null) {
					script.append(JsonUtil.join(params, ","));
				}
				script.append("){\n");
				script.append(WebUtil.replaceParams(this.request, value));
				script.append("\n}");
			}
		}
		if (this.addTags(this.events, addComma)) {
			this.headerScript.append("\n}");
		}
		if (addMediaComma) {
			this.mediaScript.append("\n}");
		}
	}

	private boolean addTags(final JSONObject object, boolean hasContent) {
		final boolean isEvents = object == this.events;
		String tags;
		if (isEvents) {
			tags = (String)object.opt("tagEvents");
		}
		else {
			tags = (String)object.opt("tagConfigs");
		}
		if (tags != null) {
			tags = WebUtil.replaceParams(this.request, tags);
			final String trimsTag = tags.trim();
			final int beginPos = trimsTag.indexOf(123);
			final int endPos = trimsTag.lastIndexOf(125);
			if (beginPos == 0 && endPos == trimsTag.length() - 1) {
				tags = trimsTag.substring(beginPos + 1, endPos).trim();
			}
			if (tags.isEmpty()) {
				return hasContent;
			}
			if (hasContent) {
				this.headerScript.append(',');
			}
			else {
				hasContent = true;
				if (isEvents) {
					if (this.hasItems) {
						this.headerScript.append(',');
					}
					else {
						this.hasItems = true;
					}
					this.headerScript.append("listeners:{");
				}
			}
			this.headerScript.append('\n');
			this.headerScript.append(tags);
			this.hasItems = true;
		}
		return hasContent;
	}

	protected void extendConfig() throws Exception {
	}
}
