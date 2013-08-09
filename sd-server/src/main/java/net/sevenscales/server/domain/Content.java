package net.sevenscales.server.domain;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;

import net.sevenscales.domain.api.IContent;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;


//@MappedSuperclass
@Entity(name="content")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Content implements IContent {
  @Override
  public String toString() {
    return "Content [createdTime=" + createdTime + ", creator=" + creator
        + ", height=" + height + ", id=" + id + ", modifiedTime="
        + modifiedTime + ", modifier=" + modifier + ", name=" + name
        + ", properties=" + properties + ", width=" + width + "]";
  }

  private Long id;
  private String name;
  private Integer width;
  private Integer height;
  private Map<String, ContentProperty> properties = new HashMap<String, ContentProperty>();
  
  private Long modifiedTime;
  private Long createdTime;
  private String modifier;
  private String creator;

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
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
  
  @CollectionOfElements(targetElement=ContentProperty.class, fetch=FetchType.EAGER)
  @MapKey(targetElement = String.class)
  @JoinTable(name="content_property_mappings")
  @Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
  public Map<String, ContentProperty> getProperties() {
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
    
    if ( ((Content) arg0).getId().equals(getId())) {
      return true;
    }
    return false;
  }
  
}
