package net.sevenscales.domain;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.core.client.JsonUtils;


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

	public final native String getDisplayName()/*-{
		return this.dname;
	}-*/;

	public final native double getCreatedAt()/*-{
		return this.cat;
	}-*/;

	public final native double getUpdatedAt()/*-{
		return this.uat;
	}-*/;

	public static JsComment parseCommentJson(String commentJsonStr) {
    // logger.debug("parseCommentJson.commentJsonStr {}", commentJsonStr);
  	JsComment jsComment = JsonUtils.safeEval(commentJsonStr);
    // this.parentThreadId = jsComment.getParentThread();
    return jsComment;
	}

	public static JsComment createJsComment(String parentThreadId) {
		return parseCommentJson(createCommentJsonStr(parentThreadId, "", false).toString());
	}

	public static JsComment createJsComment(String parentThreadId, String displayName) {
		return parseCommentJson(createCommentJsonStr(parentThreadId, displayName, false).toString());
	}

	public static String createCommentJsonStr(String parentThreadId, String displayName, boolean toserver) {
   	JSONObject json = new JSONObject();
    json.put("pthread", new JSONString(parentThreadId));
    json.put("user", new JSONString(""));
    json.put("dname", new JSONString(displayName));
    json.put("cat", new JSONNumber(0));
    json.put("uat", new JSONNumber(0));

    String result = json.toString();

    if (toserver) {
    	result = result.replaceAll("\"", "\\\\\"");
    }

    // logger.debug("pthread: {}", result);
    return result;
	}


}