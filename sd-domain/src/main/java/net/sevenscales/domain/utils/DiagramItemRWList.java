package net.sevenscales.domain.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.DiagramItemIdComparator;

public class DiagramItemRWList<T extends IDiagramItemRO> extends ArrayList<T> {	
  private static final DiagramItemIdComparator DIAGRAM_ITEM_IDENTIFIER_COMPARATOR = new DiagramItemIdComparator();
  public boolean add(T item) {
    int index = binarySearch(item);
    if (index < 0) {
      index = ~index;
      add(index, item);
      return true;
    }
    return false;
  }

  public int binarySearch(T item) {
    return Collections.binarySearch(this, item, DIAGRAM_ITEM_IDENTIFIER_COMPARATOR);
  }

}
