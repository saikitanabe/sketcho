package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;

public class JsSendOperation extends JavaScriptObject {
	protected JsSendOperation() {
	}

	public final native String getOperation()/*-{
		return this.operation
	}-*/;
	public final native JavaScriptObject getItems()/*-{
		return this.items
	}-*/;
}
