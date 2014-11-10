package net.sevenscales.editor.api;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.editor.api.dojo.FactoryDoJo;

import net.sevenscales.editor.api.LibrarySelections.Library;
import net.sevenscales.editor.api.LibrarySelections.LibrarySelectedHandler;
import net.sevenscales.editor.api.LibraryShapes.LibraryShape;
import net.sevenscales.editor.api.event.LibrarySelectionEvent;
import net.sevenscales.editor.api.event.ThemeChangedEvent;
import net.sevenscales.editor.api.event.ThemeChangedEventHandler;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;

import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.api.ot.OTBuffer;
import net.sevenscales.editor.api.ot.OperationTransaction;
import net.sevenscales.editor.utils.DiagramItemConfiguration;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.content.utils.AbstractDiagramFactory;
import net.sevenscales.editor.content.utils.ShapeParser;
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
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.uicomponents.uml.ActivityChoiceElement;
import net.sevenscales.editor.uicomponents.uml.ActivityElement;
import net.sevenscales.editor.uicomponents.uml.ForkElement;
import net.sevenscales.editor.uicomponents.uml.ActivityEnd;
import net.sevenscales.editor.uicomponents.uml.ActivityStart;
import net.sevenscales.editor.uicomponents.uml.Actor;
import net.sevenscales.editor.uicomponents.uml.ComponentElement;
import net.sevenscales.editor.uicomponents.uml.EllipseElement;
import net.sevenscales.editor.uicomponents.uml.MindCentralElement;
import net.sevenscales.editor.uicomponents.uml.NoteElement;
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
import com.google.gwt.dom.client.Style;


public class Libarary extends SimplePanel implements SurfaceLoadedEventListener, ClickDiagramHandler, IToolSelection {
	private ISurfaceHandler surface;
	private ISurfaceHandler toolpool;
	private List<Diagram> items;
  private ProxyDragHandler proxyDragHandler;
	private EditorContext editorContext;
  private FlowPanel panel;

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
      toolpool.show();
      panel.getElement().getStyle().setOverflowY(Style.Overflow.SCROLL);
      ngHideImageLibrary();
			
			switch (library) {
			case SOFTWARE:
				break;
			case MINDMAP:
				toolpool.getRootLayer().applyTransform(0, -MINDMAP_GROUP + 20);
        // getWidget().getElement().setScrollTop(MINDMAP_GROUP - 220);
				break;
			case ROADMAP:
        // getWidget().getElement().setScrollTop(ROADMAP_GROUP - 220);
        toolpool.getRootLayer().applyTransform(0, -ROADMAP_GROUP + 23);
				break;
      case GENERAL:
        // getWidget().getElement().setScrollTop(GENERAL_GROUP - 220);
        toolpool.getRootLayer().applyTransform(0, -GENERAL_GROUP + 23);
        break;
      case IMAGES:
        // getWidget().getElement().setScrollTop(GENERAL_GROUP - 220);
        // toolpool.getRootLayer().applyTransform(0, -GENERAL_GROUP + 23);
        ngShowImageLibrary();
        panel.getElement().getStyle().setOverflowY(Style.Overflow.HIDDEN);
        toolpool.hide();
        break;
			}

