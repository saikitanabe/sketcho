package net.sevenscales.confluence.plugins.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sevenscales.domain.PathDTO;
import net.sevenscales.domain.SvgDataDTO;

@XmlRootElement(name = "svg")
@XmlAccessorType(XmlAccessType.FIELD)
public class SvgDataJson {
  // case class SvgData(paths: List[Path], width: Double, height: Double)
  // case class Path(path: String, style: Option[String])

	@XmlElement(name = "paths")
	private List<PathJson> paths = new ArrayList<PathJson>();
  @XmlElement(name = "width")
  private double width = 0;
  @XmlElement(name = "height")
  private double height = 0;
  
  public SvgDataJson() {
  }

  public SvgDataJson(List<PathJson> paths, double width, double height) {
  	this.paths = paths;
  	this.width = width;
    this.height = height;
  }
  
  public List<PathJson> getPaths() {
    return paths;
  }

  public void setPaths(List<PathJson> paths) {
    this.paths = paths;
  }

  public double getWidth() {
    return width;
  }
  public void setWidth(double width) {
    this.width = width;
  }

  public double getHeight() {
    return height;
  }
  public void setHeight(double height) {
    this.height = height;
  }

  public SvgDataDTO asDTO() {
    return new SvgDataDTO(pathsDTO(), width, height);
  }
  
  private List<PathDTO> pathsDTO() {
	List<PathDTO> result = null;
	if (paths != null) {
	  result = new ArrayList<PathDTO>();
	  for (PathJson p : paths) {
	    result.add(p.asDTO());
	  }
	}
	return result;
  }

}
