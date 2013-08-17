package net.sevenscales.editor.content;

import net.sevenscales.domain.DiagramItemJS;
import net.sevenscales.sketchoconfluenceapp.client.util.BoardUser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class OperationJS extends JavaScriptObject {
	protected OperationJS() {
	}
	
	public final native String getOperation()/*-{
		return this.operation;
	}-*/;
	public final native JsArray<DiagramItemJS> getItems()/*-{
		return this.items;
	}-*/;

	public final native JsArray<BoardUser.BoardUserJson> getUsers() /*-{
		return this.users;
	}-*/;
}
