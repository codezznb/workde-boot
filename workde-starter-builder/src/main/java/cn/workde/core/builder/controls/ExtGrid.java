package cn.workde.core.builder.controls;

import cn.workde.core.builder.utils.StringUtil;
import cn.workde.core.builder.utils.WbUtil;

/**
 * @author zhujingang
 * @date 2019/9/19 12:29 PM
 */
public class ExtGrid extends ExtControl{

	@Override
	protected void extendConfig() throws Exception {
		final String syncStateId = this.gs("syncStateId");
		final String allowExportUrl = this.gs("allowExportUrl");
		if (!allowExportUrl.isEmpty() && !WbUtil.canAccess(this.request, allowExportUrl)) {
			if (this.hasItems) {
				this.headerScript.append(',');
			}
			else {
				this.hasItems = true;
			}
			this.headerScript.append("hideExportBtns:true");
		}
		/*if (!syncStateId.isEmpty()) {
			final String headersData = this.getHeadersData(syncStateId);
			if (!StringUtil.isEmpty(headersData)) {
				if (this.hasItems) {
					this.headerScript.append(',');
				}
				else {
					this.hasItems = true;
				}
				this.headerScript.append("syncHeadersData:");
				this.headerScript.append(headersData);
			}
		}*/
	}

//	public String getHeadersData(final String syncStateId) {
//		return Resource.getString(this.request, "gridState#" + syncStateId, null);
//	}
}
