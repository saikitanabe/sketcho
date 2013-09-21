package net.sevenscales.domain;

import com.google.gwt.core.client.JavaScriptObject;

// TODO maybe JsCommentMeta if doesn't contain msg itself!
public class JsComment extends JavaScriptObject {
	protected JsComment() {
	}

	public final native String getParentThread()/*-{
		return this.pthread;
	}-*/;

	public final native String getUser()/*-{
		return this.user;
	}-*/;

	public final native double getCreatedAt()/*-{
		return this.cat;
	}-*/;

	public final native double getUpdatedAt()/*-{
		return this.uat;
	}-*/;

}