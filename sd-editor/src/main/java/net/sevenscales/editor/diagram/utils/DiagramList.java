package net.sevenscales.editor.diagram.utils;

import java.util.ArrayList;

import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.editor.diagram.Diagram;

/**
 * Ordered diagram list.
 */
public class DiagramList extends ArrayList<Diagram> {
	// TODO miten hanskataan CircleElementin laitto??; ei ole client id:tä!!
	@Override
	public boolean add(Diagram diagram) {
		int index = BoardDocumentHelpers.binarySearch(this, diagram);
    if (index < 0) {
    	index = ~index;
    	add(index, diagram);
    	return true;
    }
    return false;
	}
}
