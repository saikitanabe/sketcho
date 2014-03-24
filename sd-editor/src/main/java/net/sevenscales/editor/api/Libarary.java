package net.sevenscales.editor.api;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.editor.api.dojo.FactoryDoJo;

import net.sevenscales.editor.api.LibrarySelections.Library;
import net.sevenscales.editor.api.LibrarySelections.LibrarySelectedHandler;
import net.sevenscales.editor.api.event.LibrarySelectionEvent;
import net.sevenscales.editor.api.event.ThemeChangedEvent;
import net.sevenscales.editor.api.event.ThemeChangedEventHandler;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;

import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.diagram.ClickDiagramHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.ProxyDragHandler;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.diagram.shape.ActivityChoiceShape;
import net.sevenscales.editor.diagram.shape.ActivityEndShape;
import net.sevenscales.editor.diagram.shape.ActivityShape;
import net.sevenscales.editor.diagram.shape.ForkShape;
import net.sevenscales.editor.diagram.shape.ActivityStartShape;
import net.sevenscales.editor.diagram.shape.ActorShape;
import net.sevenscales.editor.diagram.shape.ComponentShape;
import net.sevenscales.editor.diagram.shape.DbShape;
import net.sevenscales.editor.diagram.shape.EllipseShape;
import net.sevenscales.editor.diagram.shape.MindCentralShape;
import net.sevenscales.editor.diagram.shape.NoteShape;
import net.sevenscales.editor.diagram.shape.RectContainerShape;
import net.sevenscales.editor.diagram.shape.HorizontalPartitionShape;
import net.sevenscales.editor.diagram.shape.RectShape;
import net.sevenscales.editor.diagram.shape.SequenceShape;
import net.sevenscales.editor.diagram.shape.ServerShape;
import net.sevenscales.editor.diagram.shape.TextShape;
import net.sevenscales.editor.diagram.shape.UMLPackageShape;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.uicomponents.uml.ActivityChoiceElement;
import net.sevenscales.editor.uicomponents.uml.ActivityElement;
import net.sevenscales.editor.uicomponents.uml.ForkElement;
import net.sevenscales.editor.uicomponents.uml.ActivityEnd;
import net.sevenscales.editor.uicomponents.uml.ActivityStart;
import net.sevenscales.editor.uicomponents.uml.Actor;
import net.sevenscales.editor.uicomponents.uml.ClassElement2;
import net.sevenscales.editor.uicomponents.uml.ComponentElement;
import net.sevenscales.editor.uicomponents.uml.EllipseElement;
import net.sevenscales.editor.uicomponents.uml.MindCentralElement;
import net.sevenscales.editor.uicomponents.uml.NoteElement;
import net.sevenscales.editor.uicomponents.uml.RectBoundaryElement;
import net.sevenscales.editor.uicomponents.uml.HorizontalPartitionElement;
import net.sevenscales.editor.uicomponents.uml.SequenceElement;
import net.sevenscales.editor.uicomponents.uml.ServerElement;
import net.sevenscales.editor.uicomponents.uml.StorageElement;
import net.sevenscales.editor.uicomponents.uml.TextElement;
import net.sevenscales.editor.uicomponents.uml.UMLPackageElement;
import net.sevenscales.editor.uicomponents.uml.GenericElement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class Libarary extends SimplePanel implements SurfaceLoadedEventListener, ClickDiagramHandler, IToolSelection {
	private ISurfaceHandler surface;
	private ISurfaceHandler toolpool;
	private List<Diagram> items;
  private ProxyDragHandler proxyDragHandler;
	private EditorContext editorContext;

	private static final int GROUP_SPACE = 25;
	private static final int GROUP_HEADING_SPACE = 45;

	private static final int SOFTWARE_SKETCHING_GROUP = 25;
	private static final int SOFTWARE_SKETCHING_GROUP_HEIGHT = 410;
//	private static final int SOFTWARE_SKETCHING_GROUP_START = 70;

  private static final int ACTIVITY_GROUP = SOFTWARE_SKETCHING_GROUP + SOFTWARE_SKETCHING_GROUP_HEIGHT + GROUP_SPACE;
  private static final int ACTIVITY_GROUP_HEIGHT = 305;

  private static final int CLASS_GROUP = ACTIVITY_GROUP + ACTIVITY_GROUP_HEIGHT + GROUP_SPACE;
  private static final int CLASS_GROUP_HEIGHT = 210;

	private static final int MINDMAP_GROUP = CLASS_GROUP + CLASS_GROUP_HEIGHT + GROUP_SPACE;
	private static final int MINDMAP_GROUP_HEIGHT = 260;

  private static final int ROADMAP_GROUP = MINDMAP_GROUP + MINDMAP_GROUP_HEIGHT + GROUP_SPACE;
  private static final int ROADMAP_GROUP_HEIGHT = 260;

  private static final int GENERAL_GROUP = ROADMAP_GROUP + ROADMAP_GROUP_HEIGHT + GROUP_SPACE;
  private static final int GENERAL_GROUP_HEIGHT = 260;

	
	private LibrarySelectedHandler librarySelectedHandler = new LibrarySelectedHandler() {
		@Override
		public void onSelected(Library library) {
			toolpool.getRootLayer().resetTransform();
      getWidget().getElement().setScrollTop(0);
			
			switch (library) {
			case SOFTWARE:
				break;
			case MINDMAP:
				toolpool.getRootLayer().applyTransform(0, -MINDMAP_GROUP + 25);
				break;
			case ROADMAP:
        toolpool.getRootLayer().applyTransform(0, -ROADMAP_GROUP + 25);
				break;
      case GENERAL:
        toolpool.getRootLayer().applyTransform(0, -GENERAL_GROUP + 25);
        break;
			}

			editorContext.getEventBus().fireEvent(new LibrarySelectionEvent(library));
			editorContext.set(EditorProperty.CURRENT_LIBRARY, library);
		}
	};

	public Libarary(ISurfaceHandler asurface, int height, IModeManager modeManager, EditorContext editorContext) {
		this.surface = asurface;
		this.editorContext = editorContext;
		this.toolpool = FactoryDoJo.createSurfaceHandler();
		toolpool.setName(ISurfaceHandler.LIBRARY_AREA);
		toolpool.setDisableOnArea(true);
		toolpool.init(200, 1700, true, modeManager, false, editorContext);
		
		setStyle();
		editorContext.getEventBus().addHandler(ThemeChangedEvent.TYPE, new ThemeChangedEventHandler() {
      @Override
      public void on(ThemeChangedEvent event) {
        setStyle();
      }
    });
		
		// This is the default library
		editorContext.set(EditorProperty.CURRENT_LIBRARY, Library.SOFTWARE);
		
		toolpool.setDragEnabled(false);
//		toolpool.setVerticalDragOnly(true);
		toolpool.setProxyOnDrag(true);
    toolpool.addLoadEventListener(this);

//		VerticalPanel panel = new VerticalPanel();
//    ScrollPanel panel = new ScrollPanel(toolpool);
//    panel.setAlwaysShowScrollBars(false);
//    panel.getElement().getStyle().setOverflowY(Overflow.HIDDEN);
//    panel.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
//		panel.setSpacing(0);
    
    final FlowPanel panel = new FlowPanel();
    panel.setHeight(Window.getClientHeight() + "px");
    panel.setStyleName("library");
		panel.add(toolpool.getWidget());
		panel.add(new LibrarySelections(librarySelectedHandler, editorContext));
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
		    panel.setHeight(Window.getClientHeight() + "px");
			}
		});
		
