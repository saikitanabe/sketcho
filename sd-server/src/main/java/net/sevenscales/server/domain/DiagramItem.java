package net.sevenscales.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import net.sevenscales.domain.api.IDiagramItem;

import org.hibernate.annotations.Type;


@SuppressWarnings("serial")
@Entity(name="diagram_item")
public class DiagramItem implements IDiagramItem {

  private Long id;
	private String text;
  private String type;
	private String shape;
	private DiagramContent diagramContent;
	private Integer version;

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name="ID")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

  @Column
  @Type(type="text")
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
  @Column
  @Type(type="text")
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
	
  @Column
  @Type(type="text")
  public String getShape() {
    return shape;
  }  
  public void setShape(String shape) {
    this.shape = shape;
  }
  
//  @ManyToOne
////  @NotNull // cannot be used because of cascade DELETE_ORPHAN causes not null constraint
//  @JoinColumn(name="CONTENT_ID")
//  public DiagramContent getDiagramContent() {
//    return diagramContent;
//  }
//  public void setDiagramContent(DiagramContent diagramContent) {
//    this.diagramContent = diagramContent;
//  }
//  public void setDiagramContent(IDiagramContent diagramContent) {
//    this.diagramContent = (DiagramContent) diagramContent;
//  }
  
  @Override
  public Integer getVersion() {
    return version;
  }
  @Override
  public void setVersion(Integer version) {
    this.version = version;
  }

  public boolean equals(Object arg0) {
    if (arg0 == null) {
      return false;
    }
    if (arg0 == this) {
      return true;
    }
    
    if ( ((DiagramItem) arg0).getId().equals(getId())) {
      return true;
    }
    return false;
  }

  @Override
  public String getBackgroundColor() {
  	// TODO Auto-generated method stub
  	return null;
  }
  @Override
  public void setBackgroundColor(String backgroundColor) {
  	// TODO Auto-generated method stub
  	
  }
  
  @Override
  public String getTextColor() {
  	// TODO Auto-generated method stub
  	return null;
  }
  @Override
  public void setTextColor(String textColor) {
  	// TODO Auto-generated method stub
  	
  }
	
}
