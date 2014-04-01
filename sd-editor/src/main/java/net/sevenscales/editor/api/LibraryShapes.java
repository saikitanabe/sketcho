package net.sevenscales.editor.api;

import java.util.Map;
import java.util.HashMap;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;

public class LibraryShapes {
	public static Map<ElementType, LibraryShape> shapes;

	static {
		shapes = new HashMap<ElementType, LibraryShape>();
	  shapes.put(ElementType.STAR4, new LibraryShape(ElementType.STAR4, 40, 40));
	  shapes.put(ElementType.STAR5, new LibraryShape(ElementType.STAR5, 40, 40)); 
	  shapes.put(ElementType.ENVELOPE, new LibraryShape(ElementType.ENVELOPE, 50, 35, ShapeProperty.TEXT_POSITION_BOTTOM.getValue()));
	  shapes.put(ElementType.TRIANGLE, new LibraryShape(ElementType.TRIANGLE, 40, 40, ShapeProperty.TEXT_POSITION_BOTTOM.getValue()));
	  shapes.put(ElementType.CLOUD, new LibraryShape(ElementType.CLOUD, 40, 40, 0, 3, 3));
	  shapes.put(ElementType.FIREWALL, new LibraryShape(ElementType.FIREWALL, 27, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 3, 3));
	  shapes.put(ElementType.BUBBLE, new LibraryShape(ElementType.BUBBLE, 50, 35, ShapeProperty.TEXT_RESIZE_DIR_VERTICAL.getValue(), 3, 3));
	  shapes.put(ElementType.BUBBLE_R, new LibraryShape(ElementType.BUBBLE_R, 50, 35, ShapeProperty.TEXT_RESIZE_DIR_VERTICAL.getValue(), 3, 3));
	  shapes.put(ElementType.CIRCLE, new LibraryShape(ElementType.CIRCLE, 40, 40, 0, 3, 3));
	  shapes.put(ElementType.SMILEY, new LibraryShape(ElementType.SMILEY, 40, 40, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 3, 3));
	  shapes.put(ElementType.POLYGON4, new LibraryShape(ElementType.POLYGON4, 40, 40, 0, 3, 3)); 
	  shapes.put(ElementType.POLYGON8, new LibraryShape(ElementType.POLYGON8, 40, 40, 0, 3, 3));
	  shapes.put(ElementType.ARROW_UP, new LibraryShape(ElementType.ARROW_UP, 20, 40, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 3, 3));
	  shapes.put(ElementType.ARROW_DOWN, new LibraryShape(ElementType.ARROW_DOWN, 20, 40, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 3, 3));
	  shapes.put(ElementType.ARROW_RIGHT, new LibraryShape(ElementType.ARROW_RIGHT, 40, 20, ShapeProperty.SHAPE_AUTO_RESIZE_FALSE.getValue(), 3, 3));
	  shapes.put(ElementType.ARROW_LEFT, new LibraryShape(ElementType.ARROW_LEFT, 40, 20, ShapeProperty.SHAPE_AUTO_RESIZE_FALSE.getValue(), 3, 3));
	  shapes.put(ElementType.IPHONE, new LibraryShape(ElementType.IPHONE, 24, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 12, 12));
	  shapes.put(ElementType.WEB_BROWSER, new LibraryShape(ElementType.WEB_BROWSER, 50, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 12, 12));
	  shapes.put(ElementType.RECT, new LibraryShape(ElementType.RECT, 50, 35, 0, 2, 2));
	}

	public static LibraryShape get(ElementType type) {
		return shapes.get(type);
	}

  public static class LibraryShape {
    public ElementType elementType;
    public int shapeProperties;
    public int width;
    public int height;
    public int duplicateFactoryX;
    public int duplicateFactoryY;

    LibraryShape(ElementType elementType, int width, int height, int shapeProperties, int duplicateFactoryX, int duplicateFactoryY) {
      this.elementType = elementType;
      this.width = width;
      this.height = height;
      this.shapeProperties = shapeProperties;
      this.duplicateFactoryX = duplicateFactoryX;
      this.duplicateFactoryY = duplicateFactoryY;
    }

    LibraryShape(ElementType elementType, int width, int height) {
      this(elementType, width, height, 0, 1, 1);
    }

    LibraryShape(ElementType elementType, int width, int height, int shapeProperties) {
      this(elementType, width, height, shapeProperties, 1, 1);
    }

    // LibraryShape(ElementType elementType, int shapeProperties) {
    //   this(elementType, 0, 0, shapeProperties, 1, 1);
    // }

    // LibraryShape(ElementType elementType) {
    //   this(elementType, 0, 0, 0, 1, 1);
    // }

  }
}