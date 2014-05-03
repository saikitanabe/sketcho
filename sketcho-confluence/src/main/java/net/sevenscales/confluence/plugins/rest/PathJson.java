package net.sevenscales.confluence.plugins.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sevenscales.domain.PathDTO;

@XmlRootElement(name = "path")
@XmlAccessorType(XmlAccessType.FIELD)
public class PathJson {
  // case class Path(path: String, style: Option[String])
	@XmlElement(name = "path")
	private String path = "";
  @XmlElement(required = false, name = "style")
  private String style = "";
  
  public PathJson() {
  }

  public PathJson(String path) {
  	this.path = path;
  }

  public PathJson(String path, String style) {
    this.path = path;
    this.style = style;
  }
  
  public String getPath() {
	return path;
  }

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getStyle() {
		return style;
	}
	
	public void setStyle(String style) {
		this.style = style;
	}

	public PathDTO asDTO() {
		return new PathDTO(path, style);
	}

}
