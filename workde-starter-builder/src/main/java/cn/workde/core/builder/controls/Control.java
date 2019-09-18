package cn.workde.core.builder.controls;

import cn.workde.core.builder.utils.DateUtil;
import cn.workde.core.builder.utils.WebUtil;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author zhujingang
 * @date 2019/9/18 10:24 AM
 */
public class Control {

	public HttpServletRequest request;
	public HttpServletResponse response;
	public JSONObject controlData;
	public JSONObject configs;
	protected JSONObject events;
	protected JSONObject controlMeta;
	protected JSONObject generalMeta;
	protected JSONObject configsMeta;
	protected JSONObject eventsMeta;
	protected JSONObject parentGeneral;
	protected boolean lastNode;
	protected boolean normalRunType;

	public void create() throws Exception {
	}

	public void init(final HttpServletRequest request, final HttpServletResponse response, final JSONObject controlData, final JSONObject controlMeta, final JSONObject parentGeneral, final boolean lastNode, final boolean normalRunType) {
		this.request = request;
		this.response = response;
		this.controlData = controlData;
		this.configs = (JSONObject)controlData.opt("configs");
		this.events = (JSONObject)controlData.opt("events");
		this.controlMeta = controlMeta;
		this.generalMeta = (JSONObject)controlMeta.opt("general");
		this.configsMeta = (JSONObject)controlMeta.opt("configs");
		this.eventsMeta = (JSONObject)controlMeta.opt("events");
		this.parentGeneral = parentGeneral;
		this.lastNode = lastNode;
		this.normalRunType = normalRunType;
	}

	protected String gs(final String name) {
		final Object value = this.configs.opt(name);
		if (value == null) {
			return "";
		}
		return WebUtil.replaceParams(this.request, (String)value);
	}

	protected int gi(final String name) {
		return this.gi(name, 0);
	}

	protected int gi(final String name, final int defaultValue) {
		final String value = this.gs(name);
		if (value.isEmpty()) {
			return defaultValue;
		}
		return Integer.parseInt(value);
	}

	protected float gf(final String name) {
		return this.gf(name, 0.0f);
	}

	protected float gf(final String name, final float defaultValue) {
		final String value = this.gs(name);
		if (value.isEmpty()) {
			return defaultValue;
		}
		return (float)Integer.parseInt(value);
	}

	protected Date gd(final String name) {
		return this.gd(name, null);
	}

	protected Date gd(final String name, final Date defaultValue) {
		final String value = this.gs(name);
		if (value.isEmpty()) {
			return defaultValue;
		}
		return DateUtil.strToDate(value);
	}

	protected boolean gb(final String name) {
		return this.gb(name, false);
	}

	protected boolean gb(final String name, final boolean defaultValue) {
		final String value = this.gs(name);
		if (value.isEmpty()) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	protected String ge(final String name) {
		if (this.events == null) {
			return "";
		}
		final Object event = this.events.opt(name);
		if (event == null) {
			return "";
		}
		return WebUtil.replaceParams(this.request, (String)event);
	}
}
