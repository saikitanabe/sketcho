package net.sevenscales.domain;

import com.google.gwt.json.client.*;

import net.sevenscales.domain.utils.JsonFormat;
import net.sevenscales.domain.api.IDiagramItem;

public class CommentThreadDTO extends DiagramItemDTO {
	private int resolved;

	public CommentThreadDTO(String text, String type, String shape, String backgroundColor, String textColor,
			Integer version, Long id, String clientId, String customData, double crc32, int annotation, int resolved) {
		super(text, type, shape, backgroundColor, textColor, version, id, clientId, customData, crc32, annotation);
		this.resolved = resolved;
	}

	@Override
	public String toString() {
		return "CommentThreadDTO=" + "[" + super.toString() + "][resolved=" + resolved 
				+ "]";
	}

	public CommentThreadDTO(String clientId) {
		this("", "", "", "", "", 0, 0L, clientId, "", 0, 1, 0);
	}

	public CommentThreadDTO(IDiagramItemRO di) {
		copyFrom(di);
	}

	public int getResolved() {
		return resolved;
	}

	public void setResolved(int resolved) {
		this.resolved = resolved;
	}

	@Override
	public IDiagramItem copy() {
		return new CommentThreadDTO(this);
	}
	
	@Override
	public void copyFrom(IDiagramItemRO di) {
		super.copyFrom(di);
		if (di instanceof CommentThreadDTO) {
			CommentThreadDTO c = (CommentThreadDTO) di;
			resolved = c.resolved;
		}
	}

	public JSONValue toJson(JsonFormat jsonFormat) {
		JSONValue result = super.toJson(jsonFormat);
		JSONObject obj = result.isObject();
		if (obj != null && resolved == 1) {
	    obj.put("r", new JSONNumber(resolved));
    }
		return result;
	}
}