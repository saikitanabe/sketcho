package net.sevenscales.editor.content.utils;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ISvgDataRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ActivityChoiceShape;
import net.sevenscales.editor.diagram.shape.ActivityEndShape;
import net.sevenscales.editor.diagram.shape.ActivityShape;
import net.sevenscales.editor.diagram.shape.ActivityStartShape;
import net.sevenscales.editor.diagram.shape.ChildTextShape;
import net.sevenscales.editor.diagram.shape.CommentThreadShape;
import net.sevenscales.editor.diagram.shape.ComponentShape;
import net.sevenscales.editor.diagram.shape.DbShape;
import net.sevenscales.editor.diagram.shape.EllipseShape;
import net.sevenscales.editor.diagram.shape.ForkShape;
import net.sevenscales.editor.diagram.shape.FreehandShape;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.shape.HorizontalPartitionShape;
import net.sevenscales.editor.diagram.shape.ImageShape;
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
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.uicomponents.uml.ActivityChoiceElement;
import net.sevenscales.editor.uicomponents.uml.ActivityElement;
import net.sevenscales.editor.uicomponents.uml.ActivityEnd;
import net.sevenscales.editor.uicomponents.uml.ActivityStart;
import net.sevenscales.editor.uicomponents.uml.ChildTextElement;
import net.sevenscales.editor.uicomponents.uml.ClassElement2;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.ComponentElement;
import net.sevenscales.editor.uicomponents.uml.EllipseElement;
import net.sevenscales.editor.uicomponents.uml.ForkElement;
import net.sevenscales.editor.uicomponents.uml.FreehandElement;
import net.sevenscales.editor.uicomponents.uml.GenericElement;
import net.sevenscales.editor.uicomponents.uml.GenericFreehandElement;
import net.sevenscales.editor.uicomponents.uml.GenericNoteElement;
// import net.sevenscales.editor.uicomponents.uml.HorizontalPartitionElement;
import net.sevenscales.editor.uicomponents.uml.HorizontalPartitionElement4;
import net.sevenscales.editor.uicomponents.uml.HorizontalPartitionElementCorporate;
import net.sevenscales.editor.uicomponents.uml.ImageElement;
import net.sevenscales.editor.uicomponents.uml.MindCentralElement;
import net.sevenscales.editor.uicomponents.uml.NoteElement;
import net.sevenscales.editor.uicomponents.uml.PackageElement;
import net.sevenscales.editor.uicomponents.uml.PackageElementCorporate;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.SequenceElement;
import net.sevenscales.editor.uicomponents.uml.SequenceElement2;
import net.sevenscales.editor.uicomponents.uml.ServerElement;
import net.sevenscales.editor.uicomponents.uml.StorageElement;
import net.sevenscales.editor.uicomponents.uml.TextElement;
import net.sevenscales.editor.uicomponents.uml.VerticalPartitionElement;
import net.sevenscales.editor.uicomponents.uml.VerticalPartitionElementCorporate;


