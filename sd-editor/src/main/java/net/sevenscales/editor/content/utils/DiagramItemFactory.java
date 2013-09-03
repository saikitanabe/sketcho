package net.sevenscales.editor.content.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.SLogger;
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
import net.sevenscales.editor.diagram.shape.RectContainerShape;
import net.sevenscales.editor.diagram.shape.RectShape;
import net.sevenscales.editor.diagram.shape.RelationshipShape2;
import net.sevenscales.editor.diagram.shape.SequenceShape;
import net.sevenscales.editor.diagram.shape.ServerShape;
import net.sevenscales.editor.diagram.shape.TextShape;
import net.sevenscales.editor.diagram.shape.UMLPackageShape;
import net.sevenscales.editor.gfx.domain.Color;
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
import net.sevenscales.editor.uicomponents.uml.RectBoundaryElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.SequenceElement;
import net.sevenscales.editor.uicomponents.uml.ServerElement;
import net.sevenscales.editor.uicomponents.uml.StorageElement;
import net.sevenscales.editor.uicomponents.uml.TextElement;
import net.sevenscales.editor.uicomponents.uml.UMLPackageElement;

import com.google.gwt.logging.client.LogConfiguration;

public class DiagramItemFactory {
  private static SLogger logger = SLogger.createLogger(DiagramItemFactory.class);
  
  private static final String[] LEGACY_TEXT_COLOR = new String[]{"68","68","68","1"};
  private static final String[] LEGACY_BG_COLOR = new String[]{"204","204","255","0"};
  private static final String[] LEGACY_RectBoundaryElemen_BG_COLOR = new String[]{"221","221","221","1"};
  private static final String[] LEGACY_RectBoundaryElemen_BORDER_COLOR = new String[]{"193","193","193","0"};
  
  /**
   * version 3: each item has something background, border colors and text color fields
   */
  public static final int ITEM_VERSION = 3;
  
