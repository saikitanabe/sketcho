package net.sevenscales.editor.api;

import java.util.Map;
import java.util.HashMap;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.DiagramItemDTO;

public class LibraryShapes {
	public static Map<ElementType, LibraryShape> shapes;

  public static final int CLASS_LIKE_PROPERTIES = ShapeProperty.TEXT_POSITION_TOP.getValue() | ShapeProperty.TEXT_RESIZE_DIR_HORIZONTAL.getValue();
  public static final int MIDDLE_CLASS_LIKE_PROPERTIES = ShapeProperty.TEXT_RESIZE_DIR_HORIZONTAL.getValue() | 
                                                         ShapeProperty.CENTERED_TEXT.getValue();

  public static final int BOTTOM_CLASS_LIKE_PROPERTIES = ShapeProperty.TEXT_POSITION_BOTTOM.getValue() | 
                                                         ShapeProperty.CENTERED_TEXT.getValue() |
                                                         ShapeProperty.BOLD_TITLE.getValue();


	static {
		shapes = new HashMap<ElementType, LibraryShape>();

    shapes.put(ElementType.ACTOR, new LibraryShape(ElementType.ACTOR, 48, 60, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.SERVER, new LibraryShape(ElementType.SERVER, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.STORAGE, new LibraryShape(ElementType.STORAGE, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.NOTE, new LibraryShape(ElementType.NOTE, 47, 55, ShapeProperty.TEXT_RESIZE_DIR_VERTICAL.getValue(), 3, 3));
    shapes.put(ElementType.COMPONENT, new LibraryShape(ElementType.COMPONENT, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.ACTIVITY, new LibraryShape(ElementType.ACTIVITY, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.CHOICE, new LibraryShape(ElementType.CHOICE, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.ACTIVITY_START2, new LibraryShape(ElementType.ACTIVITY_START2, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.ACTIVITY_END2, new LibraryShape(ElementType.ACTIVITY_START2, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.FORK_HORIZONTAL, new LibraryShape(ElementType.FORK_HORIZONTAL, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.FORK_VERTICAL, new LibraryShape(ElementType.FORK_VERTICAL, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));

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
	  shapes.put(ElementType.ARROW_RIGHT, new LibraryShape(ElementType.ARROW_RIGHT, 40, 20, ShapeProperty.DISABLE_SHAPE_AUTO_RESIZE.getValue(), 3, 3));
	  shapes.put(ElementType.ARROW_LEFT, new LibraryShape(ElementType.ARROW_LEFT, 40, 20, ShapeProperty.DISABLE_SHAPE_AUTO_RESIZE.getValue(), 3, 3));
	  shapes.put(ElementType.IPHONE, new LibraryShape(ElementType.IPHONE, 24, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 12, 12));
	  shapes.put(ElementType.WEB_BROWSER, new LibraryShape(ElementType.WEB_BROWSER, 50, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 12, 12));
    shapes.put(ElementType.RECT, new LibraryShape(ElementType.RECT, 50, 35, CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.CLASS, new LibraryShape(ElementType.CLASS, 50, 35, CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.SEQUENCE, new LibraryShape(ElementType.CLASS, 50, 35, CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.HORIZONTAL_PARTITION, new LibraryShape(ElementType.HORIZONTAL_PARTITION, 50, 35, ShapeProperty.TEXT_POSITION_TOP.getValue() |
      // ShapeProperty.CENTERED_TEXT.getValue() |
      ShapeProperty.DISABLE_SHAPE_AUTO_RESIZE.getValue() |
      ShapeProperty.BOLD_TITLE.getValue(), 2, 2));
    shapes.put(ElementType.USE_CASE, new LibraryShape(ElementType.USE_CASE, 50, 35, CLASS_LIKE_PROPERTIES | ShapeProperty.CENTERED_TEXT.getValue(), 2, 2));
    shapes.put(ElementType.SWITCH, new LibraryShape(ElementType.SWITCH, 50, 35, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 2, 2));
    shapes.put(ElementType.ROUTER, new LibraryShape(ElementType.ROUTER, 50, 35, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 2, 2));
    shapes.put(ElementType.DESKTOP, new LibraryShape(ElementType.DESKTOP, 50, 40, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 2, 2));
    shapes.put(ElementType.LAPTOP, new LibraryShape(ElementType.LAPTOP, 50, 28, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 2, 2));
    shapes.put(ElementType.SERVER2, new LibraryShape(ElementType.SERVER2, 25, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 2, 2));
    shapes.put(ElementType.TABLET_UP, new LibraryShape(ElementType.TABLET_UP, 40, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 2, 2));
    shapes.put(ElementType.TABLET_HORIZONTAL, new LibraryShape(ElementType.TABLET_HORIZONTAL, 50, 35, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 2, 2));
    shapes.put(ElementType.OLD_PHONE, new LibraryShape(ElementType.OLD_PHONE, 24, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 2, 2));
    shapes.put(ElementType.ANDROID, new LibraryShape(ElementType.ANDROID, 26, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 2, 2));
    shapes.put(ElementType.LIGHTBULB, new LibraryShape(ElementType.LIGHTBULB, 45, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 2, 2));
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

  public static DiagramItemDTO createByType(ElementType type) {
    DiagramItemDTO result = new DiagramItemDTO();
    LibraryShapes.LibraryShape s = LibraryShapes.get(type);
    Integer properties = null;
    if (s != null) {
      properties = s.shapeProperties;
    }

    result.setType(type.getValue());
    result.setShapeProperties(properties);
    return result;
  }

}