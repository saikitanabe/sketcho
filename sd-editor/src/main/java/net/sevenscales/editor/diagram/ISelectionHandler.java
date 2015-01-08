package net.sevenscales.editor.diagram;

import java.util.Set;

public interface ISelectionHandler {
  public void moveSelected(int x, int y);
  public Set<Diagram> getSelectedItems();
  public void unselectAll();
  void movedMaybeGroup(int dx, int dy);
}
