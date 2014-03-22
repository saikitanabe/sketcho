package net.sevenscales.editor.content.utils;

import java.util.Comparator;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.editor.diagram.Diagram;


public class DiagramDisplaySorter {
	private static class DiagramItemComparator implements Comparator<IDiagramItem> {
		@Override
		public int compare(IDiagramItem item1, IDiagramItem item2) {
			Integer o1 = item1.getDisplayOrder();
			o1 = o1 == null ? 0: o1;
			Integer o2 = item2.getDisplayOrder();
			o2 = o2 == null ? 0: o2;

			int result = o1 - o2;
			if (result == 0) {
				// same display order so compare client ids
				result = BoardDocumentHelpers.DIAGRAM_ITEM_IDENTIFIER_COMPARATOR.compare(item1, item2);
			}
			return result;
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
}