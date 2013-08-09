package net.sevenscales.server.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IUserProperties;

import org.hibernate.annotations.Cascade;

@SuppressWarnings("serial")
@Entity(name="user_properties")
public class UserProperties implements IUserProperties {
	private Long id;
  private Long userId;
	private String name;
	private String value;
	
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public Long getUserId() {
	  return userId;
	}
	@Override
	public void setUserId(Long userId) {
	  this.userId = userId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getValue() {
	  return value;
	}
	@Override
	public void setValue(String value) {
	  this.value = value;
	}

	public boolean equals(Object arg0) {
		if (arg0 == null) {
			return false;
		}
		if (arg0 == this) {
			return true;
		}
		
		if ( ((UserProperties) arg0).getId().equals(getId())) {
			return true;
		}
		return false;
	}

}
