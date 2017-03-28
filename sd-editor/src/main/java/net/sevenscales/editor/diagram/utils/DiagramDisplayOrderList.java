package net.sevenscales.editor.diagram.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.sevenscales.editor.content.utils.DiagramDisplaySorter;
import net.sevenscales.editor.diagram.Diagram;

/**
 * Ordered diagram list.
 */
public class DiagramDisplayOrderList extends ArrayList<Diagram> {
  private Comparator<Diagram> sorter = DiagramDisplaySorter.createDiagramComparator();

	@Override
	public boolean add(Diagram diagram) {
		int index = Collections.binarySearch(this, diagram, sorter);
    if (index < 0) {
    	index = ~index;
    	add(index, diagram);
    	return true;
    }
    return false;
	}
}
