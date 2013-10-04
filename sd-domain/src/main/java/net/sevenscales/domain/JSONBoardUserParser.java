package net.sevenscales.domain;

import com.google.gwt.json.client.JSONObject;

public class JSONBoardUserParser {
	private String username;
	private String avatarUrl;
	private int x;
	private int y;
	private int tx;
	private int ty;
	private String cids;
	private String cid;

	public JSONBoardUserParser(JSONObject obj) {
		username = JSONParserHelpers.getString(obj.get("username"));
		avatarUrl = JSONParserHelpers.getString(obj.get("avatarUrl"));
		x = JSONParserHelpers.getInt(obj.get("x"));
		y = JSONParserHelpers.getInt(obj.get("y"));
		tx = JSONParserHelpers.getInt(obj.get("tx"));
		ty = JSONParserHelpers.getInt(obj.get("ty"));
		cids = JSONParserHelpers.getString(obj.get("cids"));
		cid = JSONParserHelpers.getString(obj.get("cid"));
	}

	public String getUsername() {
		return this.username;
	}
	public String getAvatarUrl() {
		return this.avatarUrl;
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
