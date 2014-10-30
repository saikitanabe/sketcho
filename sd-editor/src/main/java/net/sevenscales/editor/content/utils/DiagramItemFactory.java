package net.sevenscales.editor.content.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ExtensionDTO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ActivityChoiceShape;
import net.sevenscales.editor.diagram.shape.ActivityEndShape;
import net.sevenscales.editor.diagram.shape.ActivityShape;
import net.sevenscales.editor.diagram.shape.ActivityStartShape;
import net.sevenscales.editor.diagram.shape.ActorShape;
import net.sevenscales.editor.diagram.shape.ComponentShape;
import net.sevenscales.editor.diagram.shape.DbShape;
import net.sevenscales.editor.diagram.shape.EllipseShape;
import net.sevenscales.editor.diagram.shape.FreehandShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.MindCentralShape;
import net.sevenscales.editor.diagram.shape.NoteShape;
import net.sevenscales.editor.diagram.shape.CommentThreadShape;
import net.sevenscales.editor.diagram.shape.CommentShape;
import net.sevenscales.editor.diagram.shape.RectContainerShape;
import net.sevenscales.editor.diagram.shape.HorizontalPartitionShape;
import net.sevenscales.editor.diagram.shape.ForkShape;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.shape.RectShape;
import net.sevenscales.editor.diagram.shape.RelationshipShape2;
import net.sevenscales.editor.diagram.shape.SequenceShape;
import net.sevenscales.editor.diagram.shape.ServerShape;
import net.sevenscales.editor.diagram.shape.ChildTextShape;
import net.sevenscales.editor.diagram.shape.TextShape;
import net.sevenscales.editor.diagram.shape.UMLPackageShape;
import net.sevenscales.editor.diagram.shape.ImageShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.ActivityChoiceElement;
import net.sevenscales.editor.uicomponents.uml.ActivityElement;
import net.sevenscales.editor.uicomponents.uml.ActivityEnd;
import net.sevenscales.editor.uicomponents.uml.ActivityStart;
import net.sevenscales.editor.uicomponents.uml.Actor;
import net.sevenscales.editor.uicomponents.uml.ClassElement2;
import net.sevenscales.editor.uicomponents.uml.ComponentElement;
import net.sevenscales.editor.uicomponents.uml.EllipseElement;
import net.sevenscales.editor.uicomponents.uml.FreehandElement;
import net.sevenscales.editor.uicomponents.uml.MindCentralElement;
import net.sevenscales.editor.uicomponents.uml.NoteElement;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.CommentElement;
import net.sevenscales.editor.uicomponents.uml.RectBoundaryElement;
import net.sevenscales.editor.uicomponents.uml.HorizontalPartitionElement;
import net.sevenscales.editor.uicomponents.uml.ForkElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.SequenceElement;
import net.sevenscales.editor.uicomponents.uml.ServerElement;
import net.sevenscales.editor.uicomponents.uml.StorageElement;
import net.sevenscales.editor.uicomponents.uml.TextElement;
import net.sevenscales.editor.uicomponents.uml.UMLPackageElement;

import com.google.gwt.logging.client.LogConfiguration;

public class DiagramItemFactory {
  private static SLogger logger = SLogger.createLogger(DiagramItemFactory.class);

  static {
    SLogger.addFilter(DiagramItemFactory.class);
  }
  
  private static final String[] LEGACY_TEXT_COLOR = new String[]{"68","68","68","1"};
  private static final String[] LEGACY_BG_COLOR = new String[]{"204","204","255","0"};
  private static final String[] LEGACY_RectBoundaryElemen_BG_COLOR = new String[]{"221","221","221","1"};
  private static final String[] LEGACY_RectBoundaryElemen_BORDER_COLOR = new String[]{"193","193","193","0"};
  
  /**
   * version 3: each item has something background, border colors and text color fields
   * version 4: 
   * - comment fields: p, cby, cbyd, cat, uat
   * - annotation field: a
   * version 5: links
   * version 6: 
   * - relationship supports border colors, legacy border color is interpreted as default rel border color
   */
  public static final int ITEM_VERSION = 6;
  
