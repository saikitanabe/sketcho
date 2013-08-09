package net.sevenscales.server.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.api.ITextContent;
import net.sevenscales.domain.api.ITicket;

@Entity
public class Ticket implements ITicket {
	private Long id;
	private String summary;
	private String link;
	private String state;
	private TextContent description;
	private DiagramContent diagram;
	private Project project;
  private Long modifiedTime;
  private Long createdTime;
  private String modifier;

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
	
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//  @Override
	public TextContent getDescription() {
    return description;
  }	
  public void setDescription(TextContent description) {
    this.description = description;
  }
//  @Override
  public void setDescription(ITextContent description) {
    this.description = (TextContent) description;
  }
	
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public DiagramContent getDiagram() {
    return diagram;
  }
	public void setDiagram(DiagramContent diagram) {
    this.diagram = diagram;
  }
//  @Override
  public void setDiagram(IContent diagram) {
    this.diagram = (DiagramContent) diagram;
  }
	
	public String getLink() {
    return link;
  }
	public void setLink(String link) {
    this.link = link;
  }
	
	public String getState() {
    return state;
  }
	public void setState(String state) {
    this.state = state;
  }
	
	public String getSummary() {
    return summary;
  }
	public void setSummary(String summary) {
    this.summary = summary;
  }
	
//	@Override
  @ManyToOne	
	public Project getProject() {
	  return project;
	}
//	@Override
	public void setProject(IProject project) {
	  this.project = (Project) project;
	}
	public void setProject(Project project) {
	  this.project = project;
	}
	
//	@Override
	public Long getModifiedTime() {
	  return modifiedTime;
	}
//	@Override
	public void setModifiedTime(Long date) {
	  this.modifiedTime = date;
	}

  public void setCreatedTime(Long date) {
    this.createdTime = date;
  }

  public Long getCreatedTime() {
    return this.createdTime;
  }

  public void setModifier(String modifier) {
    this.modifier = modifier;
  }
  
  public String getModifier() {
    return this.modifier;
  }
  
  public boolean equals(Object arg0) {
    if (arg0 == null) {
      return false;
    }
    if (arg0 == this) {
      return true;
    }
    
    if ( ((Ticket) arg0).getId().equals(getId())) {
      return true;
    }
    return false;
  }

}
