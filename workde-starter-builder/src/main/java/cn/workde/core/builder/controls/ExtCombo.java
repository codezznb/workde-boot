package cn.workde.core.builder.controls;

/**
 * @author zhujingang
 * @date 2019/9/22 10:55 PM
 */
public class ExtCombo extends ExtControl{

	@Override
	protected void extendConfig() throws Exception {
		final String keyName = this.gs("keyName");
		String queryControl = this.gs("queryControl");
//		if (!keyName.isEmpty()) {
//			if (this.hasItems) {
//				this.headerScript.append(',');
//			}
//			else {
//				this.hasItems = true;
//			}
//			this.headerScript.append(getkeyNameScript(keyName));
//		}
//		if (!queryControl.isEmpty()) {
//			if (queryControl.startsWith("app.")) {
//				queryControl = queryControl.substring(4);
//			}
//			if (this.hasItems) {
//				this.headerScript.append(',');
//			}
//			else {
//				this.hasItems = true;
//			}
//			this.headerScript.append(this.getQueryScript(queryControl));
//		}
	}
}
