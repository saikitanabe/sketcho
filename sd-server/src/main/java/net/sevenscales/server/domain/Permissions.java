package net.sevenscales.server.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import net.sevenscales.domain.api.IPermissions;

public class Permissions implements IPermissions {
  private Integer permissions;
  private Long id;
  
  public Permissions() {
  }
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name="ID")
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }

//  @Override
  public Integer getPermissions() {
    return permissions;
  }
  
//  @Override
  public void setPermissions(Integer permissions) {
    this.permissions = permissions;
  }
  
  public boolean equals(Object arg0) {
    if (arg0 == null) {
      return false;
    }
    if (arg0 == this) {
      return true;
    }
    
    if ( ((Permissions) arg0).getId().equals(getId())) {
      return true;
    }
    return false;
  }

}
