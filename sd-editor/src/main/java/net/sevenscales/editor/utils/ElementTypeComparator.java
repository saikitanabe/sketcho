package net.sevenscales.editor.utils;

import java.util.Comparator;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.utils.DiagramItemIdComparator;


public class ElementTypeComparator {
	public static class DiagramComparator implements Comparator<Diagram> {
		@Override
		public int compare(Diagram d1, Diagram d2) {
			return ElementTypeComparator.compareDiagramItem(d1.getDiagramItem(), d2.getDiagramItem());
		}
	}

	public static class DiagramItemComparator implements Comparator<IDiagramItemRO> {
		@Override
		public int compare(IDiagramItemRO di1, IDiagramItemRO di2) {
			return ElementTypeComparator.compareDiagramItem(di1, di2);
		}
	}

	public static int compareDiagramItem(IDiagramItemRO di1, IDiagramItemRO di2) {
		// relationship should be after anchored element
		// child text should be after attached relationship
		// otherwise use client id order
		if (ElementType.RELATIONSHIP.getValue().equals(di1.getType()) && 
				ElementType.CHILD_TEXT.getValue().equals(di2.getType())) {
			return -1;
		}
		if (ElementType.CHILD_TEXT.getValue().equals(di1.getType()) && 
				ElementType.RELATIONSHIP.getValue().equals(di2.getType())) {
			return 1;
		}
		if (ElementType.RELATIONSHIP.getValue().equals(di1.getType()) && 
				!ElementType.RELATIONSHIP.getValue().equals(di2.getType())) {
			return 1;
		}
		if (!ElementType.RELATIONSHIP.getValue().equals(di1.getType()) && 
				ElementType.RELATIONSHIP.getValue().equals(di2.getType())) {
			return -1;
		}
		if (ElementType.CHILD_TEXT.getValue().equals(di1.getType()) && 
				!ElementType.CHILD_TEXT.getValue().equals(di2.getType())) {
			return 1;
		}
		if (!ElementType.CHILD_TEXT.getValue().equals(di1.getType()) && 
				ElementType.CHILD_TEXT.getValue().equals(di2.getType())) {
			return -1;
		}

		return DiagramItemIdComparator.compareClientId(di1, di2);
	}

}
