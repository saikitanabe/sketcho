package net.sevenscales.editor.api.ot;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class BoardUser {
	private String username = "";
	private String avatarUrl = "";
	private int x;
	private int y;
	private int targetx;
	private int targety;
	private String selectedCids = "";
	private String clientIdentifier = "";

	public BoardUser(int x, int y) {
		this("", "", x, y, 0, 0, "", "");
	}

	public BoardUser(int x, int y, int targetx, int targety, String selectedCids) {
		this("", "", x, y, targetx, targety, selectedCids, "");
	}

	public BoardUser(String username, String avatarUrl, int x, int y, int targetx, int targety, String selectedCids, String clientIdentifier) {
		this.username = username;
		this.avatarUrl = avatarUrl;
		this.x = x;
		this.y = y;
		this.targetx = targetx;
		this.targety = targety;
		this.selectedCids = selectedCids;
		this.clientIdentifier = clientIdentifier;
	}

	/**
	* Can use gwt to json since no checksum made out of these
	*/
	public JSONObject toJson() {
    JSONObject result = new JSONObject();
    result.put("username", new JSONString(username));
    result.put("avatarUrl", new JSONString(username));
    result.put("x", new JSONNumber(x));
    result.put("y", new JSONNumber(y));
    result.put("tx", new JSONNumber(targetx));
    result.put("ty", new JSONNumber(targety));
    result.put("cids", new JSONString(selectedCids));
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
		public final native int getTargetX() /*-{return this.tx;}-*/;
		public final native int getTargetY() /*-{return this.ty;}-*/;
		public final native String getSelectedCids() /*-{return this.cids;}-*/;
		public final native String getClientIdentifier() /*-{return this.cid;}-*/;
	}

}
