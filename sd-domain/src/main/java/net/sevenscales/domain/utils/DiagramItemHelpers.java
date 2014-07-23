package net.sevenscales.domain.utils;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.Dimension;
import net.sevenscales.domain.ElementType;

public class DiagramItemHelpers {
	/**
	* Returns null if doesn't support parsing yet.
	*/
  public static Dimension parseDimension(IDiagramItem diagramItem) {
		Dimension result = null;
		switch (ElementType.getEnum(diagramItem.getType())) {
			case ACTIVITY:
			case NOTE:
			case TEXT_ITEM: {
				result = parseRectShape(diagramItem.getShape());
				break;
			}

		}
		return result;
	}

	private static Dimension parseRectShape(String shapestr) {
		Dimension result = null;

		int[] shape = parseShape(shapestr);
		if (shape.length == 4) {
			result = new Dimension(shape[2], shape[3]);
		}

		return result;
	}

	public static int[] parseShape(String newshape) {
    String shapestr = newshape.replaceFirst("\\s", ",");
    String[] shapeString = shapestr.split(",");
    int[] shape = new int[shapeString.length];
    int i = 0;
    for (String val : shapeString) {
      shape[i] = Integer.valueOf(val);
      i = i + 1;
    }
		return shape;		
	}

}