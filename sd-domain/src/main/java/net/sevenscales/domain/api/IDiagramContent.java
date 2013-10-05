package net.sevenscales.domain.api;

import java.util.List;

import net.sevenscales.domain.IDiagramItemRO;

public interface IDiagramContent extends IContent {  
	List<IDiagramItemRO> getDiagramItems();
	void addItem(IDiagramItemRO diagramItem);
	void reset();
	int getVersion();
}
