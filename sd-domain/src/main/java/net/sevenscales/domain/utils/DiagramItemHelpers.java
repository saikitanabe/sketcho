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
		if (diagramItem.getType().equals(ElementType.ACTIVITY) ||
				diagramItem.getType().equals(ElementType.NOTE) ||
				diagramItem.getType().equals(ElementType.TEXT_ITEM)) {
			result = parseRectShape(diagramItem.getShape());
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

	private static Dimension parseUseCase(String shapestr) {
		Dimension result = null;
		int[] shape = parseShape(shapestr);
		if (shape.length == 4) {
			result = new Dimension(shape[2] * 2, shape[3] * 2);
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

  public static String formatShape(int[] shape) {
    String result = "";

    for (int s : shape) {
      if (result.length() > 0) {
        result += ",";
      }
      result += s;
    }

    return result;
  }
}