package net.sevenscales.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import net.sevenscales.domain.api.ILinkContent;

import org.hibernate.annotations.Type;

@Entity(name="link_content")
public class LinkContent extends Content implements ILinkContent {
  private String link;
  
  @Column
  @Type(type="text")
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