			editorContext.getEventBus().fireEvent(new LibrarySelectionEvent(library));
			editorContext.set(EditorProperty.CURRENT_LIBRARY, library);
		}
	};

	public Libarary(ISurfaceHandler asurface, int height, IModeManager modeManager, EditorContext editorContext, OTBuffer otBuffer, OperationTransaction operationTransaction) {
		this.surface = asurface;
		this.editorContext = editorContext;
		this.toolpool = FactoryDoJo.createSurfaceHandler();
		toolpool.setName(ISurfaceHandler.LIBRARY_AREA);
		toolpool.setDisableOnArea(true);
		toolpool.init(200, 1700, true, modeManager, false, editorContext, otBuffer, operationTransaction);
		
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
    
    panel = new FlowPanel();
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

    createLibraryOnBoardReady(this);
	}

  private native void createLibraryOnBoardReady(Libarary me)/*-{
    $wnd.boardReadyStream.onValue(function() {
      me.@net.sevenscales.editor.api.Libarary::onBoardReady()();
    })
  }-*/;

  private void onBoardReady() {
    if (items == null) {
      editorContext.set(EditorProperty.ON_SURFACE_LOAD, true);
      List<Diagram> items = createToolbarItems();
      editorContext.set(EditorProperty.ON_SURFACE_LOAD, false);
      for (Diagram item : items) {
        toolpool.add(item, true); 
      }
      
      float factor = 0.8f;
      if (TouchHelpers.isSupportsTouch()) {
        factor = 0.7f;
      }
      toolpool.scale(factor);
    }
  }

  private native void ngShowImageLibrary()/*-{
    if (typeof $wnd.ngShowImageLibrary != 'undefined') {
      $wnd.ngShowImageLibrary();
    }
  }-*/;

  private native void ngHideImageLibrary()/*-{
    if (typeof $wnd.ngHideImageLibrary != 'undefined') {
      $wnd.ngHideImageLibrary();
    }
  }-*/;

	private void setStyle() {
    toolpool.setStyleName("toolbar " + Theme.themeCssClass());
  }

  public void addSelectionHandler(DiagramSelectionHandler handler) {
    toolpool.addSelectionListener(handler);
	}

	public void onLoaded() {
	}

  private void flows(List<Diagram> result) {
    final Color flowGroupColor = new Color(0x99, 0x99, 0xff, 0);

    result.add(_create(ElementType.VERTICAL_PARTITION,
                       "Flows",
                       new RectContainerShape(10, ACTIVITY_GROUP, 220, ACTIVITY_GROUP_HEIGHT).toString()));
    
    result.add(_create(ElementType.CHOICE,
                       "",
                       new ActivityChoiceShape(185, ACTIVITY_GROUP + GROUP_HEADING_SPACE + 10, 32, 32).toString()));

    result.add(_create(ElementType.ACTIVITY_START,
                       "",
                       new ActivityStartShape(150, ACTIVITY_GROUP + GROUP_HEADING_SPACE, ActivityStart.ACTIVITY_START_RADIUS).toString(), null, false,
                       Theme.createDefaultBorderColor(),
                       Theme.createDefaultBorderColor(),
                       Theme.createDefaultTextColor()));

    result.add(_create(ElementType.ACTIVITY_END,
                       "",
                       new ActivityEndShape(150, ACTIVITY_GROUP + GROUP_HEADING_SPACE + 50, ActivityEnd.ACTIVITY_END_RADIUS).toString(), null, false,
                       Theme.createDefaultBorderColor(),
                       Theme.createDefaultBorderColor(),
                       Theme.createDefaultTextColor()));

    result.add(_create(ElementType.ACTIVITY, "My Activity",
                 new ActivityShape(30, ACTIVITY_GROUP + GROUP_HEADING_SPACE + 10, 100, 30).toString(),
                 LibraryShapes.CLASS_LIKE_PROPERTIES));

    result.add(_create(ElementType.SEQUENCE, "Sequence",
                 new SequenceShape(50, ACTIVITY_GROUP + 130, 100, 25, 25).toString(),
                 LibraryShapes.CLASS_LIKE_PROPERTIES));
        
    result.add(_create(ElementType.ACTIVITY, "Collaboration",
                 new ActivityShape(80, ACTIVITY_GROUP + 220, 100, 30).toString(),
                 LibraryShapes.CLASS_LIKE_PROPERTIES));

    result.add(_create(ElementType.FORK, "",
                 new ForkShape(150, ACTIVITY_GROUP + 130, 50, 5).toString()));

    result.add(_create(ElementType.FORK, "",
                 new ForkShape(210, ACTIVITY_GROUP + 130, 5, 50, 1).toString()));

    Diagram swimline = _create(ElementType.HORIZONTAL_PARTITION, "Swimline",
                 new HorizontalPartitionShape(22, ACTIVITY_GROUP + 200, 190, 90).toString());
    result.add(swimline);
  }

  private void roadmap(List<Diagram> result) {
    int roadmapIndent = 35;

    Diagram q1 = _create(ElementType.VERTICAL_PARTITION,
                       "Q1",
                       new RectContainerShape(roadmapIndent, ROADMAP_GROUP, 195, ROADMAP_GROUP_HEIGHT).toString());
    q1.setDuplicateMultiplySize(2, 2);
    result.add(q1);

    int marketingY = ROADMAP_GROUP + 25;
    int marketingHeight = ROADMAP_GROUP_HEIGHT / 2 - 10;
    Diagram marketing = _create(ElementType.HORIZONTAL_PARTITION, "Marketing",
                                new HorizontalPartitionShape(10, marketingY, 220, marketingHeight).toString());
    marketing.setDuplicateMultiplySize(3, 3);
    result.add(marketing);

    int activityIndent = roadmapIndent + 10;

    result.add(_create(ElementType.CHOICE,
                       "",
                       new ActivityChoiceShape(activityIndent, marketingY + 45, 32, 32).toString()));

    result.add(_create(ElementType.MIND_CENTRAL,
                       "Keynote",
                       new MindCentralShape(roadmapIndent + 50, marketingY + 10, 100, 30).toString()));

    int productY = marketingY + marketingHeight;
    int productHeight = ROADMAP_GROUP_HEIGHT / 2 - 15;
    Diagram productLine = _create(ElementType.HORIZONTAL_PARTITION, "Product Line",
                                new HorizontalPartitionShape(10, productY, 220, productHeight).toString());

    productLine.setDuplicateMultiplySize(3, 3);
    result.add(productLine);

    result.add(_create(ElementType.ACTIVITY, "Release X",
                 new ActivityShape(roadmapIndent + 10, productY + 30, 100, 30).toString(),
                 LibraryShapes.CLASS_LIKE_PROPERTIES));
  }

  private void general(List<Diagram> result) {
    LibraryShape[][] shapes = new LibraryShape[][]{
      {
        LibraryShapes.get(ElementType.STAR4),
        LibraryShapes.get(ElementType.STAR5),
        LibraryShapes.get(ElementType.ENVELOPE),
        LibraryShapes.get(ElementType.TRIANGLE)
      },
      {
        LibraryShapes.get(ElementType.BUBBLE),
        LibraryShapes.get(ElementType.BUBBLE_R),
        LibraryShapes.get(ElementType.RECT),
        LibraryShapes.get(ElementType.LIGHTBULB)
      },
      {
        LibraryShapes.get(ElementType.CIRCLE),
        LibraryShapes.get(ElementType.SMILEY),
        LibraryShapes.get(ElementType.POLYGON4),
        LibraryShapes.get(ElementType.POLYGON8)
      },
      {
        LibraryShapes.get(ElementType.ARROW_UP),
        LibraryShapes.get(ElementType.ARROW_DOWN),
        LibraryShapes.get(ElementType.ARROW_RIGHT),
        LibraryShapes.get(ElementType.ARROW_LEFT)
      },
      {
        LibraryShapes.get(ElementType.CLOUD),
        LibraryShapes.get(ElementType.FIREWALL),
        LibraryShapes.get(ElementType.SWITCH),
        LibraryShapes.get(ElementType.ROUTER)
      },
      {
        LibraryShapes.get(ElementType.IPHONE),
        LibraryShapes.get(ElementType.WEB_BROWSER),
        LibraryShapes.get(ElementType.DESKTOP),
        LibraryShapes.get(ElementType.LAPTOP)
      },
      {
        LibraryShapes.get(ElementType.SERVER2),
        LibraryShapes.get(ElementType.TABLET_UP),
        LibraryShapes.get(ElementType.TABLET_HORIZONTAL),
        LibraryShapes.get(ElementType.OLD_PHONE)
      },
      {
        LibraryShapes.get(ElementType.ANDROID)
      }
    };

    final int COL_SIZE = 40;
    final int ROW_SIZE = 50;
    final int MARGIN = 15;
    int row = 0;
    for (LibraryShape[] rowShapes : shapes) {
      int col = 0;
      int colpos = MARGIN;
      for (LibraryShape colShape : rowShapes) {
        Diagram el = _create(colShape.elementType,
                       "",
                       new GenericShape(colShape.elementType.getValue(), 
                             // MARGIN + col * COL_SIZE + col * MARGIN, 
                             colpos + col * MARGIN,
                             GENERAL_GROUP + row * ROW_SIZE + row * MARGIN, 
                             colShape.width, 
                             colShape.height, 
                             colShape.shapeProperties, 
                             null).toString());

        // Diagram el = new GenericElement(this.toolpool,
        //     new GenericShape(colShape.elementType.getValue(), 
        //                      // MARGIN + col * COL_SIZE + col * MARGIN, 
        //                      colpos + col * MARGIN,
        //                      GENERAL_GROUP + row * ROW_SIZE + row * MARGIN, 
        //                      colShape.width, 
        //                      colShape.height, 
        //                      colShape.shapeProperties, 
        //                      null),
        //       "",
        //       Theme.createDefaultBackgroundColor(),
        //       Theme.createDefaultBorderColor(),
        //       Theme.createDefaultTextColor(),
        //       true,
        //       DiagramItemDTO.createByType(colShape.elementType));
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
    result.add(_create(ElementType.VERTICAL_PARTITION,
                       "Software Sketching",
                       new RectContainerShape(10, SOFTWARE_SKETCHING_GROUP, 220, SOFTWARE_SKETCHING_GROUP_HEIGHT).toString()));
    
    result.add(_create(ElementType.NOTE,
                       "*Note* this!",
                       new NoteShape(30, SOFTWARE_SKETCHING_GROUP + GROUP_HEADING_SPACE, 170, 45).toString()));

    result.add(_create(ElementType.ACTOR,
                       "<<system>>\nActor",
                       new ActorShape(30, SOFTWARE_SKETCHING_GROUP + 120, 25, 40).toString()));

    result.add(_create(ElementType.ELLIPSE,
                       "Use Case",
                       new EllipseShape(145, SOFTWARE_SKETCHING_GROUP + 150, 50, 25).toString()));

    result.add(_create(ElementType.STORAGE,
                       "Db",
                       new DbShape(160, SOFTWARE_SKETCHING_GROUP + 220, 100, 30).toString()));
    
    result.add(_create(ElementType.COMPONENT,
                       "Component",
                       new ComponentShape(70, SOFTWARE_SKETCHING_GROUP + 350, 100, 60).toString()));
    
		// special border => no border
    result.add(_create(ElementType.TEXT_ITEM,
                       "### Just text",
                       new TextShape(120, SOFTWARE_SKETCHING_GROUP + 300, 100, 30).toString()));

    result.add(_create(ElementType.SERVER,
                       "Server",
                       new ServerShape(30, SOFTWARE_SKETCHING_GROUP + 215, 60, 80).toString()));
    
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

    result.add(_create(ElementType.VERTICAL_PARTITION,
                       "Class and Package",
                       new RectContainerShape(10, CLASS_GROUP, 220, CLASS_GROUP_HEIGHT).toString()));

    result.add(_create(ElementType.CLASS, "<<interface>>\n" +
                 "ClassName\n" +
                 "--\n" +
                 "method()",
                 new RectShape(30, CLASS_GROUP + 40, 100, 60).toString(),
                 LibraryShapes.CLASS_LIKE_PROPERTIES));

    result.add(_create(ElementType.PACKAGE,
                       "package",
                       new UMLPackageShape(120, CLASS_GROUP + 150, 100, 40).toString()));
    
		// mindmap group
    result.add(_create(ElementType.VERTICAL_PARTITION,
                       "Mind Map Library",
                       new RectContainerShape(10, MINDMAP_GROUP, 220, MINDMAP_GROUP_HEIGHT).toString()));

    result.add(_create(ElementType.MIND_CENTRAL,
                   "Central Topic",
                   new MindCentralShape(30, MINDMAP_GROUP + 40, 100, 30).toString()));
    
    result.add(_create(ElementType.ACTIVITY,
               "Main Topic",
               new ActivityShape(30, MINDMAP_GROUP + 120, 100, 30).toString()));
    
    result.add(_create(ElementType.TEXT_ITEM,
               "Subtopic",
               new TextShape(30, MINDMAP_GROUP + 170, 100, 30).toString()));

    result.add(_create(ElementType.NOTE,
               "*Notes!*",
               new NoteShape(30, MINDMAP_GROUP + 205, 170, 45).toString()));

    roadmap(result);
    general(result);

    // restore value back
		editorContext.set(EditorProperty.AUTO_RESIZE_ENABLED, currentValue);

		return result;
	}

  private Diagram _create(ElementType type, String text, String shape) {
    return _create(type, text, shape, null, true, null, null, null);
  }

  private Diagram _create(ElementType type, String text, String shape, Integer properties) {
    return _create(type, text, shape, properties, true, null, null, null);
  }

  private Diagram _create(ElementType type, String text, String shape, Integer properties, boolean setcolors, Color backgroundColor, Color borderColor, Color textColor) {
    DiagramItemDTO item = DiagramItemDTO.createByType(type);
    if (setcolors) {
      DiagramItemConfiguration.setDefaultColors(item);
    } else {
      DiagramItemConfiguration.setColors(item, backgroundColor, borderColor, textColor);
    }
    item.setText(text);
    item.setShape(shape);
    if (properties != null) {
      item.setShapeProperties(properties);
    }

    return ShapeParser.createDiagramElement(item, this.toolpool);
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
