package net.sevenscales.domain;

import com.google.gwt.json.client.JSONObject;

public class JSONBoardUserParser {
	private String email;
	private String username;
	private String avatarUrl;
	private boolean sketchboardAvatar;
	private int x;
	private int y;
	private int tx;
	private int ty;
	private String cids;
	private String cid;

	public JSONBoardUserParser(JSONObject obj) {
		email = JSONParserHelpers.getString(obj.get("email"));
		username = JSONParserHelpers.getString(obj.get("username"));
		avatarUrl = JSONParserHelpers.getString(obj.get("avatarUrl"));
		sketchboardAvatar = JSONParserHelpers.getBoolean(obj.get("sketchboardAvatar"));
		x = JSONParserHelpers.getInt(obj.get("x"));
		y = JSONParserHelpers.getInt(obj.get("y"));
		tx = JSONParserHelpers.getInt(obj.get("tx"));
		ty = JSONParserHelpers.getInt(obj.get("ty"));
		cids = JSONParserHelpers.getString(obj.get("cids"));
		cid = JSONParserHelpers.getString(obj.get("cid"));
	}

	public String getEmail() {
		return email;
	}
	public String getUsername() {
		return this.username;
	}
	public String getAvatarUrl() {
		return this.avatarUrl;
	}
	public boolean isSketchboardAvatar() {
		return this.sketchboardAvatar;
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	public int getTargetX() {
		return this.tx;
	}
	public int getTargetY() {
		return this.ty;
	}
	public String getSelectedCids() {
		return this.cids;
	}
	public String getClientIdentifier() {
		return this.cid;
	}

}