  public static Diagram create(IDiagramItemRO item, ISurfaceHandler surface, boolean editable, IParentElement parent) {
    // 0, 0 create to exact position
    return DiagramItemFactory.create(0, 0, item, surface, editable, parent);
  }

  public static Diagram create(int moveX, int moveY, IDiagramItemRO item, ISurfaceHandler surface, boolean editable, IParentElement parent) {
    try {
      AbstractDiagramFactory factory = ShapeParser.factory(item);
      Info shape = factory.parseShape(item, moveX, moveY);
      Diagram result = factory.parseDiagram(surface, shape, editable, item, parent);
      return applyDiagramItem(result, item);
    } catch (Exception e) {
      logger.error("Failed to load: " + item.toString(), e);
      return null;
    }
  }

  public static Diagram applyDiagramItem(Diagram result, IDiagramItemRO item) {
    if (result != null) {
      // result.setDiagramItem(item.copy());
      // TODO this could be done in abstract diagram item constructor
      result.parseCustomData(item.getCustomData());
    }
    return result;    
  }

	public static Color parseBackgroundColor(IDiagramItemRO item) {
  	String[] result = new String[]{"240","240","202","1"};
    if (item.getBackgroundColor() != null && !item.getBackgroundColor().equals("")) {
    	String bgValue = item.getBackgroundColor();
    	if (bgValue.indexOf(":") > 0) {
    		bgValue = bgValue.split(":")[0];
    	}
    	result = bgValue.split(",");
    }
    
    if (result.length >= 3 && result[3].equals("0")) { // transparent
      // convert to new color scheme
      return Theme.getColorScheme(ThemeName.PAPER).getBackgroundColor().create();
    }
    return new Color(result);
  }
  
  private static boolean isLegacyColor(String[] color, String[] legacy) {
    int index = 0;
    for (String c : color) {
      if (!c.equals(legacy[index++])) {
        return false;
      }
    }
    return true;
  }

  public static Color parseBorderColor(IDiagramItemRO item) {
  	String[] result = null;
  	String backgroundColor = item.getBackgroundColor();
    if (backgroundColor != null && !backgroundColor.equals("") && backgroundColor.indexOf(":") > 0) {
    	String borderValue = backgroundColor.split(":")[1];
    	if (borderValue != null && borderValue.matches("\\d+,\\d+,\\d+,.+")) {
      	result = borderValue.split(",");
    	}
    }
    if (result == null) {
      // some legacy elements didn't have anything for border color
      return Theme.getColorScheme(ThemeName.PAPER).getBorderColor().create();
    }
    return new Color(result);
  }
  
  public static Color parseTextColor(IDiagramItemRO item) {
  	String[] result = new String[]{"0","0","0","1"};
    if (item.getTextColor() != null && !item.getTextColor().equals("")) {
    	result = item.getTextColor().split(",");
    }

    if (isLegacyColor(result, LEGACY_TEXT_COLOR)) {
      // convert to new color scheme
      return Theme.getColorScheme(ThemeName.PAPER).getTextColor().create();
    }

    return new Color(result);
  }

	public static IDiagramItem createOrUpdate(Diagram diagram) {
    return createOrUpdate(diagram, 0, 0);
  }
  
