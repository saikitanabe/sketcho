package net.sevenscales.server.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class MetaContent {
  private Long id;

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  public boolean equals(Object arg0) {
    if (arg0 == null) {
      return false;
    }
    if (arg0 == this) {
      return true;
    }
    
    if ( ((MetaContent) arg0).getId().equals(getId())) {
      return true;
    }
    return false;
  }

}
