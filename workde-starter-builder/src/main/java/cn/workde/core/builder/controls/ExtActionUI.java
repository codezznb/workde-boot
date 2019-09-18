package cn.workde.core.builder.controls;

/**
 * @author zhujingang
 * @date 2019/9/18 10:37 AM
 */
public class ExtActionUI extends ExtControl {

	@Override
	protected void extendConfig() throws Exception {
		final String bindModule = this.gs("bindModule");
		if (!bindModule.isEmpty()) {
			if (this.hasItems) {
				this.headerScript.append(',');
			} else {
				this.hasItems = true;
			}
			this.headerScript.append("hidden:true");
		}
	}
}
