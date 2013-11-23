package net.sevenscales.confluence.plugins.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sevenscales.domain.IDiagramItemRO;


@XmlRootElement(name = "diagramchilditem")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiagramChildItemJson extends DiagramItemJson {
  @XmlElement(name = "p")
  private String p;
  
  public String getP() {
	return p;
  }
  
  public void setP(String p) {
	this.p = p;
  }
  
	public static DiagramItemJson fromDTO(IDiagramItemRO from) {
		DiagramChildItemJson result = new DiagramChildItemJson();
		result.setText(from.getText());
		result.setElementType(from.getType());
		result.setShape(from.getShape());
		result.setBackgroundColor(from.getBackgroundColor());
		result.setTextColor(from.getTextColor());
		result.setVersion(from.getVersion());
		result.setId(from.getId());
		result.setClientId(from.getClientId());
		result.setCd(from.getCustomData());
		
		if (from instanceof ChildDTO) {
			result.setP(((ChildDTO) from).getParent());
		}
		return result;
	}
	
}
