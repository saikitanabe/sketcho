package net.sevenscales.domain;

import java.io.Serializable;

import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sf.hibernate4gwt.pojo.java5.LazyPojo;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DiagramItemDTO extends LazyPojo implements IDiagramItem, Serializable, IsSerializable {
	private static final long serialVersionUID = 5754682876312853660L;
	private Long id;
	private String text;
	private String type;
	private String shape;
  private IDiagramContent diagramContent;
	private String backgroundColor;
	private String textColor;
  private Integer version = 3;
	private String clientId;
	private String customData;
	
	public DiagramItemDTO() {
  }
	
	@Override
	public String toString() {
		return "DiagramItemDTO [id=" + id + ", text=" + text + ", type=" + type
				+ ", shape=" + shape + ", diagramContent=" + diagramContent
				+ ", backgroundColor=" + backgroundColor + ", textColor=" + textColor
				+ ", version=" + version + ", clientId=" + clientId + "]";
	}

	public DiagramItemDTO(String text, String type, String shape, String backgroundColor, String textColor,
			Integer version, Long id, String clientId, String customData) {
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
	}
	
	public DiagramItemDTO(String clientId) {
		this("", "", "", "", "", 0, 0L, clientId, "");
	}

	public DiagramItemDTO(IDiagramItemRO di) {
		copyFrom(di);
	}

	public Long getId() {
		return id != null ? id : -1;
	}
	
	public void setId(Long id) {
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
	
	public Integer getVersion() {
    return version;
  }
	
	public void setVersion(Integer version) {
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
	}
}
