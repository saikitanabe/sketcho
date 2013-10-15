package net.sevenscales.domain;

import com.google.gwt.json.client.*;

import net.sevenscales.domain.utils.JsonFormat;
import net.sevenscales.domain.api.IDiagramItem;

public class CommentDTO extends DiagramItemDTO {
	private String parentThreadId;
	private String username;
	private String userDisplayName;
	private long createdAt;
	private long updatedAt;

	public CommentDTO(String text, String type, String shape, String backgroundColor, String textColor,
			Integer version, Long id, String clientId, String customData, double crc32, int annotation, int resolved, String parentThreadId, String username, String userDisplayName, long createdAt, long updatedAt) {
		super(text, type, shape, backgroundColor, textColor, version, id, clientId, customData, crc32, annotation, resolved);
		this.parentThreadId = parentThreadId;
		this.username = username;
		this.userDisplayName = userDisplayName;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "CommentDTO=" + "[" + super.toString() + "][parentThreadId=" + parentThreadId 
				+ ", username=" + username 
				+ ", userDisplayName=" + userDisplayName
				+ ", createdAt=" + createdAt 
				+ ", updatedAt=" + updatedAt
				+ "]";
	}


	public CommentDTO(String parentThreadId, String username, String userDisplayName) {
		this("", "", "", "", "", 0, 0L, "", "", 0, 1, 0, parentThreadId, username, userDisplayName, 0L, 0L);
	}	

	public CommentDTO(String parentThreadId, String username, String userDisplayName, long createdAt, long updatedAt) {
		this("", "", "", "", "", 0, 0L, "", "", 0, 1, 0, parentThreadId, username, userDisplayName, createdAt, updatedAt);
	}	

	public CommentDTO(String clientId, String parentThreadId) {
		this("", "", "", "", "", 0, 0L, clientId, "", 0, 1, 0, parentThreadId, "", "", 0L ,0L);
	}

	public CommentDTO(IDiagramItemRO di) {
		copyFrom(di);
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

	public void setCreatedAt(long cat) {
		this.createdAt = cat;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(long uat) {
		this.updatedAt = uat;
	}

	@Override
	public boolean isComment() {
		return true;
	}

	@Override
	public IDiagramItem copy() {
		return new CommentDTO(this);
	}
	
	@Override
	public void copyFrom(IDiagramItemRO di) {
		super.copyFrom(di);
		if (di.isComment()) {
			CommentDTO c = (CommentDTO) di;
			parentThreadId = c.parentThreadId;
			username = c.username;
			userDisplayName = c.userDisplayName;
			createdAt = c.createdAt;
			updatedAt = c.updatedAt;
		}
	}

	public JSONValue toJson(JsonFormat jsonFormat) {
		JSONValue result = super.toJson(jsonFormat);
		JSONObject obj = result.isObject();
		if (obj != null) {
    	obj.put("p", new JSONString(safeJsonString(parentThreadId)));
    	obj.put("cby", new JSONString(safeJsonString(username)));
    	obj.put("cbyd", new JSONString(safeJsonString(userDisplayName)));
    	obj.put("cat", new JSONNumber(createdAt));
    	obj.put("uat", new JSONNumber(updatedAt));
    }
		return result;
	}
}