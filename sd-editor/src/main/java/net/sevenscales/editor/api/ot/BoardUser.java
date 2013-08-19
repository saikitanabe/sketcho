package net.sevenscales.editor.api.ot;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONNumber;

public class BoardUser {
	private String username = "";
	private String avatarUrl = "";
	private int x;
	private int y;
	private String clientIdentifier = "";

	public BoardUser(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public BoardUser(String username, String avatarUrl, int x, int y, String clientIdentifier) {
		this.username = username;
		this.avatarUrl = avatarUrl;
		this.x = x;
		this.y = y;
		this.clientIdentifier = clientIdentifier;
	}

	/**
	* Can use gwt to json since no checksum made out of these
	*/
	public JSONValue toJson() {
    JSONObject result = new JSONObject();
    result.put("username", new JSONString(username));
    result.put("avatarUrl", new JSONString(username));
    result.put("x", new JSONNumber(x));
    result.put("y", new JSONNumber(y));
    result.put("cid", new JSONString(clientIdentifier));
		return result;
	}

	// public static class BoardUsersJson extends JavaScriptObject {
	// 	public final native JsArray<BoardUserJson> getUsers() /*-{ return this.users; }-*/;
	// }

	public static class BoardUserJson extends JavaScriptObject {
		protected BoardUserJson() {}
		public final native String getUsername() /*-{return this.username;}-*/;
		public final native String getAvatarUrl() /*-{return this.avatarUrl;}-*/;
		public final native int getX() /*-{return this.x;}-*/;
		public final native int getY() /*-{return this.y;}-*/;
		public final native String getClientIdentifier() /*-{return this.cid;}-*/;
	}

}
