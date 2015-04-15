package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;

public class JsTimestamp extends JavaScriptObject {
	protected JsTimestamp() {
	}

	public final native String getClientId()/*-{
		return this.client_id
	}-*/;
	public final native double getCreatedAt()/*-{
		return this.cat
	}-*/;
	public final native double getUpdatedAt()/*-{
		return this.uat
	}-*/;
}