//		surface.add(new Circle(surface, 30, 30, 10), true);		
		
		setWidget(panel);
    proxyDragHandler = new ProxyDragHandler(toolpool, surface);
	}

	private void setStyle() {
    toolpool.setStyleName("toolbar " + Theme.themeCssClass());
  }

  public void addSelectionHandler(DiagramSelectionHandler handler) {
    toolpool.addSelectionListener(handler);
	}

	public void onLoaded() {
	  if (items == null) {
	  	editorContext.set(EditorProperty.ON_SURFACE_LOAD, true);
      List<Diagram> items = createToolbarItems();
    	editorContext.set(EditorProperty.ON_SURFACE_LOAD, false);
      for (Diagram item : items) {
        item.registerClickHandler(this);
        toolpool.add(item, true); 
      }
      
      float factor = 0.8f;
      if (TouchHelpers.isSupportsTouch()) {
      	factor = 0.7f;
      }
      toolpool.scale(factor);
	  }
	}

  private void flows(List<Diagram> result) {
    final Color flowGroupColor = new Color(0x99, 0x99, 0xff, 0);
    result.add(new RectBoundaryElement(this.toolpool,
        new RectContainerShape(10, ACTIVITY_GROUP, 220, ACTIVITY_GROUP_HEIGHT),
        "Flows",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));
    
    result.add(new ActivityChoiceElement(this.toolpool,
        new ActivityChoiceShape(185, ACTIVITY_GROUP + GROUP_HEADING_SPACE + 10, 32, 32),
        "",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));

    result.add(new ActivityStart(this.toolpool,
        new ActivityStartShape(150, ACTIVITY_GROUP + GROUP_HEADING_SPACE, ActivityStart.ACTIVITY_START_RADIUS), true,
        new DiagramItemDTO()));

    result.add(new ActivityEnd(this.toolpool,
        new ActivityEndShape(150, ACTIVITY_GROUP + GROUP_HEADING_SPACE + 50, ActivityEnd.ACTIVITY_END_RADIUS), true,
        new DiagramItemDTO()));

    result.add(new ActivityElement(this.toolpool,
        new ActivityShape(30, ACTIVITY_GROUP + GROUP_HEADING_SPACE + 10, 100, 30),
        "My Activity",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));
    
    result.add(new SequenceElement(this.toolpool, 
        new SequenceShape(50, ACTIVITY_GROUP + 130, 100, 25, 25),
        "Sequence",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));
    
    result.add(new ActivityElement(this.toolpool,
        new ActivityShape(80, ACTIVITY_GROUP + 220, 100, 30),
        "Collaboration",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));

    result.add(new ForkElement(this.toolpool,
      new ForkShape(150, ACTIVITY_GROUP + 130, 50, 5),
      Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
      new DiagramItemDTO()));
    result.add(new ForkElement(this.toolpool,
      new ForkShape(210, ACTIVITY_GROUP + 130, 5, 50, 1),
      Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
      new DiagramItemDTO()));

    Diagram swimline = new HorizontalPartitionElement(this.toolpool, new HorizontalPartitionShape(22, ACTIVITY_GROUP + 200, 190, 90), "Swimline", Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true, new DiagramItemDTO());
    swimline.setDuplicateMultiplySize(3, 3);
    result.add(swimline);
  }

  private void roadmap(List<Diagram> result) {
    int roadmapIndent = 35;
    Diagram q1 = new RectBoundaryElement(this.toolpool,
        new RectContainerShape(roadmapIndent, ROADMAP_GROUP, 195, ROADMAP_GROUP_HEIGHT),
        "Q1",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), 
        Theme.createDefaultTextColor(), true,
        new DiagramItemDTO());
    q1.setDuplicateMultiplySize(2, 2);
    result.add(q1);

    int marketingY = ROADMAP_GROUP + 25;
    int marketingHeight = ROADMAP_GROUP_HEIGHT / 2 - 10;
    Diagram marketing = new HorizontalPartitionElement(this.toolpool, new HorizontalPartitionShape(10, marketingY, 220, marketingHeight), "Marketing", Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true, 
      new DiagramItemDTO());
    marketing.setDuplicateMultiplySize(3, 3);
    result.add(marketing);

    int activityIndent = roadmapIndent + 10;
    result.add(new ActivityChoiceElement(this.toolpool,
        new ActivityChoiceShape(activityIndent, marketingY + 45, 32, 32),
        "",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));

    result.add(new MindCentralElement(this.toolpool,
        new MindCentralShape(roadmapIndent + 50, marketingY + 10, 100, 30),
        "Keynote",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));

    int productY = marketingY + marketingHeight;
    int productHeight = ROADMAP_GROUP_HEIGHT / 2 - 15;
    Diagram productLine = new HorizontalPartitionElement(this.toolpool, new HorizontalPartitionShape(10, productY, 220, productHeight), "Product Line", Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
      new DiagramItemDTO());
    productLine.setDuplicateMultiplySize(3, 3);
    result.add(productLine);

    result.add(new ActivityElement(this.toolpool,
        new ActivityShape(roadmapIndent + 10, productY + 30, 100, 30),
        "Release X",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));
  }

  private static class LibraryShape {
    ElementType elementType;
    int shapeProperties;
    int width;
    int height;
    int duplicateFactoryX;
    int duplicateFactoryY;

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

  private void general(List<Diagram> result) {
    LibraryShape[][] shapes = new LibraryShape[][]{
      {
        new LibraryShape(ElementType.STAR4, 40, 40),
        new LibraryShape(ElementType.STAR5, 40, 40), 
        new LibraryShape(ElementType.ENVELOPE, 50, 35, ShapeProperty.TEXT_POSITION_BOTTOM.getValue()),
        new LibraryShape(ElementType.TRIANGLE, 40, 40, ShapeProperty.TEXT_POSITION_BOTTOM.getValue())
      },
      {
        new LibraryShape(ElementType.CLOUD, 40, 40, 0, 3, 3),
        new LibraryShape(ElementType.FIREWALL, 27, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 3, 3),
        new LibraryShape(ElementType.BUBBLE, 50, 35, ShapeProperty.TEXT_RESIZE_DIR_VERTICAL.getValue(), 3, 3),
        new LibraryShape(ElementType.BUBBLE_R, 50, 35, ShapeProperty.TEXT_RESIZE_DIR_VERTICAL.getValue(), 3, 3)
      },
      {
        new LibraryShape(ElementType.CIRCLE, 40, 40, 0, 3, 3),
        new LibraryShape(ElementType.SMILEY, 40, 40, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 3, 3),
        new LibraryShape(ElementType.POLYGON4, 40, 40, 0, 3, 3), 
        new LibraryShape(ElementType.POLYGON8, 40, 40, 0, 3, 3)
      },
      {
        new LibraryShape(ElementType.ARROW_UP, 20, 40, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 3, 3),
        new LibraryShape(ElementType.ARROW_DOWN, 20, 40, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 3, 3),
        new LibraryShape(ElementType.ARROW_RIGHT, 40, 20, ShapeProperty.SHAPE_AUTO_RESIZE_FALSE.getValue(), 3, 3),
        new LibraryShape(ElementType.ARROW_LEFT, 40, 20, ShapeProperty.SHAPE_AUTO_RESIZE_FALSE.getValue(), 3, 3)
      },
      {
        new LibraryShape(ElementType.IPHONE, 24, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 12, 12),
        new LibraryShape(ElementType.WEB_BROWSER, 50, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue(), 12, 12),
        new LibraryShape(ElementType.RECT, 50, 35, 0, 2, 2)
      }
    };

    final int COL_SIZE = 40;
    final int ROW_SIZE = 40;
    final int MARGIN = 10;
    int row = 0;
    for (LibraryShape[] rowShapes : shapes) {
      int col = 0;
      int colpos = MARGIN;
      for (LibraryShape colShape : rowShapes) {
        Diagram el = new GenericElement(this.toolpool,
            new GenericShape(colShape.elementType.getValue(), 
                             // MARGIN + col * COL_SIZE + col * MARGIN, 
                             colpos + col * MARGIN,
                             GENERAL_GROUP + row * ROW_SIZE + row * MARGIN, 
                             colShape.width, 
                             colShape.height, 
                             colShape.shapeProperties),
              "",
              Theme.createDefaultBackgroundColor(),
              Theme.createDefaultBorderColor(),
              Theme.createDefaultTextColor(),
              true,
              DiagramItemDTO.createGenericItem(colShape.elementType));
        el.setDuplicateMultiplySize(colShape.duplicateFactoryX, colShape.duplicateFactoryY);
        result.add(el);
        colpos += colShape.width;
        col++;
      }
      row++;
    }

    // Diagram iphone = new GenericElement(this.toolpool,
    //     new GenericShape(ElementType.IPHONE.getValue(), 10 + 0 * 40 + 0 * 10, GENERAL_GROUP + 4 * 40 + 4 * 10, 24, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue()),
    //       "",
    //       Theme.createDefaultBackgroundColor(),
    //       Theme.createDefaultBorderColor(),
    //       Theme.createDefaultTextColor(),
    //       true,
    //       DiagramItemDTO.createGenericItem(ElementType.IPHONE));
    // iphone.setDuplicateMultiplySize(3, 3);
    // result.add(iphone);

    // Diagram wbrowser = new GenericElement(this.toolpool,
    //     new GenericShape(ElementType.WEB_BROWSER.getValue(), 10 + 1 * 40 + 1 * 10, GENERAL_GROUP + 4 * 40 + 4 * 10, 50, 50, ShapeProperty.TEXT_POSITION_BOTTOM.getValue()),
    //       "",
    //       Theme.createDefaultBackgroundColor(),
    //       Theme.createDefaultBorderColor(),
    //       Theme.createDefaultTextColor(),
    //       true,
    //       DiagramItemDTO.createGenericItem(ElementType.WEB_BROWSER));
    // wbrowser.setDuplicateMultiplySize(12, 12);
    // result.add(wbrowser);

    // Diagram rect = new GenericElement(this.toolpool,
    //     new GenericShape(ElementType.RECT.getValue(), 10 + 2 * 40 + 2 * 10, GENERAL_GROUP + 4 * 40 + 4 * 10, 50, 35),
    //       "",
    //       Theme.createDefaultBackgroundColor(),
    //       Theme.createDefaultBorderColor(),
    //       Theme.createDefaultTextColor(),
    //       true,
    //       DiagramItemDTO.createGenericItem(ElementType.RECT));
    // rect.setDuplicateMultiplySize(2, 2);
    // result.add(rect);
  }

	private List<Diagram> createToolbarItems() {
		boolean currentValue = editorContext.isTrue(EditorProperty.AUTO_RESIZE_ENABLED);
		
		editorContext.set(EditorProperty.AUTO_RESIZE_ENABLED, true);
		
		List<Diagram> result = new ArrayList<Diagram>();
		
		// software sketching group
//    result.add(new TextElement(this.toolpool,
//        new TextShape(20, SOFTWARE_SKETCHING_GROUP, 100, 30),
//        new Color(0x44, 0x44, 0x44, 1), new Color(0x44, 0x44, 0x44, 1), "*Software Sketching Library*", true));
		
    
//    result.add(new ClassElement2(this.toolpool,
//        new RectShape(30, SOFTWARE_SKETCHING_GROUP_START, 100, 30),
//        "SimpleClass", ClassElement2.createDefaultBackgroundColor(), Color.createDefaultTextColor(), true));
		
    result.add(new RectBoundaryElement(this.toolpool,
        new RectContainerShape(10, SOFTWARE_SKETCHING_GROUP, 220, SOFTWARE_SKETCHING_GROUP_HEIGHT),
        "Software Sketching",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));
    
    result.add(new NoteElement(this.toolpool,
        new NoteShape(30, SOFTWARE_SKETCHING_GROUP + GROUP_HEADING_SPACE, 170, 30),
        "*Note* this!", Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));

    result.add(new Actor(this.toolpool,
        new ActorShape(30, SOFTWARE_SKETCHING_GROUP + 120, 25, 40),
        "<<system>>\nActor", Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));

    result.add(new EllipseElement(this.toolpool, 
        new EllipseShape(145, SOFTWARE_SKETCHING_GROUP + 150, 50, 25),
        "Use Case", Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));
    
    result.add(new StorageElement(this.toolpool,
        new DbShape(160, SOFTWARE_SKETCHING_GROUP + 220, 100, 30),
        "Db",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));

//    result.add(new SketchLine(this.toolpool,
//        new SketchLineShape(30, SOFTWARE_SKETCHING_GROUP_START + 160, 100, 30),
//        true));
    
		result.add(new ComponentElement(this.toolpool,
				new ComponentShape(70, SOFTWARE_SKETCHING_GROUP + 350, 100, 60),
				"Component",
				Theme.createDefaultBackgroundColor(), 
				Theme.createDefaultBorderColor(), 
				Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));

		// special border => no border
    result.add(new TextElement(this.toolpool,
        new TextShape(120, SOFTWARE_SKETCHING_GROUP + 300, 100, 30),
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), "Just text", true, new DiagramItemDTO()));
    
		result.add(new ServerElement(this.toolpool,
				new ServerShape(30, SOFTWARE_SKETCHING_GROUP + 215, 60, 80),
				"Server",
				Theme.createDefaultBackgroundColor(), 
				Theme.createDefaultBorderColor(),
				Theme.createDefaultTextColor(), true, 
        new DiagramItemDTO()));

    
    // ACTIVITY DIAGRAM
    
    flows(result);

//    List<Integer> points = new ArrayList<Integer>();
//
//    points = new ArrayList<Integer>();
//    points.add(30);
//    points.add(SOFTWARE_SKETCHING_GROUP_START + 160);
//    points.add(120);
//    points.add(SOFTWARE_SKETCHING_GROUP_START + 160);
//    result.add(new Relationship2(this.toolpool, points, "->", true));
//    
//    points = new ArrayList<Integer>();
//    points.add(130);
//    points.add(SOFTWARE_SKETCHING_GROUP_START + 160);
//    points.add(150);
//    points.add(SOFTWARE_SKETCHING_GROUP_START + 160);
//    points.add(150);
//    points.add(SOFTWARE_SKETCHING_GROUP_START + 215);
//    points.add(130);
//    points.add(SOFTWARE_SKETCHING_GROUP_START + 215);
//    result.add(new Relationship2(this.toolpool, points, "->", true));
//
//    points = new ArrayList<Integer>();
//    points.add(30);
//    points.add(SOFTWARE_SKETCHING_GROUP_START + 175);
//    points.add(120);
//    points.add(SOFTWARE_SKETCHING_GROUP_START + 175);
//    result.add(new Relationship2(this.toolpool, points, "-->", true));
//
//    points = new ArrayList<Integer>();
//    points.add(30);
//    points.add(SOFTWARE_SKETCHING_GROUP_START + 190);
//    points.add(120);
//    points.add(SOFTWARE_SKETCHING_GROUP_START + 190);
//    result.add(new Relationship2(this.toolpool, points, "-|>", true));
//
//    points = new ArrayList<Integer>();
//    points.add(30);
//    points.add(SOFTWARE_SKETCHING_GROUP_START + 215);
//  	points.add(120);
//  	points.add(SOFTWARE_SKETCHING_GROUP_START + 215);
//    result.add(new Relationship2(this.toolpool, points, "label\n1<>->*", true));


    result.add(new RectBoundaryElement(this.toolpool,
        new RectContainerShape(10, CLASS_GROUP, 220, CLASS_GROUP_HEIGHT),
        "Class and Package",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));

		result.add(new ClassElement2(this.toolpool,
				new RectShape(30, CLASS_GROUP + 40, 100, 60),
				"<<interface>>\n"+
				"ClassName\n"+
				"--\n"+
				"method()",
				Theme.createDefaultBackgroundColor(), 
				Theme.createDefaultBorderColor(),
				Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));

    result.add(new UMLPackageElement(this.toolpool,
        new UMLPackageShape(120, CLASS_GROUP + 150, 100, 40),
        "package",
        Theme.createDefaultBackgroundColor(), 
        Theme.createDefaultBorderColor(), 
        Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));
    
		// mindmap group
    result.add(new RectBoundaryElement(this.toolpool,
        new RectContainerShape(10, MINDMAP_GROUP, 220, MINDMAP_GROUP_HEIGHT),
        "Mind Map Library",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), 
        Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));
    
