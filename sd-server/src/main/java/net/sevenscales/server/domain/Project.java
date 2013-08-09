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
import net.sevenscales.domain.api.IProject;

import org.hibernate.annotations.Cascade;

@SuppressWarnings("serial")
@Entity(name="project")
public class Project implements IProject {
	private Long id;
	private String name;
	private Page dashboard;
//  private List<Ticket> sketches;
  private Boolean publicProject;
	
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

//  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//  @JoinColumn(name="PROJECT_ID")
//  @Sort(type = SortType.COMPARATOR, comparator = OrderedPageComparator.class)
//  @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
//  public SortedSet<OrderedPage> getPages() {
//    return pages;
//  }
//	public void setPages(SortedSet<OrderedPage> pages) {
//		this.pages = pages;
//	}
	
  @OneToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
  @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
  @JoinColumn(name="dashboard_id")
	public Page getDashboard() {
    return dashboard;
  }
	public void setDashboard(Page dashboard) {
    this.dashboard = dashboard;
  }
//	@Override
	public void setDashboard(IPage page) {
	  this.dashboard = (Page) page;
	}
	
	public Boolean isPublicProject() {
	  return publicProject;
	}
	public void setPublicProject(Boolean publicProject) {
	  this.publicProject = publicProject;
	}
	
//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
////  @Sort(type = SortType.COMPARATOR, comparator = OrderedContentComparator.class)
//  @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
//  public List<Ticket> getSketches() {
//    return sketches;
//  } 
//  public void setSketches(List<Ticket> sketches) {
//    this.sketches = sketches;
//  }

	public boolean equals(Object arg0) {
		if (arg0 == null) {
			return false;
		}
		if (arg0 == this) {
			return true;
		}
		
		if ( ((Project) arg0).getId().equals(getId())) {
			return true;
		}
		return false;
	}

//	public IPage getPage(Long pageId) {
//		for (OrderedPage op : pages) {
//			if (op.getPage().getId().equals(pageId)) {
//				return op.getPage();
//			}
//		}
//		return null;
//	}
}
