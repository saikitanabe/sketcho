package net.sevenscales.editor.api;

import java.util.Map;
import java.util.HashMap;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;

public class LibraryShapes {
	public static Map<ElementType, LibraryShape> shapes;

  public static final int TOP_CLASS_LIKE_PROPERTIES = ShapeProperty.TEXT_POSITION_TOP.getValue() | 
                                                  ShapeProperty.TEXT_RESIZE_DIR_HORIZONTAL.getValue() |
                                                  ShapeProperty.TEXT_TITLE_CENTER.getValue();
  public static final int MIDDLE_CLASS_LIKE_PROPERTIES = ShapeProperty.TEXT_RESIZE_DIR_HORIZONTAL.getValue() | 
                                                         ShapeProperty.CENTERED_TEXT.getValue() |
                                                         ShapeProperty.TEXT_TITLE_CENTER.getValue();

  public static final int BOTTOM_CLASS_LIKE_PROPERTIES = ShapeProperty.TEXT_POSITION_BOTTOM.getValue() | 
                                                         ShapeProperty.CENTERED_TEXT.getValue() |
                                                         ShapeProperty.BOLD_TITLE.getValue() |
                                                         ShapeProperty.TEXT_TITLE_CENTER.getValue();

  public static final int CIRCLE_LIKE_TEXT = ShapeProperty.TEXT_POSITION_MIDDLE.getValue() |
                                             ShapeProperty.TEXT_TITLE_CENTER.getValue() | 
                                             ShapeProperty.BOLD_TITLE.getValue() |
                                             ShapeProperty.CENTERED_TEXT.getValue();                                                         
	static {
		shapes = new HashMap<ElementType, LibraryShape>();

    shapes.put(ElementType.ACTOR, new LibraryShape(ElementType.ACTOR, 48, 60, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.SERVER, new LibraryShape(ElementType.SERVER, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.STORAGE, new LibraryShape(ElementType.STORAGE, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.NOTE, new LibraryShape(ElementType.NOTE, 150, 40, ShapeProperty.TEXT_RESIZE_DIR_VERTICAL.getValue(), 3, 3));
    shapes.put(ElementType.COMPONENT, new LibraryShape(ElementType.COMPONENT, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.ACTIVITY, new LibraryShape(ElementType.ACTIVITY, 109, 40, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.CHOICE, new LibraryShape(ElementType.CHOICE, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.ACTIVITY_START2, new LibraryShape(ElementType.ACTIVITY_START2, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.ACTIVITY_END2, new LibraryShape(ElementType.ACTIVITY_START2, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.FORK_HORIZONTAL, new LibraryShape(ElementType.FORK_HORIZONTAL, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.FORK_VERTICAL, new LibraryShape(ElementType.FORK_VERTICAL, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.PACKAGE, new LibraryShape(ElementType.PACKAGE, 47, 55, TOP_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.MIND_CENTRAL, new LibraryShape(ElementType.MIND_CENTRAL, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3, 18));


	  shapes.put(ElementType.STAR4, new LibraryShape(ElementType.STAR4, 40, 40, BOTTOM_CLASS_LIKE_PROPERTIES));
	  shapes.put(ElementType.STAR5, new LibraryShape(ElementType.STAR5, 40, 40, BOTTOM_CLASS_LIKE_PROPERTIES)); 
	  shapes.put(ElementType.ENVELOPE, new LibraryShape(ElementType.ENVELOPE, 50, 35, BOTTOM_CLASS_LIKE_PROPERTIES));
	  shapes.put(ElementType.TRIANGLE, new LibraryShape(ElementType.TRIANGLE, 40, 40, BOTTOM_CLASS_LIKE_PROPERTIES));
	  shapes.put(ElementType.CLOUD, new LibraryShape(ElementType.CLOUD, 40, 40, CIRCLE_LIKE_TEXT, 3, 3));
	  shapes.put(ElementType.FIREWALL, new LibraryShape(ElementType.FIREWALL, 27, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
	  shapes.put(ElementType.BUBBLE, new LibraryShape(ElementType.BUBBLE, 50, 35, ShapeProperty.TEXT_RESIZE_DIR_VERTICAL.getValue(), 3, 3));
	  shapes.put(ElementType.BUBBLE_R, new LibraryShape(ElementType.BUBBLE_R, 50, 35, ShapeProperty.TEXT_RESIZE_DIR_VERTICAL.getValue(), 3, 3));
	  shapes.put(ElementType.CIRCLE, new LibraryShape(ElementType.CIRCLE, 40, 40, CIRCLE_LIKE_TEXT, 3, 3));
	  shapes.put(ElementType.SMILEY, new LibraryShape(ElementType.SMILEY, 40, 40, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
	  shapes.put(ElementType.POLYGON4, new LibraryShape(ElementType.POLYGON4, 40, 40, CIRCLE_LIKE_TEXT, 3, 3)); 
	  shapes.put(ElementType.POLYGON8, new LibraryShape(ElementType.POLYGON8, 40, 40, CIRCLE_LIKE_TEXT, 3, 3));
    shapes.put(ElementType.ARROW_UP, new LibraryShape(ElementType.ARROW_UP, 20, 40, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
	  shapes.put(ElementType.ARROW_DOWN, new LibraryShape(ElementType.ARROW_DOWN, 20, 40, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
	  shapes.put(ElementType.ARROW_RIGHT, new LibraryShape(ElementType.ARROW_RIGHT, 40, 20, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
	  shapes.put(ElementType.ARROW_LEFT, new LibraryShape(ElementType.ARROW_LEFT, 40, 20, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.IPHONE, new LibraryShape(ElementType.IPHONE, 24, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
	  shapes.put(ElementType.WEB_BROWSER, new LibraryShape(ElementType.WEB_BROWSER, 50, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 4, 4));
    shapes.put(ElementType.RECT, new LibraryShape(ElementType.RECT, 50, 35, TOP_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.CLASS, new LibraryShape(ElementType.CLASS, 50, 35, TOP_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.SEQUENCE, new LibraryShape(ElementType.SEQUENCE, 50, 35, TOP_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.HORIZONTAL_PARTITION, new LibraryShape(ElementType.HORIZONTAL_PARTITION, 50, 35, ShapeProperty.TEXT_POSITION_TOP.getValue() |
      ShapeProperty.DISABLE_SHAPE_AUTO_RESIZE.getValue() |
      ShapeProperty.BOLD_TITLE.getValue(), 2, 2));
    shapes.put(ElementType.VERTICAL_PARTITION, new LibraryShape(ElementType.HORIZONTAL_PARTITION, 50, 35,
      ShapeProperty.TEXT_POSITION_TOP.getValue() |
      ShapeProperty.DISABLE_SHAPE_AUTO_RESIZE.getValue() |
      ShapeProperty.BOLD_TITLE.getValue() |
      ShapeProperty.TEXT_TITLE_CENTER.getValue(), 2, 2));
    shapes.put(ElementType.USE_CASE, new LibraryShape(ElementType.USE_CASE, 50, 35, TOP_CLASS_LIKE_PROPERTIES | ShapeProperty.CENTERED_TEXT.getValue(), 2, 2));
    shapes.put(ElementType.SWITCH, new LibraryShape(ElementType.SWITCH, 50, 35, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.ROUTER, new LibraryShape(ElementType.ROUTER, 50, 35, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.DESKTOP, new LibraryShape(ElementType.DESKTOP, 50, 40, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.LAPTOP, new LibraryShape(ElementType.LAPTOP, 50, 28, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.SERVER2, new LibraryShape(ElementType.SERVER2, 25, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.TABLET_UP, new LibraryShape(ElementType.TABLET_UP, 40, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 4, 4));
    shapes.put(ElementType.TABLET_HORIZONTAL, new LibraryShape(ElementType.TABLET_HORIZONTAL, 50, 35, BOTTOM_CLASS_LIKE_PROPERTIES, 4, 4));
    shapes.put(ElementType.OLD_PHONE, new LibraryShape(ElementType.OLD_PHONE, 24, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.ANDROID, new LibraryShape(ElementType.ANDROID, 28, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.LIGHTBULB, new LibraryShape(ElementType.LIGHTBULB, 45, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
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
    public Integer fontSize;

    LibraryShape(ElementType elementType, int width, int height, int shapeProperties, int duplicateFactoryX, int duplicateFactoryY, Integer fontSize) {
      this.elementType = elementType;
      this.width = width;
      this.height = height;
      this.shapeProperties = shapeProperties;
      this.duplicateFactoryX = duplicateFactoryX;
      this.duplicateFactoryY = duplicateFactoryY;
      this.fontSize = fontSize;
    }

    LibraryShape(ElementType elementType, int width, int height, int shapeProperties, int duplicateFactoryX, int duplicateFactoryY) {
      this(elementType, width, height, shapeProperties, duplicateFactoryX, duplicateFactoryY, null);
    }

    LibraryShape(ElementType elementType, int width, int height) {
      this(elementType, width, height, 0, 1, 1, null);
    }

    LibraryShape(ElementType elementType, int width, int height, int shapeProperties) {
      this(elementType, width, height, shapeProperties, 1, 1, null);
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
    Integer fontSize = null;
    if (s != null) {
      properties = s.shapeProperties;
      fontSize = s.fontSize;
    }

    result.setType(type.getValue());
    result.setShapeProperties(properties);
    result.setFontSize(fontSize);
    return result;
  }

  public static DiagramItemDTO createFrom(IDiagramItemRO item) {
    ElementType type =  ElementType.getEnum(item.getType());
    DiagramItemDTO result = createByType(type);

    // update default values with dynamic properties
    Integer props = result.getShapeProperties();
    props = props | (item.getShapeProperties() & ShapeProperty.DISABLE_SHAPE_AUTO_RESIZE.getValue());
    result.setShapeProperties(props);
    return result;
  }

}