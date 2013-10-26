package net.sevenscales.domain;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.dto.ContentDTO;

import com.google.gwt.user.client.rpc.IsSerializable;


public class DiagramContentDTO extends ContentDTO implements IDiagramContent, IsSerializable {
	private static final long serialVersionUID = 4975000551782219679L;
	private List<IDiagramItemRO> diagramItems = new ArrayList<IDiagramItemRO>();
	// precision will suffer, but should be enough times to update the board...
  private int version = -1;

  public DiagramContentDTO() {
	}
  
	public DiagramContentDTO(IContent content) {
    IDiagramContent dc = (IDiagramContent) content;
    for (IDiagramItemRO di : dc.getDiagramItems()) {
    	addItem(new DiagramItemDTO(di));
    }
	}
	
	public List<IDiagramItemRO> getDiagramItems() {
		return this.diagramItems;
	}
	public void setDiagramItems(List<IDiagramItemRO> diagramItems) {
		this.diagramItems = diagramItems;
	}

	public void addItem(IDiagramItemRO diagramItem) {
		if (!diagramItems.contains(diagramItem)) {
			diagramItems.add(diagramItem);
		}
	}

	public void reset() {
		diagramItems.clear();
	}
	
	public boolean isEmpty() {
		return diagramItems.size() == 0 ? true : false;
	}
	
	public int getVersion() {
    return version;
  }
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public boolean equals(Object content) {
    IDiagramContent dc = (IDiagramContent) content;
    return diagramItems.equals(dc.getDiagramItems());
	}

}