public interface AbstractDiagramFactory {
	Info parseShape(IDiagramItemRO item, int moveX, int moveY);
	Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent);


	public class EllipseFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      return new EllipseShape(item.getShape().split(",")).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      return new EllipseElement(surface,
              (EllipseShape) shape,
              item.getText(),
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
              editable,
              item);
		}
	}

	public class SequenceFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s1 = item.getShape().split(" ");
      int lifeline = Integer.valueOf(s1[0]);
      String[] s2 = s1[1].split(",");
      int x = Integer.valueOf(s2[0]);
      int y = Integer.valueOf(s2[1]);
      int width = Integer.valueOf(s2[2]);
      int height = Integer.valueOf(s2[3]);

			return new SequenceShape(x, y, width, height, lifeline).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      if (Tools.isSketchMode()) {
        return new SequenceElement2(surface,
                (SequenceShape) shape,
                item.getText(),
                DiagramItemFactory.parseBackgroundColor(item),
                DiagramItemFactory.parseBorderColor(item),
                DiagramItemFactory.parseTextColor(item),
                editable,
                item);
      } else {
        return new SequenceElement(surface,
                (SequenceShape) shape,
                item.getText(),
                DiagramItemFactory.parseBackgroundColor(item),
                DiagramItemFactory.parseBorderColor(item),
                DiagramItemFactory.parseTextColor(item),
                editable,
                item);
      }
		}
	}


	public class ComponentFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
			String[] s = item.getShape().split(",");
			return new ComponentShape(s).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      return new ComponentElement(surface,
          (ComponentShape) shape, 
          item.getText(), 
          DiagramItemFactory.parseBackgroundColor(item),
          DiagramItemFactory.parseBorderColor(item),
          DiagramItemFactory.parseTextColor(item), 
          editable,
          item);
		}
	}


	public class ServerFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
			String[] s = item.getShape().split(",");
			return new ServerShape(s).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      return new ServerElement(surface,
          (ServerShape) shape,
          item.getText(), 
          DiagramItemFactory.parseBackgroundColor(item),
          DiagramItemFactory.parseBorderColor(item),
          DiagramItemFactory.parseTextColor(item),
          editable,
          item);
		}
	}


	public class ClassFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
			String[] s = item.getShape().split(",");
			return new RectShape(s).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      return new ClassElement2(surface,
          (RectShape) shape, 
          item.getText(), 
          DiagramItemFactory.parseBackgroundColor(item),
          DiagramItemFactory.parseBorderColor(item),
          DiagramItemFactory.parseTextColor(item),
          editable,
          item);
		}
	}


	public class NoteFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      int x = Integer.valueOf(s[0]);
      int y = Integer.valueOf(s[1]);
      int width = Integer.valueOf(s[2]);
      int height = Integer.valueOf(s[3]);
      return new NoteShape(x, 
              y,
              width,
              height).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      return new NoteElement(surface,
          		(NoteShape) shape,
              item.getText(),
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
          editable,
          item);
		}
	}


	public class CommentThreadFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      int x = Integer.valueOf(s[0]);
      int y = Integer.valueOf(s[1]);
      int width = Integer.valueOf(s[2]);
      int height = Integer.valueOf(s[3]);
			return new CommentThreadShape(x, 
              y,
              width,
              height).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      return new CommentThreadElement(surface,
          (CommentThreadShape) shape,
              item.getText(),
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
          editable,
          item);
		}
	}


	public class ActivityChoiceFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      int x = Integer.valueOf(s[0]);
      int y = Integer.valueOf(s[1]);
      int width = Integer.valueOf(s[2]);
      int height = Integer.valueOf(s[3]);
      return new ActivityChoiceShape(x, 
              y,
              width,
              height).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
			return new ActivityChoiceElement(surface,
          		(ActivityChoiceShape) shape,
              item.getText(), 
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
          editable,
          item);
		}
	}


	public class ActivityStartFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      int cx = Integer.valueOf(s[0]);
      int cy = Integer.valueOf(s[1]);
      int r = Integer.valueOf(s[2]);
      return new ActivityStartShape(cx, cy, r).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      if (item.getVersion() >= 3) {
        return new ActivityStart(surface,
            (ActivityStartShape) shape,
            DiagramItemFactory.parseBackgroundColor(item),
            DiagramItemFactory.parseBorderColor(item),
            DiagramItemFactory.parseTextColor(item),
            editable,
            item);
      } else {
        // legacy activity start colors => update to new color scheme
        ElementColorScheme paperScheme = Theme.getColorScheme(ThemeName.PAPER);
        return new ActivityStart(surface,
            (ActivityStartShape) shape,
            paperScheme.getBorderColor().create(),
            paperScheme.getBorderColor().create(),
            paperScheme.getTextColor().create(),
            editable,
            item);
      }

		}
	}

	public class ActivityEndFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      int cx = Integer.valueOf(s[0]);
      int cy = Integer.valueOf(s[1]);
      int r = Integer.valueOf(s[2]);
			return new ActivityEndShape(cx, cy, r).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
			return new ActivityEnd(surface,
          		(ActivityEndShape) shape,
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
          editable,
          item);			
		}
	}

	public class ActivityFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      int x = Integer.valueOf(s[0]);
      int y = Integer.valueOf(s[1]);
      int width = Integer.valueOf(s[2]);
      int height = Integer.valueOf(s[3]);
      return new ActivityShape(x, y, width, height).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
			return new ActivityElement(surface,
          		(ActivityShape)shape,
              item.getText(),
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
          editable,
          item);
    }
	}

	public class MindCentralFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      int x = Integer.valueOf(s[0]);
      int y = Integer.valueOf(s[1]);
      int width = Integer.valueOf(s[2]);
      int height = Integer.valueOf(s[3]);
      return new MindCentralShape(x, y, width, height).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
			return new MindCentralElement(surface,
          		(MindCentralShape) shape,
              item.getText(),
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
          editable,
          item);			
		}
	}

	public class StorageFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      int x = Integer.valueOf(s[0]);
      int y = Integer.valueOf(s[1]);
      int width = Integer.valueOf(s[2]);
      int height = Integer.valueOf(s[3]);
      return new DbShape(x, y, width, height).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
			return new StorageElement(surface,
          		(DbShape)shape,
              item.getText(),
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
          editable,
          item);			
		}
	}

	public class TextItemFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      int x = Integer.valueOf(s[0]);
      int y = Integer.valueOf(s[1]);
      int width = Integer.valueOf(s[2]);
      int height = Integer.valueOf(s[3]);
      return new TextShape(x, y, width, height).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      return new TextElement(surface,
	          		(TextShape)shape,
	              DiagramItemFactory.parseBackgroundColor(item),
	              DiagramItemFactory.parseBorderColor(item),
	              DiagramItemFactory.parseTextColor(item),
	              item.getText(),
          			editable,
          			item);
		}
	}

  public class ChildTextItemFactory implements AbstractDiagramFactory {
    public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      int x = Integer.valueOf(s[0]);
      int y = Integer.valueOf(s[1]);
      int width = Integer.valueOf(s[2]);
      int height = Integer.valueOf(s[3]);
      return new ChildTextShape(x, y, width, height).move(moveX, moveY);
    }

    public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      return new ChildTextElement(surface,
              (ChildTextShape)shape,
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
              item.getText(),
              editable,
              item,
              parent);
    }
  }

	// public class ActorFactory implements AbstractDiagramFactory {
	// 	public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
  //     String[] s = item.getShape().split(",");
  //     int x = Integer.valueOf(s[0]);
  //     int y = Integer.valueOf(s[1]);
  //     int width = Integer.valueOf(s[2]);
  //     int height = Integer.valueOf(s[3]);
  //     return new ActorShape(x, y, width, height).move(moveX, moveY);
	// 	}

	// 	public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
	// 		return new Actor(surface,
  //         		(ActorShape)shape,
  //             item.getText(),
  //             DiagramItemFactory.parseBackgroundColor(item),
  //             DiagramItemFactory.parseBorderColor(item),
  //             DiagramItemFactory.parseTextColor(item),
  //         editable,
  //         item);			
	// 	}
	// }

	public class RelationshipFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      List<Integer> points = new ArrayList<Integer>();
      String[] ps = item.getShape().split(",");
      for (String p : ps) {
        points.add(Integer.valueOf(p));
      }
      return new RelationshipShape2(points).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
			return new Relationship2(surface, 
                               (RelationshipShape2)shape, 
                               item.getText(),
                               DiagramItemFactory.parseBackgroundColor(item), 
                               DiagramItemFactory.parseBorderColor(item), 
                               DiagramItemFactory.parseTextColor(item),
                               editable,
                               item);
		}
	}

	public class FreehandFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] ps = item.getShape().split(",");
      int[] points = new int[ps.length];
      int i = 0;
      for (String p : ps) {
        points[i++] = (Integer.valueOf(p));
      }
      return new FreehandShape(points).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      return new FreehandElement(
      		surface, 
      		(FreehandShape) shape,
      		DiagramItemFactory.parseBackgroundColor(item), 
      		DiagramItemFactory.parseBorderColor(item),
      		DiagramItemFactory.parseTextColor(item),
      		editable,
          item);
		}
	}


  public class GenericFactory implements AbstractDiagramFactory {
    public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      ISvgDataRO svgdata = null;
      if (item.getExtension() != null) {
        svgdata = item.getExtension().getSvgData();
      }
      return new GenericShape(item.getType(), s, item.getShapeProperties(), svgdata).move(moveX, moveY);
    }

    public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      GenericShape gh = (GenericShape) shape;
      AbstractDiagramFactoryUtils.fixUninitializedDiagramItem(item, gh);

      if (item.getType().equals(ElementType.NOTE.getValue())) {
        // need to always have vertical text element, conversion from 
        // official note => sketch note doesn't contain this property!
        // gh.addShapeProperty(ShapeProperty.TEXT_RESIZE_DIR_VERTICAL);
        return new GenericNoteElement(surface,
          gh,
          item.getText(), 
          DiagramItemFactory.parseBackgroundColor(item),
          DiagramItemFactory.parseBorderColor(item),
          DiagramItemFactory.parseTextColor(item),
          editable,
          item);
      } else if (item.getType().equals(ElementType.FREEHAND2.getValue())) {
        return new GenericFreehandElement(surface,
          gh,
          item.getText(), 
          DiagramItemFactory.parseBackgroundColor(item),
          DiagramItemFactory.parseBorderColor(item),
          DiagramItemFactory.parseTextColor(item),
          editable,
          item);
      // } else if (item.getType().equals(ElementType.SLIDE.getValue())) {
      //   return new GenericSlideElement(surface,
      //     gh,
      //     item.getText(), 
      //     DiagramItemFactory.parseBackgroundColor(item),
      //     DiagramItemFactory.parseBorderColor(item),
      //     DiagramItemFactory.parseTextColor(item),
      //     editable,
      //     item);
      } else {
        // if (item.getShapeProperties() == null) {
        // }
        // could load default shape properties, since this is probably a switch
        // from awesome => corporate
        // could restore all except DISABLE_SHAPE_AUTO_RESIZE
        return new GenericElement(surface,
            gh,
            item.getText(), 
            DiagramItemFactory.parseBackgroundColor(item),
            DiagramItemFactory.parseBorderColor(item),
            DiagramItemFactory.parseTextColor(item),
            editable,
            item);
      }
    }
  }

  public class ImageFactory implements AbstractDiagramFactory {
    public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      String[] s = item.getShape().split(",");
      int x = Integer.valueOf(s[0]);
      int y = Integer.valueOf(s[1]);
      int width = Integer.valueOf(s[2]);
      int height = Integer.valueOf(s[3]);

      // vai pitäiskö pistää custom dataan molemmat!!!
      // url,filename
      // vois olla oikea paikka tälle ja shape ois vaan left, top, width, height
      String url = "";
      String filename = "";
      if (item.getCustomData() != null) {
        String[] cd = item.getCustomData().split(",");
        if (cd.length >= 1) {
          url = cd[0];
        }
        if (cd.length >= 2) {
          // filename is optional
          filename = cd[1];
        }
      }
      return new ImageShape(x, y, width, height, url, filename).move(moveX, moveY);
    }

    public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      return new ImageElement(surface,
              (ImageShape)shape,
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
              editable,
              item);      
    }
  }

	public class PackageFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      return new UMLPackageShape(item.getShape().split(",")).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      Integer props = null;
      LibraryShapes.ShapeProps sh = LibraryShapes.getShapeProps(ElementType.PACKAGE.getValue());
      if (sh != null) {
        props = sh.properties;
      }
      GenericShape gs = ((UMLPackageShape) shape).toGenericShape(props);

      if (Tools.isSketchMode()) {
        return new PackageElement(surface,
                gs,
                item.getText(),
                DiagramItemFactory.parseBackgroundColor(item),
                DiagramItemFactory.parseBorderColor(item),
                DiagramItemFactory.parseTextColor(item),
            editable,
            item);
      } else {
        // return new UMLPackageElement(surface,
        //     		(UMLPackageShape)shape,
        //         item.getText(),
        //         DiagramItemFactory.parseBackgroundColor(item),
        //         DiagramItemFactory.parseBorderColor(item),
        //         DiagramItemFactory.parseTextColor(item),
        //     editable,
        //     item);
        return new PackageElementCorporate(
          surface,
              gs,
              item.getText(),
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
          editable,
          item
        );
      }
		}
	}

	public class VerticalPartitionFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      return new RectContainerShape(item.getShape().split(",")).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      Integer props = null;
      LibraryShapes.ShapeProps sh = LibraryShapes.getShapeProps(ElementType.VERTICAL_PARTITION.getValue());
      if (sh != null) {
        props = sh.properties;
      }
      GenericShape gs = ((RectContainerShape) shape).toGenericShape(props);

      if (Tools.isSketchMode()) {
        return new VerticalPartitionElement(surface,
                gs,
                item.getText(),
                DiagramItemFactory.parseBackgroundColor(item),
                DiagramItemFactory.parseBorderColor(item),
                DiagramItemFactory.parseTextColor(item),
            editable,
            item);
      } else {
  			// return new RectBoundaryElement(surface,
        //     		(RectContainerShape) shape,
        //         item.getText(),
        //         DiagramItemFactory.parseBackgroundColor(item),
        //         DiagramItemFactory.parseBorderColor(item),
        //         DiagramItemFactory.parseTextColor(item),
        //     editable,
        //     item);
        return new VerticalPartitionElementCorporate(surface,
          gs,
          item.getText(),
          DiagramItemFactory.parseBackgroundColor(item),
          DiagramItemFactory.parseBorderColor(item),
          DiagramItemFactory.parseTextColor(item),
          editable,
          item
        );
      }
		}
	}


	public class HorizontalPartitionFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
			return new HorizontalPartitionShape(item.getShape().split(",")).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
      if (Tools.isSketchMode()) {
        return new HorizontalPartitionElement4(surface,
              (HorizontalPartitionShape)shape,
              item.getText(),
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
          editable,
          item);      
      } else {
				// return new HorizontalPartitionElement(surface,
        //   		(HorizontalPartitionShape)shape,
        //       item.getText(),
        //       DiagramItemFactory.parseBackgroundColor(item),
        //       DiagramItemFactory.parseBorderColor(item),
        //       DiagramItemFactory.parseTextColor(item),
        //   editable,
        //   item);
        return new HorizontalPartitionElementCorporate(surface,
          (HorizontalPartitionShape)shape,
          item.getText(),
          DiagramItemFactory.parseBackgroundColor(item),
          DiagramItemFactory.parseBorderColor(item),
          DiagramItemFactory.parseTextColor(item),
          editable,
          item
        );
      }
		}
	}


	public class ForkFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
			return new ForkShape(item.getShape().split(",")).move(moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, boolean editable, IDiagramItemRO item, IParentElement parent) {
			return new ForkElement(surface,
          		(ForkShape) shape,
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
          editable,
          item);
		}
	}

}