//    result.add(new TextElement(this.toolpool,
//        new TextShape(20, MINDMAP_GROUP, 100, 30),
//        new Color(0x44, 0x44, 0x44, 1), new Color(0x44, 0x44, 0x44, 1), "*Mindmap Library*", true));

//    result.add(new EllipseElement(this.toolpool, 
//        new EllipseShape(65, MINDMAP_GROUP + 60, 50, 25),
//        "Central Topic", AbstractDiagramItem.createDefaultBackgroundColor(), Color.createDefaultTextColor(), true));
    
    result.add(new MindCentralElement(this.toolpool,
        new MindCentralShape(30, MINDMAP_GROUP + 40, 100, 30),
        "Central Topic",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));
    
    result.add(new ActivityElement(this.toolpool,
        new ActivityShape(30, MINDMAP_GROUP + 120, 100, 30),
        "Main Topic",
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true,
        new DiagramItemDTO()));
    
    result.add(new TextElement(this.toolpool,
        new TextShape(30, MINDMAP_GROUP + 170, 100, 30),
        Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), "Subtopic", true, new DiagramItemDTO()));

    result.add(new NoteElement(this.toolpool,
        new NoteShape(30, MINDMAP_GROUP + 205, 170, 30),
        "*Notes!*", Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true, new DiagramItemDTO()));
    
