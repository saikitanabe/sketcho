package net.sevenscales.server.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity(name="list_value")
@Inheritance(strategy = InheritanceType.JOINED)
public class ListValue {
  private Long id;
  private String name;
  private Project project;

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
  
  public Project getProject() {
    return project;
  }
  public void setProject(Project project) {
    this.project = project;
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
