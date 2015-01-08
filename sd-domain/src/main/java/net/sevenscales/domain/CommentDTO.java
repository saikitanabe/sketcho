package net.sevenscales.domain;

import java.util.List;

import com.google.gwt.json.client.*;

import net.sevenscales.domain.utils.JsonFormat;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.api.IExtension;

public class CommentDTO extends DiagramItemDTO {
	private String username;
	private String userDisplayName;
	private long createdAt;
	private long updatedAt;

	public CommentDTO(String text, String type, String shape, IExtension extension, String backgroundColor, String textColor, Integer tsize,
			Integer version, Long id, String clientId, String customData, double crc32, int annotation, int resolved, List<UrlLinkDTO> links, String parentThreadId, String username, String userDisplayName, long createdAt, long updatedAt) {
		super(text, type, shape, extension, backgroundColor, textColor, tsize, /* shapeProperties */null, /* displayOrder */ null, version, id, clientId, customData, crc32, /*group*/ null, annotation, resolved, links, parentThreadId);
		this.username = username;
		this.userDisplayName = userDisplayName;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "CommentDTO=" + "[" + super.toString() + "][username=" + username 
				+ ", userDisplayName=" + userDisplayName
				+ ", createdAt=" + createdAt 
				+ ", updatedAt=" + updatedAt
				+ "]";
	}


	public CommentDTO(String parentThreadId, String username, String userDisplayName) {
		this("", "", "", /* svg */ null, "", "", null, 0, 0L, "", "", 0, 1, 0, null, parentThreadId, username, userDisplayName, 0L, 0L);
	}	

	public CommentDTO(String parentThreadId, String username, String userDisplayName, long createdAt, long updatedAt) {
		this("", "", "", /* svg */ null, "", "", null, 0, 0L, "", "", 0, 1, 0, null, parentThreadId, username, userDisplayName, createdAt, updatedAt);
	}	

	public CommentDTO(String clientId, String parentThreadId) {
		this("", "", "", /* svg */ null, "", "", null, 0, 0L, clientId, "", 0, 1, 0, null, parentThreadId, "", "", 0L ,0L);
	}

	public CommentDTO(IDiagramItemRO di) {
		copyFrom(di);
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
			username = c.username;
			userDisplayName = c.userDisplayName;
			createdAt = c.createdAt;
			updatedAt = c.updatedAt;
		}
	}

}