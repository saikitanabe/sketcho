package net.sevenscales.server.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.NotNull;

@Entity(name="page_ordered_content")
public class PageOrderedContent implements IPageOrderedContent, Comparable<IPageOrderedContent> {
	private Long id;
	private Content content;
	private Integer orderValue;
	private Page page;
	
	public PageOrderedContent() {
  }

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
//  @Override
	public Long getId() {
		return id;
	}
//  @Override
	public void setId(Long id) {
		this.id = id;
	}

//  @ManyToOne(fetch=FetchType.LAZY)
  @OneToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
  @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
  @JoinColumn(name="CONTENT_ID")
  @NotNull
  public Content getContent() {
    return content;
  }
  public void setContent(Content content) {
    this.content = content;
  }
//  @Override
  public void setContent(IContent content) {
    this.content = (Content) content;
  }

  public Integer getOrderValue() {
    return orderValue;
  }
  public void setOrderValue(Integer orderValue) {
    this.orderValue = orderValue;
  }

  @ManyToOne(fetch=FetchType.LAZY)
  @NotNull
  @JoinColumn(name="page_id")
  public Page getPage() {
    return page;
  }
  public void setPage(Page page) {
    this.page = page;
  }
//  @Override
  public void setPage(IPage page) {
    this.page = (Page) page;
  }

  public boolean equals(Object arg0) {
    if (arg0 == null) {
      return false;
    }
    if (arg0 == this) {
      return true;
    }
    
    if ( ((PageOrderedContent) arg0).getId().equals(getId())) {
      return true;
    }
    return false;
  }
  
//  @Override
  public int compareTo(IPageOrderedContent arg0) {
    if (getOrderValue() - arg0.getOrderValue() == 0) {
      if (getId() - arg0.getId() == 0) {
        return 0;
      }
      return getId() - arg0.getId() == -1 ? -1 : 1;
    }
    return getOrderValue() - arg0.getOrderValue();
  }

}