  public static IDiagramItem createOrUpdate(Diagram diagram, int moveX, int moveY) {
    IDiagramItem result = null;
    Info shape = diagram.getInfo();
    
    String shapetext = "";
    String type = "";

    if (shape instanceof EllipseShape) {
      EllipseShape rs = (EllipseShape) shape;
      result = getItem(diagram);
      shapetext += parseInt(rs.centerX+moveX) + ",";
      shapetext += parseInt(rs.centerY+moveY) + ",";
      shapetext += parseInt(rs.rx) + ",";
      shapetext += parseInt(rs.ry);
      type = ElementType.ELLIPSE.getValue();
    } else if (shape instanceof SequenceShape) {
      SequenceShape ss = (SequenceShape) shape;
      result = getItem(diagram);
      shapetext += parseInt(ss.lifeLineHeight) + " ";
      shapetext += rect2ShapeText(ss.rectShape, moveX, moveY);
      type = ElementType.SEQUENCE.getValue();
    } else if (shape instanceof RectShape) {
      RectShape rs = (RectShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(rs, moveX, moveY);
      type = ElementType.CLASS.getValue();
    } else if (shape instanceof ComponentShape) {
    	ComponentShape rs = (ComponentShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(rs.rectShape, moveX, moveY);
      type = ElementType.COMPONENT.getValue();
    } else if (shape instanceof ServerShape) {
    	ServerShape rs = (ServerShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(rs.rectShape, moveX, moveY);
      type = ElementType.SERVER.getValue();
    } else if (shape instanceof NoteShape) {
      NoteShape note = (NoteShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(note.rectShape, moveX, moveY);
      type = ElementType.NOTE.getValue();
    } else if (shape instanceof CommentThreadShape) {
      CommentThreadShape note = (CommentThreadShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(note.rectShape, moveX, moveY);
      type = ElementType.COMMENT_THREAD.getValue();
    } else if (shape instanceof CommentShape) {
      CommentShape note = (CommentShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(note.rectShape, moveX, moveY);
      type = ElementType.COMMENT.getValue();
    } else if (shape instanceof ChildTextShape) {
      // NOTE ChildTextShape needs to be before TextShape due to inheritance
      ChildTextShape text = (ChildTextShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(text.rectShape, moveX, moveY);
      type = ElementType.CHILD_TEXT.getValue();
    } else if (shape instanceof TextShape) {
      TextShape text = (TextShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(text.rectShape, moveX, moveY);
      type = ElementType.TEXT_ITEM.getValue();
    } else if (shape instanceof ActivityChoiceShape) {
    	ActivityChoiceShape choice = (ActivityChoiceShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(choice.rectShape, moveX, moveY);
      type = ElementType.CHOICE.getValue();
    } else if (shape instanceof ActivityStartShape) {
    	ActivityStartShape start = (ActivityStartShape) shape;
      result = getItem(diagram);
      shapetext += parseInt(start.centerX+moveX) + ",";
      shapetext += parseInt(start.centerY+moveY) + ",";
      shapetext += parseInt(start.radius) + ",";
      type = ElementType.ACTIVITY_START.getValue();
    } else if (shape instanceof ActivityEndShape) {
    	ActivityEndShape end = (ActivityEndShape) shape;
      result = getItem(diagram);
      shapetext += parseInt(end.centerX+moveX) + ",";
      shapetext += parseInt(end.centerY+moveY) + ",";
      shapetext += parseInt(end.radius) + ",";
      type = ElementType.ACTIVITY_END.getValue();
    } else if (shape instanceof ActivityShape) {
    	ActivityShape activity = (ActivityShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(activity.rectShape, moveX, moveY);
      type = ElementType.ACTIVITY.getValue();
    } else if (shape instanceof MindCentralShape) {
    	MindCentralShape activity = (MindCentralShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(activity.rectShape, moveX, moveY);
      type = ElementType.MIND_CENTRAL.getValue();
    } else if (shape instanceof DbShape) {
    	DbShape db = (DbShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(db.rectShape, moveX, moveY);
      type = ElementType.STORAGE.getValue();
    } else if (shape instanceof ActorShape) {
      ActorShape actor = (ActorShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(actor.rectShape, moveX, moveY);
      type = ElementType.ACTOR.getValue();
    }  else if (shape instanceof RelationshipShape2) {
      RelationshipShape2 rs = (RelationshipShape2) shape;
      result = getItem(diagram);
      
      for (int i = 0; i < rs.points.size(); ++i) {
        int p = i % 2 == 0 ? rs.points.get(i)+moveX : rs.points.get(i)+moveY ;
        p = parseInt(p);
        shapetext += p+",";
      }
      // skip last ,
      shapetext = shapetext.substring(0, shapetext.length()-1);
      type = ElementType.RELATIONSHIP.getValue();
    } else if (shape instanceof FreehandShape) {
    	FreehandShape rs = (FreehandShape) shape;
      result = getItem(diagram);
      
      for (int i = 0; i < rs.points.length; ++i) {
        int p = i % 2 == 0 ? rs.points[i]+moveX : rs.points[i]+moveY ;
        p = parseInt(p);
        shapetext += p+",";
      }
      // skip last ,
      shapetext = shapetext.substring(0, shapetext.length()-1);
      type = ElementType.FREEHAND.getValue();
    } else if (shape instanceof UMLPackageShape) {
    	UMLPackageShape rs = (UMLPackageShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(rs.rectShape, moveX, moveY);
      type = ElementType.PACKAGE.getValue();
    } else if (shape instanceof RectContainerShape) {
    	RectContainerShape rs = (RectContainerShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(rs.rectShape, moveX, moveY);
      type = ElementType.VERTICAL_PARTITION.getValue();
    } else if (shape instanceof HorizontalPartitionShape) {
      HorizontalPartitionShape rs = (HorizontalPartitionShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(rs.rectShape, moveX, moveY);
      type = ElementType.HORIZONTAL_PARTITION.getValue();
    } else if (shape instanceof ForkShape) {
      ForkShape s = (ForkShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(s.rectShape, moveX, moveY);
      shapetext += "," +s.orientation;
      type = ElementType.FORK.getValue();
    } else if (shape instanceof GenericShape) {
      GenericShape s = (GenericShape) shape;
      result = getItem(diagram);
      if (result.getExtension() != null) {
        ExtensionDTO ext = new ExtensionDTO(s.getSvgData(), result.getExtension().getLineWeight());
        result.setExtension(ext);
      }
      shapetext += rect2ShapeText(s.rectShape, moveX, moveY);
      // makes sure that type is not manipulated
      type = ElementType.getEnum(result.getType()).getValue();
    } else if (shape instanceof ImageShape) {
      ImageShape s = (ImageShape) shape;
      result = getItem(diagram);
      shapetext += rect2ShapeText(s.rectShape, moveX, moveY);
      type = s.getElementType();
    } 
    
    if (result != null) {
      // focus circle is not any supported type even though it is in surface
      result.setText(diagram.getText());
      result.setShape(shapetext);
      result.setType(type);
      result.setCustomData(diagram.getCustomData());
      String borderColor = "";
      if (shape.getBorderColor() != null && !"".equals(shape.getBorderColor())) {
      	borderColor = ":" + shape.getBorderColor();
      }
      result.setBackgroundColor(shape.getBackgroundColor() + borderColor);
      result.setTextColor(shape.getTextColor());
      result.setVersion(ITEM_VERSION); // data format version
    }
    
    if (LogConfiguration.loggingIsEnabled(Level.FINEST) && result == null && !(diagram instanceof CircleElement)) {
      throw new RuntimeException(SLogger.format("DiagramItemFactory.createOrUpdate failed diagram {}", diagram.toString()));
    }
    
    diagram.setDiagramItem(result);
    return result;
  }

  private native static int parseInt(int p)/*-{
    return parseInt(p);
  }-*/;
  public native static int parseInt(String p)/*-{
    return parseInt(p);
  }-*/;

  private static String rect2ShapeText(RectShape rectShape, int moveX, int moveY) {
    String result = "";
    result += parseInt(rectShape.left+moveX) + ",";
    result += parseInt(rectShape.top+moveY) + ",";
    result += parseInt(rectShape.width) + ",";
    result += parseInt(rectShape.height); 
    return result;
  }
  
  private static IDiagramItem getItem(Diagram diagram) {
    IDiagramItem result = diagram.getDiagramItem();
    if (result == null) {
      // new item
      result = new DiagramItemDTO();
    }
    return result;
  }
}
