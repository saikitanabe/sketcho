package net.sevenscales.editor.diagram;

import java.util.List;

public interface DiagramSelectionHandler {

	void selected(List<Diagram> sender);

	void unselectAll();

  void unselect(Diagram sender);

}
