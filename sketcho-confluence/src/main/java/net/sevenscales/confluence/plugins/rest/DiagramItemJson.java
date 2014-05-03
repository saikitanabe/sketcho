package net.sevenscales.confluence.plugins.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ExtensionDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.IExtensionRO;
import net.sevenscales.domain.IPathRO;
import net.sevenscales.domain.IUrlLinkRO;
import net.sevenscales.domain.UrlLinkDTO;


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
  @XmlElement(required = false, name = "fsize")
	private Integer fontSize;
  @XmlElement(required = false, name = "props")
  private Integer props;
  @XmlElement(required = false, name = "dord")
  private Integer dord;
  @XmlElement(name = "version")
	private Integer version;
  @XmlElement(name = "id")
	private Long id;
  @XmlElement(name = "clientId")
	private String clientId;
  @XmlElement(name = "cd")
	private String cd;
  @XmlElement(required = false, name = "crc")
	private Integer crc;
  @XmlElement(required = false, name = "a")
  private Integer a;
  @XmlElement(required = false, name = "r")
  private Integer r;
  @XmlElement(required = false, name = "uat")
  private Long uat;
  @XmlElement(required = false, name = "links")
  private List<UrlLinkJson> links;
  @XmlElement(required = false, name = "ext")
  private ExtensionJson ext;
  
	
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
	
	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}
	public Integer getFontSize() {
		return fontSize;
	}
	
	public void setProps(Integer props) {
		this.props = props;
	}
	public Integer getProps() {
		return props;
	}
	public void setDord(Integer dord) {
		this.dord = dord;
	}
	public Integer getDord() {
		return dord;
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
	
  public Integer getCrc() {
    return crc;
  }
  public void setCrc(Integer crc) {
    this.crc = crc;
  }
  public Integer getA() {
    return a;
  }
  public void setA(Integer a) {
    this.a = a;
  }
  public Integer getR() {
    return r;
  }
  public void setR(Integer r) {
    this.r = r;
  }
  public Long getUat() {
    return uat;
  }
  public void setUat(Long uat) {
    this.uat = uat;
  }
    
  public List<UrlLinkJson> getLinks() {
    return links;
  }
  public void setLinks(List<UrlLinkJson> links) {
    this.links = links;
  }
  
  public ExtensionJson getExt() {
	return ext;
  }
  public void setExt(ExtensionJson ext) {
	this.ext = ext;
  }
  
  public final DiagramItemDTO asDTO() {
    List<UrlLinkJson> jlinks = getLinks();
    List<UrlLinkDTO> links = null;
    if (jlinks != null) {
      links = new ArrayList<UrlLinkDTO>();
      for (UrlLinkJson link : getLinks()) {
        links.add(link.asDTO());
      }
    }

    ExtensionDTO e = getExt() != null ? getExt().asDTO() : null;
  	return new DiagramItemDTO(getText(), 
  							  getElementType(),
  							  getShape(),
  							  e,
  							  getBackgroundColor(),
  							  getTextColor(),
  							  getFontSize(),
  							  getProps(),
  							  getDord(),
  							  getVersion(), 
  							  new Long(getId()), 
  							  getClientId(), 
  							  getCd(),
  							  links);
  }
  
	public static DiagramItemJson fromDTO(IDiagramItemRO from) {
		DiagramItemJson result = new DiagramItemJson();
		result.setText(from.getText());
		result.setElementType(from.getType());
		result.setShape(from.getShape());
		result.setExt(fromDTO(from.getExtension()));
		result.setBackgroundColor(from.getBackgroundColor());
		result.setTextColor(from.getTextColor());
		result.setFontSize(from.getFontSize());
		result.setVersion(from.getVersion());
		result.setId(from.getId());
		result.setClientId(from.getClientId());
		result.setCd(from.getCustomData());

		List<? extends IUrlLinkRO> links = from.getLinks();
		if (links != null) {
			List<UrlLinkJson> jlinks = new ArrayList<UrlLinkJson>();
			for (IUrlLinkRO link : links) {
				jlinks.add(new UrlLinkJson(link.getUrl(), link.getName()));
			}
			result.setLinks(jlinks);
		}
		return result;
	}
	
	private static ExtensionJson fromDTO(IExtensionRO from) {
		ExtensionJson result = null;
		if (from != null && from.getSvgData() != null) {
			result = new ExtensionJson();
			SvgDataJson svg = new SvgDataJson();
			List<PathJson> paths = new ArrayList<PathJson>();
			for (IPathRO p : from.getSvgData().getPaths()) {
				paths.add(new PathJson(p.getPath(), p.getStyle()));
			}
			svg.setPaths(paths);
			svg.setWidth(from.getSvgData().getWidth());
			svg.setHeight(from.getSvgData().getHeight());
			result.setSvg(svg);
		}
		return result;
	}
}
