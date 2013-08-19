package net.sevenscales.confluence.plugins.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;


@XmlRootElement(name = "diagramitem")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiagramItemJson {
  @XmlElement(name = "text")
	private String text;
  @XmlElement(name = "elementType")
	private String elementType;
  @XmlElement(name = "shape")
	private String shape;
  @XmlElement(name = "backgroundColor")
	private String backgroundColor;
  @XmlElement(name = "textColor")
	private String textColor;
  @XmlElement(name = "version")
	private Integer version;
  @XmlElement(name = "id")
	private Long id;
  @XmlElement(name = "clientId")
	private String clientId;
  @XmlElement(name = "cd")
	private String cd;
  @XmlElement(required = false, name = "crc")
	private String crc;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getElementType() {
		return elementType;
	}
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	public String getShape() {
		return shape;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public String getTextColor() {
		return textColor;
	}
	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getCd() {
		return cd;
	}
	public void setCd(String cd) {
		this.cd = cd;
	}
	
  public final DiagramItemDTO asDTO() {
  	return new DiagramItemDTO(getText(), getElementType(), getShape(), getBackgroundColor(), getTextColor(), getVersion(), new Long(getId()), getClientId(), getCd());
  }
	public static DiagramItemJson fromDTO(IDiagramItemRO from) {
		DiagramItemJson result = new DiagramItemJson();
		result.setText(from.getText());
		result.setElementType(from.getType());
		result.setShape(from.getShape());
		result.setBackgroundColor(from.getBackgroundColor());
		result.setTextColor(from.getTextColor());
		result.setVersion(from.getVersion());
		result.setId(from.getId());
		result.setClientId(from.getClientId());
		return result;
	}
}
