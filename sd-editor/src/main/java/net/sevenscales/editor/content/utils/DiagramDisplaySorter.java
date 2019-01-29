package net.sevenscales.editor.content.utils;

import java.util.Comparator;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.editor.diagram.Diagram;


public class DiagramDisplaySorter {
	public static int compare(IDiagramItem item1, IDiagramItem item2) {
		if (item1 == null || item2 == null) {
			// circle elements doen't have diagram items
			return 0;
		}
		Integer o1 = item1.getDisplayOrder();
		o1 = o1 == null ? 0 : o1;
		Integer o2 = item2.getDisplayOrder();
		o2 = o2 == null ? 0 : o2;

		int result = o1 - o2;
		if (result == 0) {
			// same display order so compare client ids
			result = BoardDocumentHelpers.DIAGRAM_ITEM_IDENTIFIER_COMPARATOR.compare(item1, item2);
		}
		return result;
	}

	public static int compare(Diagram diagram1, Diagram diagram2) {
		return DiagramDisplaySorter.compare(diagram1.getDiagramItem(), diagram2.getDiagramItem());
	}

	private static class DiagramItemComparator implements Comparator<IDiagramItem> {
		@Override
		public int compare(IDiagramItem item1, IDiagramItem item2) {
			return DiagramDisplaySorter.compare(item1, item2);
		}
  }

  private static class DiagramComparator implements Comparator<Diagram> {
  	private DiagramItemComparator itemComparator = new DiagramItemComparator();

		@Override
		public int compare(Diagram diagram1, Diagram diagram2) {
			return itemComparator.compare(diagram1.getDiagramItem(), diagram2.getDiagramItem());
		}
  }

	public static Comparator<IDiagramItem> createDiagramItemComparator() {
		return new DiagramItemComparator();
	}

	public static Comparator<Diagram> createDiagramComparator() {
		return new DiagramComparator();
	}


	/**
	 * Sorts parents before child items.
	 */
	private static class DiagramItemParentSorter implements Comparator<IDiagramItemRO> {
		@Override
		public int compare(IDiagramItemRO item1, IDiagramItemRO item2) {
			if (item1.getParentId() == null && 
					item2.getParentId() == null) {
				return 0;
			}
			if (item1.getParentId() == null &&
					item2.getParentId() != null) {
				return -1;
			}
			if (item1.getParentId() != null &&
					item2.getParentId() == null) {
			return 1;
		}
		// child is returned after the parent
			return 0;
		}
  }

	public static Comparator<IDiagramItemRO> createDiagramItemSortParentComparator() {
		return new DiagramItemParentSorter();
	}

}
