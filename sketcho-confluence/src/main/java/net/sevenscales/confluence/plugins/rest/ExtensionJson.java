package net.sevenscales.confluence.plugins.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sevenscales.domain.ExtensionDTO;

@XmlRootElement(name = "ext")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtensionJson {
	@XmlElement(name = "svg")
	private SvgDataJson svg;
  
  public ExtensionJson() {
  }

  public ExtensionJson(SvgDataJson svg) {
  	this.svg = svg;
  }
  
  public SvgDataJson getSvg() {
    return svg;
  }

  public void setSvg(SvgDataJson svg) {
    this.svg = svg;
  }

  public ExtensionDTO asDTO() {
    return new ExtensionDTO(svg.asDTO());
  }
  
}
