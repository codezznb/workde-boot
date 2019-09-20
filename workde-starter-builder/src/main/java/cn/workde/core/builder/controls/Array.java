package cn.workde.core.builder.controls;

/**
 * @author zhujingang
 * @date 2019/9/19 12:34 PM
 */
public class Array extends ScriptControl{

	@Override
	public void create() throws Exception {
		final boolean parentRoot = Boolean.TRUE.equals(this.parentGeneral.opt("root"));
		if (parentRoot) {
			this.headerScript.append("app.");
			this.headerScript.append(this.gs("itemId"));
			this.headerScript.append("=[");
			this.footerScript.append("];");
		}
		else {
			if ("Array".equals(this.parentGeneral.opt("type"))) {
				this.headerScript.append("[");
			}
			else {
				this.headerScript.append(this.gs("itemId"));
				this.headerScript.append(":[");
			}
			if (this.lastNode) {
				this.footerScript.append("]");
			}
			else {
				this.footerScript.append("],");
			}
		}
	}
}
