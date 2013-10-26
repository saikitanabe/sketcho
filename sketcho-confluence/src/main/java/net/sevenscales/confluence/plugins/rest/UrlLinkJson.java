package net.sevenscales.confluence.plugins.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import net.sevenscales.domain.UrlLinkDTO;

@XmlRootElement(name = "urllink")
@XmlAccessorType(XmlAccessType.FIELD)
public class UrlLinkJson {
	@XmlElement(name = "url")
	private String url = "";
  @XmlElement(name = "name")
  private String name = "";
  public UrlLinkDTO asDTO() {
    return new UrlLinkDTO(url, name);
  }

}
