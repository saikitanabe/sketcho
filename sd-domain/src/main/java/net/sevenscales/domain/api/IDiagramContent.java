package net.sevenscales.domain.api;

import java.util.List;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.js.JsShape;
import com.google.gwt.core.client.JsArray;


public interface IDiagramContent extends IContent {  
	List<IDiagramItemRO> getDiagramItems();
	void addItem(IDiagramItemRO diagramItem);
	void reset();
	int getVersion();

  void setDiagramProperties(Long properties);
  Long getDiagramProperties();
	JsArray<JsShape> getLibrary();
}
