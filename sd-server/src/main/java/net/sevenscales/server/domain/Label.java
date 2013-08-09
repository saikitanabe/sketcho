package net.sevenscales.server.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import net.sevenscales.domain.api.ILabel;
import net.sevenscales.domain.api.IProject;

import org.hibernate.validator.NotNull;


@SuppressWarnings("serial")
@Entity(name="label")
public class Label implements ILabel {
	private Long id;
	private String value;
	private String backgroundColor;
	private String textColor;
	private Boolean visible = Boolean.TRUE;
	private Integer orderValue;
	private Project project;
	private List<Page> pages = new ArrayList<Page>();
	
  /* (non-Javadoc)
   * @see net.sevenscales.server.domain.ILabel#getId()
   */
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
   * @see net.sevenscales.server.domain.ILabel#setId(java.lang.Long)
   */
	public void setId(Long id) {
		this.id = id;
	}

  /* (non-Javadoc)
   * @see net.sevenscales.server.domain.ILabel#getPages()
   */
//  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
//  @JoinTable(name="label_page", 
//             joinColumns=@JoinColumn(name="pages_id"))
	@ManyToMany(
      targetEntity=Page.class,
      cascade={CascadeType.PERSIST},
      fetch=FetchType.LAZY
  )
  @JoinTable(
      name="label_page",
      joinColumns=@JoinColumn(name="label_id"),
      inverseJoinColumns=@JoinColumn(name="page_id")
  )
  @OrderBy(value="modifiedTime desc")
	public List<Page> getPages() {
    return pages;
  }
  /* (non-Javadoc)
   * @see net.sevenscales.server.domain.ILabel#setPages(java.util.List)
   */
  public void setPages(List pages) {
    this.pages = pages;
  }
  
  /* (non-Javadoc)
   * @see net.sevenscales.server.domain.ILabel#getValue()
   */
  public String getValue() {
    return value;
  }
  /* (non-Javadoc)
   * @see net.sevenscales.server.domain.ILabel#setValue(java.lang.String)
   */
  public void setValue(String value) {
    this.value = value;
  }
  
  public String getBackgroundColor() {
    return backgroundColor;
  }
  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }
  
  public String getTextColor() {
    return textColor;
  }
  public void setTextColor(String textColor) {
    this.textColor = textColor;
  }
  
  public Boolean getVisible() {
    return visible;
  }
  public void setVisible(Boolean visible) {
    this.visible = visible;
  }
  
  public Integer getOrderValue() {
    return orderValue;
  }
  public void setOrderValue(Integer orderValue) {
    this.orderValue = orderValue;
  }

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="project_id")
  @NotNull
  public Project getProject() {
    return project;
  }
  public void setProject(Project project) {
    this.project = project;
  }
  public void setProject(IProject project) {
    this.project = (Project) project;
  }

	public boolean equals(Object arg0) {
		if (arg0 == null) {
			return false;
		}
		if (arg0 == this) {
			return true;
		}
		
		if ( ((ILabel) arg0).getId().equals(getId())) {
			return true;
		}
		return false;
	}

}
