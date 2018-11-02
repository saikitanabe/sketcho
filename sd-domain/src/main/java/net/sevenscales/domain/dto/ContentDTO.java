package net.sevenscales.domain.dto;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.domain.api.IContent;

public abstract class ContentDTO implements IContent {  
  private static final long serialVersionUID = 117091171912324424L;
  private Long id;
	private String name;
  private Integer width;
  private Integer height;
  private Map<String, ContentPropertyDTO> properties = new HashMap<String, ContentPropertyDTO>();
  
  private String modifier;
  private Long modifiedTime;
  private String creator;
  private Long createdTime;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

  public Integer getWidth() {
    return this.width;
  }
  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return this.height;
  }
  public void setHeight(Integer height) {
    this.height = height;
  }
  
  public Map getProperties() {
    return properties;
  }
  public void setProperties(Map properties) {
    this.properties = properties;
  }

  public Long getCreatedTime() {
    return createdTime;
  }
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }
  
  public String getCreator() {
    return creator;
  }
  public void setCreator(String creator) {
    this.creator = creator;
  }
  
  public Long getModifiedTime() {
    return modifiedTime;
  }
  public void setModifiedTime(Long modifiedTime) {
    this.modifiedTime = modifiedTime;
  }
  
  public String getModifier() {
    return modifier;
  }
  public void setModifier(String modifier) {
    this.modifier = modifier;
  }
  
	public boolean equals(Object arg0) {
		if (arg0 == null) {
			return false;
		}
		if (arg0 == this) {
			return true;
		}
		
		if ( ((IContent) arg0).getId().equals(getId())) {
			return true;
		}
		return false;
	}

}
