package net.sevenscales.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import net.sevenscales.domain.api.ITextContent;

import org.hibernate.annotations.Type;

@Entity(name="text_content")
public class TextContent extends Content implements ITextContent {
  private String text;
  
  @Column
  @Type(type="text")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
