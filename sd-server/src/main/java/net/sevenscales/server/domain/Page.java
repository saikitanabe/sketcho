package net.sevenscales.server.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;
import net.sevenscales.domain.api.IProject;
import net.sf.hibernate4gwt.pojo.java5.SortedAsLastMethod;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.NotNull;


@SuppressWarnings("serial")
@Entity(name="page")
public class Page implements IPage, Comparable<Page> {
	private Long id;
	private Integer type;
	private String name;
  private SortedSet<PageOrderedContent> contentItems = new TreeSet<PageOrderedContent>();
  private Map<String, PageProperty> properties = new HashMap<String, PageProperty>();
  
  // page hierachies
  private Integer orderValue;
  private Page parent;
  private Project project;
  private List<Page> subpages; // = new TreeSet<Page>();
  
  private Long modifiedTime;
  private Long createdTime;
  private String modifier;
  private String creator;
  private List<Label> labels;
  private List<MetaContent> metaContentItems;

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getType() {
	  return type;
	}
	public void setType(Integer type) {
	  this.type = type;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
//  @OneToMany(cascade = CascadeType.ALLfetch = FetchType.LAZY)
	@OneToMany(fetch = FetchType.LAZY, mappedBy="page")
//  @JoinColumn(name="PAGE_ID")
  @Sort(type = SortType.COMPARATOR, comparator = OrderedContentComparator.class)
  @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
//  @OnDelete(action=OnDeleteAction.CASCADE)
  public SortedSet<PageOrderedContent> getContentItems() {
    return contentItems;
  }
  public void setContentItems(SortedSet<PageOrderedContent> contentItems) {
    this.contentItems = contentItems;
  }

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name="PARENT_ID", unique=true)
//  @Sort(type = SortType.COMPARATOR, comparator = PageComparator.class)
  @OrderBy(value="orderValue")
  @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
  // TODO:
  @SortedAsLastMethod
  public List<Page> getSubpages() {
    return subpages;
  }
//  public void setSubpages(List<Page> subpages) {
//    this.subpages = subpages;
//  }
//  @Override
  public void setSubpages(List subpages) {
    this.subpages = (List<Page>) subpages;
  }

  public Integer getOrderValue() {
    return orderValue;
  }
  public void setOrderValue(Integer orderValue) {
    this.orderValue = orderValue;
  } 

  @ManyToOne
  public Page getParent() {
    return parent;
  }
  public void setParent(Page parent) {
    this.parent = parent;
  }
//  @Override
  public void setParent(IPage parent) {
    this.parent = (Page) parent;
  }

  @ManyToOne
  @NotNull
  public Project getProject() {
    return project;
  }
  public void setProject(Project project) {
    this.project = project;
  }
//  @Override
  public void setProject(IProject project) {
    this.project = (Project) project;
  }

  @CollectionOfElements(targetElement=PageProperty.class, fetch=FetchType.EAGER)
  @MapKey(targetElement = String.class)
  @JoinTable(name="page_property_mappings")
  @Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
  public Map<String, PageProperty> getProperties() {
    return properties;
  }
//  public void setProperties(Map<String, PageProperty> properties) {
//    this.properties = properties;
//  }
  public void setProperties(Map properties) {
    this.properties = properties;
  }
  
  public Long getCreatedTime() {
    return createdTime;
  }
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }
  
  public String getCreator() {
    return creator;
  }
  public void setCreator(String creator) {
    this.creator = creator;
  }
  
  public Long getModifiedTime() {
    return modifiedTime;
  }
  public void setModifiedTime(Long modifiedTime) {
    this.modifiedTime = modifiedTime;
  }
  
  public String getModifier() {
    return modifier;
  }
  public void setModifier(String modifier) {
    this.modifier = modifier;
  }
  
//  @ManyToMany(fetch = FetchType.EAGER)
//  @JoinTable(
//        name="label_page",
//        joinColumns=@JoinColumn(name="label_id"),
//        inverseJoinColumns=@JoinColumn(name="pages_id"))
  @ManyToMany(
      cascade = {CascadeType.PERSIST},
      mappedBy = "pages",
      targetEntity = Label.class,
      fetch = FetchType.EAGER
  )
  @OrderBy(value="orderValue")
  public List<Label> getLabels() {
    return labels;
  }
  public void setLabels(List labels) {
    this.labels = labels;
  }
  
  public List<MetaContent> getMetaContentItems() {
    return metaContentItems;
  }
  public void setMetaContentItems(List<MetaContent> metaContentItems) {
    this.metaContentItems = metaContentItems;
  }
  
//  @OneToMany(fetch=FetchType.EAGER)
//  @JoinTable(name="page_properties")
//  public List<Property> getProperties() {
//    return properties;
//  }
//  public void setProperties(List<Property> properties) {
//    this.properties = properties;
//  }

  public IPageOrderedContent findContent(Long id) {
    for (PageOrderedContent c : contentItems) {
      if (c.getId().equals(id)) {
        return c;
      }
    }
    return null;
  }

  public boolean equals(Object arg0) {
    if (arg0 == null) {
      return false;
    }
    if (arg0 == this) {
      return true;
    }
    
    if ( ((Page) arg0).getId().equals(getId())) {
      return true;
    }
    return false;
  }

//  @Override
  public int compareTo(Page arg0) {
    int result = orderValue - arg0.orderValue;
    if (result == 0) {
      result = id.compareTo(arg0.id);
    }
    return result;
  }
}
