package net.sevenscales.server.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import net.sevenscales.domain.api.IProperty;


//@Embeddable
@MappedSuperclass
public class Property implements IProperty {
  @Override
  public String toString() {
    return "Property [id=" + id + ", name=" + name + ", type=" + type
        + ", value=" + value + "]";
  }
  private Long id;
  private String name;
  private String type;
  private String value;
  
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
  
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  
}
