package cn.workde.core.builder.controls;

/**
 * @author zhujingang
 * @date 2019/9/18 10:26 AM
 */
public class ScriptControl extends Control {
	protected StringBuilder headerHtml;
	protected StringBuilder footerHtml;
	protected StringBuilder headerScript;
	protected StringBuilder footerScript;

	public ScriptControl() {
		this.headerHtml = new StringBuilder();
		this.footerHtml = new StringBuilder();
		this.headerScript = new StringBuilder();
		this.footerScript = new StringBuilder();
	}

	public String getHeaderHtml() {
		return this.headerHtml.toString();
	}

	public String getFooterHtml() {
		return this.footerHtml.toString();
	}

	public String getHeaderScript() {
		return this.headerScript.toString();
	}

	public String getFooterScript() {
		return this.footerScript.toString();
	}
}
