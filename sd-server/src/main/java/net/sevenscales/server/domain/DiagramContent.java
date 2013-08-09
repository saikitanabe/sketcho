package net.sevenscales.server.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;

import org.hibernate.annotations.Cascade;

@Entity(name="diagram_content")
public class DiagramContent extends Content implements IDiagramContent {
	@Override
  public String toString() {
    return "DiagramContent [diagramItems=" + diagramItems + "]";
  }
	
  private Set<DiagramItem> diagramItems;
	
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name="CONTENT_ID")
//  @JoinColumn(name="id")
  @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	public Set<DiagramItem> getDiagramItems() {
		return this.diagramItems;
	}
	public void setDiagramItems(Set<DiagramItem> diagramItems) {
		this.diagramItems = diagramItems;
	}

	public void addItem(DiagramItem diagramItem) {
		diagramItems.add(diagramItem);
	}
  public void addItem(IDiagramItem diagramItem) {
  }
  public void reset() {
  }

//	public void reset() {
//		diagramItems.clear();
//	}
	
//	public boolean isEmpty() {
//		return diagramItems.size() == 0 ? true : false;
//	}

}