  public static Diagram create(IDiagramItemRO item, ISurfaceHandler surface, boolean editable) {
    Diagram result = null;
    if (item.getType().equals("ellipseitem")) {
      String[] s = item.getShape().split(",");
      int cx = parseInt(s[0]);
      int cy = parseInt(s[1]);
      int rx = parseInt(s[2]);
      int ry = parseInt(s[3]);
      EllipseElement ee = new EllipseElement(surface,
          new EllipseShape(cx, 
              cy,
              rx,
              ry),
              item.getText(),
              parseBackgroundColor(item),
              parseBorderColor(item),
              parseTextColor(item),
          editable);
      result = ee;
    } else if (item.getType().equals("sequenceitem")) {
      String[] s1 = item.getShape().split(" ");
      int lifeline = parseInt(s1[0]);
      String[] s2 = s1[1].split(",");
      int x = parseInt(s2[0]);
      int y = parseInt(s2[1]);
      int width = parseInt(s2[2]);
      int height = parseInt(s2[3]);
      SequenceElement se = new SequenceElement(surface,
          new SequenceShape(x, y, width, height, lifeline),
              item.getText(),
              parseBackgroundColor(item),
              parseBorderColor(item),
              parseTextColor(item),
          editable);
      result = se;
    } else if (item.getType().equals("comp")) {
      String[] s = item.getShape().split(",");
      ComponentElement ce = new ComponentElement(surface,
          new ComponentShape(s), 
          item.getText(), 
          parseBackgroundColor(item),
          parseBorderColor(item),
          parseTextColor(item), editable);
      result = ce;
    } else if (item.getType().equals("server")) {
      String[] s = item.getShape().split(",");
      ServerElement ce = new ServerElement(surface,
          new ServerShape(s), 
          item.getText(), 
          parseBackgroundColor(item),
          parseBorderColor(item),
          parseTextColor(item), editable);
      result = ce;
    } else if (item.getType().equals("classitem")) {
      String[] s = item.getShape().split(",");
      ClassElement2 ce = new ClassElement2(surface,
          new RectShape(s), 
          item.getText(), 
          parseBackgroundColor(item),
          parseBorderColor(item),
          parseTextColor(item), editable);
      result = ce;
    } else if (item.getType().equals("noteitem")) {
      String[] s = item.getShape().split(",");
      int x = parseInt(s[0]);
      int y = parseInt(s[1]);
      int width = parseInt(s[2]);
      int height = parseInt(s[3]);
      NoteElement ne = new NoteElement(surface,
          new NoteShape(x, 
              y,
              width,
              height),
              item.getText(),
              parseBackgroundColor(item),
              parseBorderColor(item),
              parseTextColor(item),
          editable);
      result = ne;
    } else if (item.getType().equals("choice")) {
      String[] s = item.getShape().split(",");
      int x = parseInt(s[0]);
      int y = parseInt(s[1]);
      int width = parseInt(s[2]);
      int height = parseInt(s[3]);
      ActivityChoiceElement choice = new ActivityChoiceElement(surface,
          new ActivityChoiceShape(x, 
              y,
              width,
              height),
              item.getText(), 
              parseBackgroundColor(item),
              parseBorderColor(item),
              parseTextColor(item),
          editable);
      result = choice;
    } else if (item.getType().equals("activitystart")) {
      String[] s = item.getShape().split(",");
      int cx = parseInt(s[0]);
      int cy = parseInt(s[1]);
      int r = parseInt(s[2]);
      ActivityStart ee = null;
      if (item.getVersion() >= 3) {
        ee = new ActivityStart(surface,
            new ActivityStartShape(cx, 
                cy,
                r),
            parseBackgroundColor(item),
            parseBorderColor(item),
            parseTextColor(item),
            editable);
      } else {
        // legacy activity start colors => update to new color scheme
        ElementColorScheme paperScheme = Theme.getColorScheme(ThemeName.PAPER);
        ee = new ActivityStart(surface,
            new ActivityStartShape(cx, 
                cy,
                r),
            paperScheme.getBorderColor().create(),
            paperScheme.getBorderColor().create(),
            paperScheme.getTextColor().create(),
            editable);
      }
      result = ee;
    } else if (item.getType().equals("activityend")) {
      String[] s = item.getShape().split(",");
      int cx = parseInt(s[0]);
      int cy = parseInt(s[1]);
      int r = parseInt(s[2]);
      ActivityEnd ee = new ActivityEnd(surface,
          new ActivityEndShape(cx, 
              cy,
              r),
          editable);
      result = ee;
    } else if (item.getType().equals("activity")) {
      String[] s = item.getShape().split(",");
      int x = parseInt(s[0]);
      int y = parseInt(s[1]);
      int width = parseInt(s[2]);
      int height = parseInt(s[3]);
      ActivityElement choice = new ActivityElement(surface,
          new ActivityShape(x, 
              y,
              width,
              height),
              item.getText(),
              parseBackgroundColor(item),
              parseBorderColor(item),
              parseTextColor(item),
          editable);
      result = choice;
    } else if (item.getType().equals("centtop")) {
      String[] s = item.getShape().split(",");
      int x = parseInt(s[0]);
      int y = parseInt(s[1]);
      int width = parseInt(s[2]);
      int height = parseInt(s[3]);
      MindCentralElement choice = new MindCentralElement(surface,
          new MindCentralShape(x, 
              y,
              width,
              height),
              item.getText(),
              parseBackgroundColor(item),
              parseBorderColor(item),
              parseTextColor(item),
          editable);
      result = choice;
    } else if (item.getType().equals("storage")) {
      String[] s = item.getShape().split(",");
      int x = parseInt(s[0]);
      int y = parseInt(s[1]);
      int width = parseInt(s[2]);
      int height = parseInt(s[3]);
      StorageElement db = new StorageElement(surface,
          new DbShape(x, 
              y,
              width,
              height),
              item.getText(),
              parseBackgroundColor(item),
              parseBorderColor(item),
              parseTextColor(item),
          editable);
      result = db;
    } else if (item.getType().equals("textitem")) {
      String[] s = item.getShape().split(",");
      int x = parseInt(s[0]);
      int y = parseInt(s[1]);
      int width = parseInt(s[2]);
      int height = parseInt(s[3]);
      TextElement ne = new TextElement(surface,
          new TextShape(x, 
              y,
              width,
              height),
              parseBackgroundColor(item),
              parseBorderColor(item),
              parseTextColor(item),
              item.getText(),
          editable);
      result = ne;
    } else if (item.getType().equals("actoritem")) {
      String[] s = item.getShape().split(",");
      int x = parseInt(s[0]);
      int y = parseInt(s[1]);
      int width = parseInt(s[2]);
      int height = parseInt(s[3]);
      Actor ne = new Actor(surface,
          new ActorShape(x, 
              y,
              width,
              height),
              item.getText(),
              parseBackgroundColor(item),
              parseBorderColor(item),
              parseTextColor(item),
          editable);
      result = ne;
    } else if (item.getType().equals("relationship")) {
      List<Integer> points = new ArrayList<Integer>();
      String[] ps = item.getShape().split(",");
      for (String p : ps) {
        points.add(parseInt(p));
      }
      Relationship2 r = new Relationship2(surface, points, item.getText(), editable);
      result = r;
    } else if (item.getType().equals("freehand")) {
      String[] ps = item.getShape().split(",");
      int[] points = new int[ps.length];
      int i = 0;
      for (String p : ps) {
        points[i++] = (parseInt(p));
      }
      FreehandElement r = new FreehandElement(
      		surface, 
      		new FreehandShape(points), 
      		parseBackgroundColor(item), 
      		parseBorderColor(item),
      		parseTextColor(item),
      		editable);
      result = r;
    } else if (item.getType().equals("package")) {
      UMLPackageElement packagee = new UMLPackageElement(surface,
          new UMLPackageShape(item.getShape().split(",")),
              item.getText(),
              parseBackgroundColor(item),
              parseBorderColor(item),
              parseTextColor(item),
          editable);
      result = packagee;
    } else if (item.getType().equals("rectcont")) {
      RectBoundaryElement packagee = new RectBoundaryElement(surface,
          new RectContainerShape(item.getShape().split(",")),
              item.getText(),
              parseBackgroundColor(item),
              parseBorderColor(item),
              parseTextColor(item),
          editable);
      result = packagee;
    }
    result.setDiagramItem(item.copy());
    result.parseCustomData(item.getCustomData());
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

	public static IDiagramItem createOrUpdate(Diagram diagram, boolean forceCreate) {
    return createOrUpdate(diagram, forceCreate, 0, 0);
  }
  
  public static IDiagramItem createOrUpdate(Diagram diagram, boolean forceCreate, int moveX, int moveY) {
    IDiagramItem result = null;
    Info shape = diagram.getInfo();
    
    String shapetext = "";
    String type = "";

    if (shape instanceof EllipseShape) {
      EllipseShape rs = (EllipseShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += parseInt(rs.cx+moveX) + ",";
      shapetext += parseInt(rs.cy+moveY) + ",";
      shapetext += parseInt(rs.rx) + ",";
      shapetext += parseInt(rs.ry);
      type = "ellipseitem";
    } else if (shape instanceof SequenceShape) {
      SequenceShape ss = (SequenceShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += parseInt(ss.lifeLineHeight) + " ";
      shapetext += rect2ShapeText(ss.rectShape, moveX, moveY);
      type = "sequenceitem";
    } else if (shape instanceof RectShape) {
      RectShape rs = (RectShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(rs, moveX, moveY);
      type = "classitem";
    } else if (shape instanceof ComponentShape) {
    	ComponentShape rs = (ComponentShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(rs.rectShape, moveX, moveY);
      type = "comp";
    } else if (shape instanceof ServerShape) {
    	ServerShape rs = (ServerShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(rs.rectShape, moveX, moveY);
      type = "server";
    } else if (shape instanceof NoteShape) {
      NoteShape note = (NoteShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(note.rectShape, moveX, moveY);
      type = "noteitem";
    } else if (shape instanceof TextShape) {
      TextShape text = (TextShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(text.rectShape, moveX, moveY);
      type = "textitem";
    } else if (shape instanceof ActivityChoiceShape) {
    	ActivityChoiceShape choice = (ActivityChoiceShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(choice.rectShape, moveX, moveY);
      type = "choice";
    } else if (shape instanceof ActivityStartShape) {
    	ActivityStartShape start = (ActivityStartShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += parseInt(start.centerX+moveX) + ",";
      shapetext += parseInt(start.centerY+moveY) + ",";
      shapetext += parseInt(start.radius) + ",";
      type = "activitystart";
    } else if (shape instanceof ActivityEndShape) {
    	ActivityEndShape end = (ActivityEndShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += parseInt(end.centerX+moveX) + ",";
      shapetext += parseInt(end.centerY+moveY) + ",";
      shapetext += parseInt(end.radius) + ",";
      type = "activityend";
    } else if (shape instanceof ActivityShape) {
    	ActivityShape activity = (ActivityShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(activity.rectShape, moveX, moveY);
      type = "activity";
    } else if (shape instanceof MindCentralShape) {
    	MindCentralShape activity = (MindCentralShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(activity.rectShape, moveX, moveY);
      type = "centtop";
    } else if (shape instanceof DbShape) {
    	DbShape db = (DbShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(db.rectShape, moveX, moveY);
      type = "storage";
    } else if (shape instanceof ActorShape) {
      ActorShape actor = (ActorShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(actor.rectShape, moveX, moveY);
      type = "actoritem";
    }  else if (shape instanceof RelationshipShape2) {
      RelationshipShape2 rs = (RelationshipShape2) shape;
      result = getItem(diagram, forceCreate);
      
      for (int i = 0; i < rs.points.size(); ++i) {
        int p = i % 2 == 0 ? rs.points.get(i)+moveX : rs.points.get(i)+moveY ;
        p = parseInt(p);
        shapetext += p+",";
      }
      // skip last ,
      shapetext = shapetext.substring(0, shapetext.length()-1);
      type = "relationship";
    } else if (shape instanceof FreehandShape) {
    	FreehandShape rs = (FreehandShape) shape;
      result = getItem(diagram, forceCreate);
      
      for (int i = 0; i < rs.points.length; ++i) {
        int p = i % 2 == 0 ? rs.points[i]+moveX : rs.points[i]+moveY ;
        p = parseInt(p);
        shapetext += p+",";
      }
      // skip last ,
      shapetext = shapetext.substring(0, shapetext.length()-1);
      type = "freehand";
    } else if (shape instanceof UMLPackageShape) {
    	UMLPackageShape rs = (UMLPackageShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(rs.rectShape, moveX, moveY);
      type = "package";
    } else if (shape instanceof RectContainerShape) {
    	RectContainerShape rs = (RectContainerShape) shape;
      result = getItem(diagram, forceCreate);
      shapetext += rect2ShapeText(rs.rectShape, moveX, moveY);
      type = "rectcont";
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
  private native static int parseInt(String p)/*-{
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
  
  private static IDiagramItem getItem(Diagram diagram, boolean forceCreate) {
    IDiagramItem result = diagram.getDiagramItem();
    if (result == null || forceCreate) {
      // new item
      result = new DiagramItemDTO();
    } else {
    	result = result.copy();
    }
    return result;
  }

  public static IDiagramItemRO createCopy(IDiagramItemRO item) {
    IDiagramItem result = new DiagramItemDTO();
//    if (item.getType().equals("ellipseitem")) {
//      IEllipseItem i = (IEllipseItem) item;
//      IEllipseItem newei = new EllipseItemDTO();
//      newei.setTop(i.getTop());
//      newei.setLeft(i.getLeft());
//      newei.setWidth(i.getWidth());
//      newei.setHeight(i.getHeight());
//      result = newei;
//    } else if (item.getType().equals("sequenceitem")) {
//      ISequenceItem i = (ISequenceItem) item;
//      ISequenceItem newitem = new SequenceItemDTO();
//      newitem.setTop(i.getTop());
//      newitem.setLeft(i.getLeft());
//      newitem.setWidth(i.getWidth());
//      newitem.setHeight(i.getHeight());
//      newitem.setLifeLineHeight(i.getLifeLineHeight());
//      result = newitem;
//    } else if (item.getType().equals("classitem")) {
//      IClassItem i = (IClassItem) item;
//      IClassItem newci = new ClassItemDTO();
//      newci.setTop(i.getTop());
//      newci.setLeft(i.getLeft());
//      newci.setWidth(i.getWidth());
//      newci.setHeight(i.getHeight());
//      result = newci;
//    } else if (item.getType().equals("relationship")) {
//      IRelationshipItem i = (IRelationshipItem) item;
//      IRelationshipItem newri = new RelationshipItemDTO();
//      newri.setStartx(i.getStartx());
//      newri.setStarty(i.getStarty());
//      newri.setEndx(i.getEndx());
//      newri.setEndy(i.getEndy());
//      newri.setCapabilities(i.getCapabilities());
//      result = newri;
//    }
    result.setShape(item.getShape());
    result.setText(item.getText());
    result.setType(item.getType());
    return result;
  }

}
