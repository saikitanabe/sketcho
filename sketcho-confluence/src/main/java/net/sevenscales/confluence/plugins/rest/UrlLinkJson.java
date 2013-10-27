package net.sevenscales.confluence.plugins.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sevenscales.domain.UrlLinkDTO;

@XmlRootElement(name = "urllink")
@XmlAccessorType(XmlAccessType.FIELD)
public class UrlLinkJson {
	@XmlElement(name = "url")
	private String url = "";
  @XmlElement(required = false, name = "name")
  private String name = "";
  
  public UrlLinkJson() {
  }

  public UrlLinkJson(String url, String name) {
  	this.url = url;
  	this.name = name;
  }
  
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UrlLinkDTO asDTO() {
    return new UrlLinkDTO(url, name);
  }

}
