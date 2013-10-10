package net.sevenscales.editor.diagram.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.domain.IDiagramItemRO;

public class DiagramItemList extends ArrayList<IDiagramItemRO> {
  public boolean add(IDiagramItemRO item) {
    int index = binarySearch(item);
    if (index < 0) {
      index = ~index;
      add(index, item);
      return true;
    }
    return false;
  }

  public int binarySearch(IDiagramItemRO item) {
    return Collections.binarySearch(this, item, BoardDocumentHelpers.DIAGRAM_ITEM_IDENTIFIER_COMPARATOR);
  }

}
