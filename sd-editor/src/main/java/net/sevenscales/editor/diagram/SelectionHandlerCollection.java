package net.sevenscales.editor.diagram;

import java.util.ArrayList;
import java.util.List;

public class SelectionHandlerCollection extends ArrayList<DiagramSelectionHandler> {

	public void fireSelection(List<Diagram> senders) {
		for (int i = 0; i < size(); ++i) {
			DiagramSelectionHandler h = (DiagramSelectionHandler) get(i);
			h.selected(senders);
		}
	}

	public void fireUnselectAll() {
		for (int i = 0; i < size(); ++i) {
			DiagramSelectionHandler h = (DiagramSelectionHandler) get(i);
			h.unselectAll();
		}
	}

  public void fireUnselect(Diagram sender) {
    for (DiagramSelectionHandler h : this) {
      h.unselect(sender);
    }
  }

}
