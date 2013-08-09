package net.sevenscales.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import net.sevenscales.domain.api.ITextLineContent;

import org.hibernate.annotations.Type;

@Entity(name="text_line_content")
public class TextLineContent extends Content implements ITextLineContent {
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
