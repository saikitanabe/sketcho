package net.sevenscales.domain.api;

import java.util.List;


public interface IDiagramContent extends IContent {  
	public List<IDiagramItem> getDiagramItems();
	public void addItem(IDiagramItem diagramItem);
	public void reset();
	Integer getVersion();
}
