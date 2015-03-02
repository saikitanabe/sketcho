package net.sevenscales.editor.api;

import java.util.Map;
import java.util.HashMap;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.uicomponents.uml.ShapeCache;
import net.sevenscales.editor.uicomponents.uml.ShapeGroup;

public class LibraryShapes {
	public static Map<String, LibraryShape> shapes;

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
		shapes = new HashMap<String, LibraryShape>();

    // for these shapes only sketch type comes from svg, corporate is coded
    // and properties mapping is needed to be for coded types (also SVGs should be updated with correct e.g. data-text-type='horizontal|top|center')
    shapes.put(ElementType.ACTOR.getValue(), new LibraryShape(ElementType.ACTOR, 48, 60, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.SERVER.getValue(), new LibraryShape(ElementType.SERVER, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.STORAGE.getValue(), new LibraryShape(ElementType.STORAGE, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.NOTE.getValue(), new LibraryShape(ElementType.NOTE, 150, 40, ShapeProperty.TEXT_RESIZE_DIR_VERTICAL.getValue(), 3, 3));
    shapes.put(ElementType.COMPONENT.getValue(), new LibraryShape(ElementType.COMPONENT, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.ACTIVITY.getValue(), new LibraryShape(ElementType.ACTIVITY, 109, 40, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.CHOICE.getValue(), new LibraryShape(ElementType.CHOICE, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.ACTIVITY_START2.getValue(), new LibraryShape(ElementType.ACTIVITY_START2, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.ACTIVITY_END2.getValue(), new LibraryShape(ElementType.ACTIVITY_START2, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.FORK_HORIZONTAL.getValue(), new LibraryShape(ElementType.FORK_HORIZONTAL, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.FORK_VERTICAL.getValue(), new LibraryShape(ElementType.FORK_VERTICAL, 47, 55, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.PACKAGE.getValue(), new LibraryShape(ElementType.PACKAGE, 47, 55, TOP_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.MIND_CENTRAL.getValue(), new LibraryShape(ElementType.MIND_CENTRAL, 47, 55, MIDDLE_CLASS_LIKE_PROPERTIES, 3, 3, 18));

    // shapes.put(ElementType.USE_CASE.getValue(), new LibraryShape(ElementType.USE_CASE, 50, 35, TOP_CLASS_LIKE_PROPERTIES | ShapeProperty.CENTERED_TEXT.getValue(), 2, 2));
    // shapes.put(ElementType.CLASS.getValue(), new LibraryShape(ElementType.CLASS, 50, 35, TOP_CLASS_LIKE_PROPERTIES, 2, 2));

    // ALL GENERIC SHAPES HAS TO BE HERE UNTIL TOOLBAR LIBRARY DOESN'T USE THESE
    // NOTE: all data-text-type on svg to match these needs to be fixed if these are remove!!
	  shapes.put(ElementType.STAR4.getValue(), new LibraryShape(ElementType.STAR4, 40, 40, BOTTOM_CLASS_LIKE_PROPERTIES));
	  shapes.put(ElementType.STAR5.getValue(), new LibraryShape(ElementType.STAR5, 40, 40, BOTTOM_CLASS_LIKE_PROPERTIES)); 
	  shapes.put(ElementType.ENVELOPE.getValue(), new LibraryShape(ElementType.ENVELOPE, 50, 35, BOTTOM_CLASS_LIKE_PROPERTIES));
	  shapes.put(ElementType.TRIANGLE.getValue(), new LibraryShape(ElementType.TRIANGLE, 40, 40, BOTTOM_CLASS_LIKE_PROPERTIES));
	  shapes.put(ElementType.CLOUD.getValue(), new LibraryShape(ElementType.CLOUD, 40, 40, CIRCLE_LIKE_TEXT, 3, 3));
	  shapes.put(ElementType.FIREWALL.getValue(), new LibraryShape(ElementType.FIREWALL, 27, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
	  shapes.put(ElementType.BUBBLE.getValue(), new LibraryShape(ElementType.BUBBLE, 50, 35, ShapeProperty.TEXT_RESIZE_DIR_VERTICAL.getValue(), 3, 3));
	  shapes.put(ElementType.BUBBLE_R.getValue(), new LibraryShape(ElementType.BUBBLE_R, 50, 35, ShapeProperty.TEXT_RESIZE_DIR_VERTICAL.getValue(), 3, 3));
	  shapes.put(ElementType.CIRCLE.getValue(), new LibraryShape(ElementType.CIRCLE, 40, 40, CIRCLE_LIKE_TEXT, 3, 3));
	  shapes.put(ElementType.SMILEY.getValue(), new LibraryShape(ElementType.SMILEY, 40, 40, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
	  shapes.put(ElementType.POLYGON4.getValue(), new LibraryShape(ElementType.POLYGON4, 40, 40, CIRCLE_LIKE_TEXT, 3, 3)); 
	  shapes.put(ElementType.POLYGON8.getValue(), new LibraryShape(ElementType.POLYGON8, 40, 40, CIRCLE_LIKE_TEXT, 3, 3));
    shapes.put(ElementType.ARROW_UP.getValue(), new LibraryShape(ElementType.ARROW_UP, 20, 40, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
	  shapes.put(ElementType.ARROW_DOWN.getValue(), new LibraryShape(ElementType.ARROW_DOWN, 20, 40, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
	  shapes.put(ElementType.ARROW_RIGHT.getValue(), new LibraryShape(ElementType.ARROW_RIGHT, 40, 20, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
	  shapes.put(ElementType.ARROW_LEFT.getValue(), new LibraryShape(ElementType.ARROW_LEFT, 40, 20, BOTTOM_CLASS_LIKE_PROPERTIES, 3, 3));
    shapes.put(ElementType.IPHONE.getValue(), new LibraryShape(ElementType.IPHONE, 24, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
	  shapes.put(ElementType.WEB_BROWSER.getValue(), new LibraryShape(ElementType.WEB_BROWSER, 50, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 4, 4));
    shapes.put(ElementType.RECT.getValue(), new LibraryShape(ElementType.RECT, 50, 35, TOP_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.SEQUENCE.getValue(), new LibraryShape(ElementType.SEQUENCE, 50, 35, TOP_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.HORIZONTAL_PARTITION.getValue(), new LibraryShape(ElementType.HORIZONTAL_PARTITION, 50, 35, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.VERTICAL_PARTITION.getValue(), new LibraryShape(ElementType.HORIZONTAL_PARTITION, 50, 35, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.SWITCH.getValue(), new LibraryShape(ElementType.SWITCH, 50, 35, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.ROUTER.getValue(), new LibraryShape(ElementType.ROUTER, 50, 35, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.DESKTOP.getValue(), new LibraryShape(ElementType.DESKTOP, 50, 40, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.LAPTOP.getValue(), new LibraryShape(ElementType.LAPTOP, 50, 28, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.SERVER2.getValue(), new LibraryShape(ElementType.SERVER2, 25, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.TABLET_UP.getValue(), new LibraryShape(ElementType.TABLET_UP, 40, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 4, 4));
    shapes.put(ElementType.TABLET_HORIZONTAL.getValue(), new LibraryShape(ElementType.TABLET_HORIZONTAL, 50, 35, BOTTOM_CLASS_LIKE_PROPERTIES, 4, 4));
    shapes.put(ElementType.OLD_PHONE.getValue(), new LibraryShape(ElementType.OLD_PHONE, 24, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.ANDROID.getValue(), new LibraryShape(ElementType.ANDROID, 28, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
    shapes.put(ElementType.LIGHTBULB.getValue(), new LibraryShape(ElementType.LIGHTBULB, 45, 50, BOTTOM_CLASS_LIKE_PROPERTIES, 2, 2));
	}

  public static LibraryShape getDefaultShape(String type) {
    return shapes.get(type);
  }

  public static class ShapeProps {
    public Integer properties;
    public Integer fontSize;

    private ShapeProps(Integer properties, Integer fontSize) {
      this.properties = properties;
      this.fontSize = fontSize;      
    }
  }

	public static ShapeProps getShapeProps(String type) {
    Integer properties = null;
    Integer fontSize = null;

    LibraryShapes.LibraryShape s = shapes.get(type);

    if (s != null) {
      properties = s.shapeProperties;
      fontSize = s.fontSize;
    } else {
      // TODO temporary solution
      //     LibraryShapes.LibraryShape s = LibraryShapes.get(ElementType.getEnum(type));
      // can be removed

      ShapeGroup sg = ShapeCache.get(type, true);
      if (sg != null) {
        properties = sg.properties;
        fontSize = sg.fontSize;
      }
    }

		return new ShapeProps(properties, fontSize);
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

  public static DiagramItemDTO createByType(String type) {
    DiagramItemDTO result = new DiagramItemDTO();
    ShapeProps sp = getShapeProps(type);

    result.setType(type);
    result.setShapeProperties(sp.properties);
    result.setFontSize(sp.fontSize);
    return result;
  }

  public static DiagramItemDTO createFrom(IDiagramItemRO item) {
    DiagramItemDTO result = createByType(item.getType());

    // update default values with dynamic properties
    Integer props = result.getShapeProperties();
    props = props | (item.getShapeProperties() & ShapeProperty.DISABLE_SHAPE_AUTO_RESIZE.getValue());
    result.setShapeProperties(props);
    return result;
  }

}