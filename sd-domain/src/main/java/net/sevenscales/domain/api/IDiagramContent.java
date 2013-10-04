package net.sevenscales.domain.api;

import java.util.List;

import net.sevenscales.domain.IDiagramItemRO;

public interface IDiagramContent extends IContent {  
	public List<IDiagramItemRO> getDiagramItems();
	public void addItem(IDiagramItemRO diagramItem);
	public void reset();
	Integer getVersion();
}
