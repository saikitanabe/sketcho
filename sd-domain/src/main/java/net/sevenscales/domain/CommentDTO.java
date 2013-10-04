package net.sevenscales.domain;

import com.google.gwt.json.client.*;

import net.sevenscales.domain.utils.JsonFormat;

public class CommentDTO extends DiagramItemDTO {
	private String parentThreadId;
	private String username;
	private String userDisplayName;
	private long createdAt;
	private long updatedAt;

	public CommentDTO(String text, String type, String shape, String backgroundColor, String textColor,
			Integer version, Long id, String clientId, String customData, double crc32, String parentThreadId, String username, String userDisplayName, long createdAt, long updatedAt) {
		super(text, type, shape, backgroundColor, textColor, version, id, clientId, customData, crc32);

	}

	public String getParentThreadId() {
		return parentThreadId;
	}

	public String getUsername() {
		return username;
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public JSONValue toJson(JsonFormat jsonFormat) {
		JSONValue result = super.toJson(jsonFormat);
		JSONObject obj = result.isObject();
		if (obj != null) {
    	obj.put("pthread", new JSONString(safeJsonString(parentThreadId)));
    	obj.put("user", new JSONString(safeJsonString(username)));
    	obj.put("dname", new JSONString(safeJsonString(userDisplayName)));
    	obj.put("cat", new JSONNumber(createdAt));
    	obj.put("uat", new JSONNumber(updatedAt));
    }
		return result;
	}
}