//    result.add(new NoteElement(this.toolpool,
//        new NoteShape(30, MINDMAP_GROUP + 205, 140, 30),
//        "*Notes!*", AbstractDiagramItem.createDefaultBackgroundColor(), Color.createDefaultTextColor(), true));


    roadmap(result);
    general(result);

    // restore value back
		editorContext.set(EditorProperty.AUTO_RESIZE_ENABLED, currentValue);

		return result;
	}

	public void onClick(Diagram sender, int x, int y, int keys) {
	}

	public void onDoubleClick(Diagram sender, MatrixPointJS point) {
		// TODO: only owner components sends notifications
		// now clients need to know how diagram is constructed
		
		// DUPLICATE COPY HAS BEEN DISABLED
//		Diagram owner = sender.getOwnerComponent();
//		Diagram d = null;
//		if (owner != null) {
//			// duplicate always from owner if exists
//			d = owner.duplicate(surface, 30, 30);
//		} else {
//			d = sender.duplicate(surface, 30, 30);
//		}
//		
//		surface.add(d, true);
	}
	
	@Override
	public void setVisible(boolean visible) {
	  super.setVisible(visible);
	  toolpool.setVisible(visible);
	}

  public Diagram selectedElement() {
    SelectionHandler sh = toolpool.getSelectionHandler();
    Object[] items = sh.getSelectedItems().toArray();
    return items.length == 0 ? null : (Diagram) items[0];
  }

  public Diagram selectedRelationship() {
    return null;
  }
  
  public ISurfaceHandler getSurfaceHandler() {
    return toolpool;
  }

	public ISurfaceHandler getToolPool() {
		return toolpool;
	}

}
