package net.sevenscales.domain;

import java.io.Serializable;

import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.JsonFormat;
import net.sf.hibernate4gwt.pojo.java5.LazyPojo;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.json.client.*;

public class DiagramItemDTO extends LazyPojo implements IDiagramItem, Serializable, IsSerializable {
	public static final int DATA_VERSION = 4;
	private static final long serialVersionUID = 5754682876312853660L;
	private long id;
	private String text;
	private String type;
	private String shape;
  private IDiagramContent diagramContent;
	private String backgroundColor;
	private String textColor;
  private int version = DATA_VERSION;
	private String clientId;
	private String customData;
	private double crc32;
	private int annotation;
	
	public DiagramItemDTO() {
  }
	
	@Override
	public String toString() {
		return "DiagramItemDTO [id=" + id + ", text=" + text + ", type=" + type
				+ ", shape=" + shape + ", diagramContent=" + diagramContent
				+ ", backgroundColor=" + backgroundColor + ", textColor=" + textColor
				+ ", version=" + version + ", clientId=" + clientId + ", customData=" + customData + ", crc32=" + crc32
				+ ", annotation=" + annotation + "]";
	}

	public DiagramItemDTO(String text, String type, String shape, String backgroundColor, String textColor,
			Integer version, Long id, String clientId, String customData, double crc32) {
		this(text, type, shape, backgroundColor, textColor, version, id, clientId, customData, crc32, 0);
	}


	public DiagramItemDTO(String text, String type, String shape, String backgroundColor, String textColor,
			Integer version, Long id, String clientId, String customData, double crc32, int annotation
			) {
		super();
		this.text = text;
		this.type = type;
		this.shape = shape;
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
		this.version = version;
		this.id = id;
		this.clientId = clientId;
		this.customData = customData;
		this.crc32 = crc32;
		this.annotation = annotation;
	}
	
	public DiagramItemDTO(String clientId) {
		this("", "", "", "", "", 0, 0L, clientId, "", 0, 0);
	}

	public DiagramItemDTO(IDiagramItemRO di) {
		copyFrom(di);
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
	  return type;
	}
	public void setType(String type) {
	  this.type = type;
	}
	
	public String getShape() {
	  return shape;
	}
	public void setShape(String shape) {
	  this.shape = shape;
	}
	
	public IDiagramContent getDiagramContent() {
	  return this.diagramContent;
	}
	public void setDiagramContent(IDiagramContent diagramContent) {
	  this.diagramContent = diagramContent;
	}
	
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public String getBackgroundColor() {
		return backgroundColor;
	}
	
	@Override
	public String getTextColor() {
		return textColor;
	}
	@Override
	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}
	
	public int getVersion() {
    return version;
  }
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public String getClientId() {
		return clientId;
	}
	
	@Override
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	@Override
	public String getCustomData() {
		return customData;
	}

	@Override
	public void setCustomData(String customData) {
		this.customData = customData;
	}

	@Override
	public double getCrc32() {
		return crc32;
	}

	@Override
	public void setCrc32(double crc32) {
		this.crc32 = crc32;
	}

	@Override
	public int getAnnotation() {
		return annotation;
	}

	@Override
	public void setAnnotation(int annotation) {
		this.annotation = annotation;
	}

	@Override
	public boolean isAnnotation() {
		return annotation == 1;
	}

	@Override
	public void annotate() {
		annotation = 1;
	}

	@Override
	public void unannotate() {
		annotation = 0;
	}

	@Override
	public boolean equals(Object obj) {
		IDiagramItemRO item = (IDiagramItemRO) obj;

		if (checkIfNotSame(id, item.getId())) {
			return false;
		}
		if (checkIfNotSame(text, item.getText())) {
			return false;
		}
		if (checkIfNotSame(type, item.getType())) {
			return false;
		}
		if (checkIfNotSame(shape, item.getShape())) {
			return false;
		}
		if (checkIfNotSame(backgroundColor, item.getBackgroundColor())) {
			return false;
		}
		if (checkIfNotSame(textColor, item.getTextColor())) {
			return false;
		}
		if (checkIfNotSame(version, item.getVersion())) {
			return false;
		}
		if (checkIfNotSame(clientId, item.getClientId())) {
			return false;
		}
		if (checkIfNotSame(customData, item.getCustomData())) {
			return false;
		}
		if (crc32 != item.getCrc32()) {
			return false;
		}
		if (annotation != item.getAnnotation()) {
			return false;
		}

		return true;
	}
	
	private <T> boolean checkIfNotSame(T me, T other) {
		if ((me != null && other == null) || (me == null && other != null) || me != null && !me.equals(other)) {
			return true;
		}
		return false;
	}

	@Override
	public IDiagramItem copy() {
		return new DiagramItemDTO(this);
	}
	
	@Override
	public void copyFrom(IDiagramItemRO di) {
		DiagramItemDTO dit = (DiagramItemDTO) di;
		id = dit.id;
		text = dit.text;
		type = dit.type;
		shape = dit.shape;
		backgroundColor = dit.backgroundColor;
		textColor = dit.textColor;
		version = dit.version;
		clientId = dit.clientId;
		customData = dit.customData;
		crc32 = dit.crc32;
		annotation = dit.annotation;
	}

	public boolean isComment() {
		return false;
	}

	public JSONValue toJson(JsonFormat jsonFormat) {
    JSONObject result = new JSONObject();
    String text = safeJsonString(itemText(this, jsonFormat));
    result.put("text", new JSONString(text));
    result.put("elementType", new JSONString(safeJsonString(getType())));
    result.put("shape", new JSONString(safeJsonString(getShape())));
    result.put("backgroundColor", new JSONString(safeJsonString(getBackgroundColor())));
    result.put("textColor", new JSONString(safeJsonString(getTextColor())));
    result.put("version", new JSONNumber(getVersion()));
    result.put("id", new JSONNumber(getId()));
    result.put("clientId", new JSONString(safeJsonString(getClientId())));
    result.put("cd", new JSONString(safeJsonString(getCustomData())));
    result.put("crc", new JSONNumber(getCrc32()));

    if (annotation == 1) {
	    result.put("a", new JSONNumber(annotation));
    }

    return result;
	}

  private static String itemText(DiagramItemDTO item, JsonFormat jsonFormat) {
    String result = item.getText();
    result = result != null ? result : "";
    switch (jsonFormat) {
    case SEND_FORMAT:
      result = escapeForSending(result);
      break;
    case PRESENTATION_FORMAT:
      break;
    case SERVER_FORMAT:
      result = escapeForServerFormat(result);
      break;
    }
    return result;
	}
  
  protected static String escapeForSending(String value) {
    return value
                .replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("\\n", "\\\\\\\n")
                .replaceAll("\"", "\\\\\\\"");
  }
  
  protected static String escapeForServerFormat(String value) {
    return value;
//                  .replaceAll("\\\\", "\\\\\\")
//                  .replaceAll("\\n", "\\\\n");
//                  .replaceAll("\"", "\\\"");
  }
	
	protected static String safeJsonString(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}

}
