package net.sevenscales.domain.utils;

import java.util.ArrayList;
import java.util.Collections;

import net.sevenscales.domain.IDiagramItemRO;

public class DiagramItemList extends ArrayList<IDiagramItemRO> {	
  private static final DiagramItemIdComparator DIAGRAM_ITEM_IDENTIFIER_COMPARATOR = new DiagramItemIdComparator();
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
    return Collections.binarySearch(this, item, DIAGRAM_ITEM_IDENTIFIER_COMPARATOR);
  }

}
