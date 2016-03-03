package net.sevenscales.editor.content;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.user.client.Window;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.IModelingPanel;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.SurfaceLoadedEventListener;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEvent;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEventHandler;
import net.sevenscales.editor.api.event.DiagramsLoadedEvent;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;
import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.content.utils.DiagramDisplaySorter;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.uicomponents.helpers.ConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.IConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.diagram.utils.CommentFactory;
import net.sevenscales.editor.uicomponents.uml.CommentElement;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.domain.js.JsBoardPosition;

public class UiModelContentHandler implements SurfaceLoadedEventListener {
	private static final SLogger logger = SLogger.createLogger(UiModelContentHandler.class);
  public interface IUiDiagramContent {
    public IModelingPanel getModelingPanel();
    public IContent getContent();
  }

	private IUiDiagramContent uiContent;
	private boolean editable;
	private boolean tools;
	private EditorContext editorContext;
	private IModeManager modeManager;
	private ResizeHelpers resizeHelpers;
	private IConnectionHelpers connectionHelpers;
	private ISurfaceHandler thesurface;
	private boolean tourStarted;
	private List<IDiagramItemRO> failed = new ArrayList<IDiagramItemRO>();

	public UiModelContentHandler(IUiDiagramContent uiContent, boolean editable, EditorContext editorContext, IModeManager modeManager) {
		this.editorContext = editorContext;
		this.uiContent = uiContent;
		this.editable = editable;
		this.modeManager = modeManager;

		editorContext.getEventBus().addHandler(BoardRemoveDiagramsEvent.TYPE, new BoardRemoveDiagramsEventHandler() {
			@Override
			public void on(BoardRemoveDiagramsEvent event) {
				if (resizeHelpers != null) {
					resizeHelpers.hideGlobalElement();
				}
				
				if (connectionHelpers != null) {
					connectionHelpers.hideForce();
				}
			}
		});

		listenTour(this);
	}

	public UiModelContentHandler(EditorContext editorContext) {
		this.editorContext = editorContext;
		this.editable = false;
	}

	private native void listenTour(UiModelContentHandler me)/*-{
		if (typeof $wnd.streamStartTour != 'undefined') {
			var su = $wnd.streamStartTour.onValue(function() {
				su()
				me.@net.sevenscales.editor.content.UiModelContentHandler::startTour()();
			})

			var eu = $wnd.streamEndTour.onValue(function() {
				eu()
				me.@net.sevenscales.editor.content.UiModelContentHandler::endTour()();
			})
		}
	}-*/;

	private void startTour() {
		this.tourStarted = true;
	}

	private void endTour() {
		if (thesurface != null) {
			for (Diagram d : thesurface.getDiagrams()) {
				d.setVisible(true);
			}
		}
	}

	public void externalize() {
		IModelingPanel mPanel = (IModelingPanel) uiContent.getModelingPanel();
		ISurfaceHandler surface = mPanel.getSurface();

		IDiagramContent dContent = (IDiagramContent) uiContent.getContent();
		dContent.reset();
		for (Diagram d : surface.getDiagrams()) {
			IDiagramItem di = DiagramItemFactory.createOrUpdate(d);
			if (di != null) {
	      // focus circle is not any supported type even though it is in surface
			  dContent.addItem(di);
//			  di.setDiagramContent(dContent);
			}
		}
	}

	public void internalize() {
		// panel and surface needs to be fully constructed
		// before new items can be added
		IModelingPanel mPanel = (IModelingPanel) uiContent.getModelingPanel();
		ISurfaceHandler surface = mPanel.getSurface();
		surface.addLoadEventListener(this);
	}
	
	public void onLoaded() {
		try {
			logger.debug("onLoaded...");
			IModelingPanel mPanel = (IModelingPanel) uiContent.getModelingPanel();
			mPanel.reset();
			ISurfaceHandler surface = mPanel.getSurface();
			thesurface = surface;
			
			// this is just to guarantee that order is correct...
			resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
			connectionHelpers = ConnectionHelpers.createConnectionHelpers(surface, modeManager);

			// always a diagram content
	    IDiagramContent dContent = (IDiagramContent) uiContent.getContent();
			addContentItems(dContent, surface);
			surface.getEditorContext().getEventBus().fireEvent(new DiagramsLoadedEvent());
			logger.debug("onLoaded... done");
		} catch (Exception e) {
			logger.error("onLoaded... failed", e);
//			Window.alert("Sorry, loading of the board failed.");
			throw new RuntimeException(e);
		}
	}

  public void addContentItems(IDiagramContent dContent, ISurfaceHandler surface) {
//  	Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//			@Override
//			public void execute() {
		  	// disable autoresize on load
		  	editorContext.set(EditorProperty.AUTO_RESIZE_ENABLED, false);
		  	editorContext.set(EditorProperty.ON_CHANGE_ENABLED, false);
		  	editorContext.set(EditorProperty.ON_SURFACE_LOAD, true);

		    addContentItems(dContent, surface, false);

		  	editorContext.set(EditorProperty.ON_SURFACE_LOAD, false);
		  	// but autoresize back
		  	editorContext.set(EditorProperty.ON_CHANGE_ENABLED, true);
		  	editorContext.set(EditorProperty.AUTO_RESIZE_ENABLED, true);
//			}
//		});
  }
  public void addContentItems(IDiagramContent dContent, final ISurfaceHandler surface, final boolean asSelected) {
    // to loop relationships later; to enable anchors
//    final List<Diagram> containerElements = new ArrayList<Diagram>();
  	
  	final ReattachHelpers reattachHelpers = new ReattachHelpers();
  	final CommentFactory commentFactory = new CommentFactory(surface, editable);

  	if (surface.getMouseDiagramManager() != null) {
	    surface.getMouseDiagramManager().getSelectionHandler().unselectAll();
	    surface.getMouseDiagramManager().getSelectionHandler().selectionOn(asSelected);
  	}

    surface.suspendRedraw();

    Tools.setDiagramProperties(dContent.getDiagramProperties());
    
    IDiagramItem[] items = new IDiagramItem[dContent.getDiagramItems().size()];
    dContent.getDiagramItems().toArray(items);

   	Arrays.sort(items, DiagramDisplaySorter.createDiagramItemComparator());
    
    int i = 0;
    boolean atLeastOneAnnotation = false;
    editorContext.set(EditorProperty.HOLD_ARROW_DRAWING, true);

    for (IDiagramItem item : items) {
    	// client id cannot clash because single user environment and done only for 
    	// legacy Confluence boards that didn't use client id for diagram items.
    	ClientIdHelpers.generateClientIdIfNotSet(item, ++i, null, null);
    	if (item.getParentId() != null) {
    		// create comments lazily or any parent child relation elements
    		commentFactory.add(item);
    	} else {
		  	Diagram diagram = DiagramItemFactory.create(item, surface, editable, /*parent*/ null);
		  	if (diagram != null) {
			  	commentFactory.process(diagram);
	    		_addDiagram(diagram, surface, reattachHelpers, commentFactory, asSelected);
		  	} else {
		  		// add to elements that failed...
		  		failed.add(item);
		  		// logger.error("Failed to load: {}", item.toString());
		  	}
    	}

    	if (item.isAnnotation()) {
    		atLeastOneAnnotation = true;
    	}
    }

    if (editable) {
	    Tools.setAtLeastOneAnnotation(atLeastOneAnnotation);
    }

    // could be more common; interface ChildElement e.g. with Relationship
    // Text for reusing "Just Text" elements
    // need to create comments later to use comment thread
    // group
    commentFactory.lazyInit(new CommentFactory.Factory() {
    	public void addDiagram(Diagram diagram, IDiagramItemRO item) {
    		if (diagram != null) {
					_addDiagram(diagram, surface, reattachHelpers, commentFactory, asSelected);
    		} else {
		  		failed.add(item);
    		}
    	}
    }, 0, 0);

    commentFactory.resizeCommentThreads();
    
    editorContext.set(EditorProperty.HOLD_ARROW_DRAWING, false);
    reattachHelpers.reattachRelationshipsAndDraw();

		trigger("board-rendered");

		setBoardPosition(surface);
   // small hack to send package elements to background on load
   // could be replaced with static layering values for container elements
   // e.g. package element should be on background always
//   Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//		@Override
//		public void execute() {
//		   System.out.println("containerElements: " + containerElements);
//		   for (Diagram d : containerElements) {
//		  		 d.moveToBack();
//		   }
//		}
//   });
   
   if (editable) {
	   surface.getMouseDiagramManager().getSelectionHandler().selectionOn(false);
   }
   if (tourStarted) {
   	hideShapes(surface);
   }
   surface.unsuspendRedrawAll();

   if (failed.size() > 0) {
		new FailureHandlerImpl(failed, surface);
   }
  }

  private native void trigger(String event)/*-{
    $wnd.$($doc).trigger(event);
  }-*/;

  private void hideShapes(ISurfaceHandler surface) {
		for (Diagram d : surface.getDiagrams()) {
			d.setVisible(false);
		}
  }

  private void setBoardPosition(ISurfaceHandler surface) {
    int left = Integer.MAX_VALUE;
    int top = Integer.MAX_VALUE;
    int bottom = Integer.MIN_VALUE;
    int right = Integer.MIN_VALUE;

  	for (Diagram d : surface.getDiagrams()) {
  		if (!(d instanceof CircleElement)) {
  			left = Math.min(left, d.getLeft());
  			top = Math.min(top, d.getTop());
  			bottom = Math.max(bottom, d.getTop() + d.getHeight());
				right = Math.max(right, d.getLeft() + d.getWidth());
  		}
  	}

  	if (left > 60000 || top > 60000 || bottom > 60000 || right > 60000) {
  		// some shapes are broken
  		return;
  	} 

  	JsBoardPosition pos = JsBoardPosition.get();

  	if (pos == null) {
  		// e.g. preview generation
  		// setMapView(true);
  		setInitialBoardPosition(surface, left, top, bottom, right);
  		return;
  	}

  	if (pos.isMap()) {
  		// map view is on, nothing else is not restored
  		setMapView(pos.isMap());
  		return;
  	}

  	if (pos.isZoomed()) {
  		scaleTo(pos.getZoom());	
  	}
  	if (pos.isPositioned()) {
  		surface.setTransform(pos.getDx(), pos.getDy());
  	} else {
  		// first time loaded or no values on localstorage
  		// set initial state
	    setInitialBoardPosition(surface, left, top, bottom, right);
  	}
  }

  private native void setMapView(boolean on)/*-{
		var value = on ? 'start' : null
  	$wnd.$($doc).trigger('map-view', value)	
  }-*/;

  private native void scaleTo(float value)/*-{
		$wnd.globalStreams.scaleRestoreStream.push(value)
  }-*/;

  private void setInitialBoardPosition(ISurfaceHandler surface, int left, int top, int bottom, int right) {
		int width = right - left;
		int height = bottom - top;
		if (width <= Window.getClientWidth() && height <= Window.getClientHeight()) {
			center(surface, left, top, width, height, bottom, right);
		} else {
			// birds eye view
			mapView();
		}
  }

  private native void mapView()/*-{
  	$wnd.$($doc).trigger("map-view");
  }-*/;

  private void center(ISurfaceHandler surface, int left, int top, int width, int height, int bottom, int right) {
  	if (left != Integer.MAX_VALUE && top != Integer.MAX_VALUE &&
  			bottom != Integer.MIN_VALUE && right != Integer.MIN_VALUE) {

  		// from 0,0
  		int centerX = width / 2;
  		int centerY = height / 2;

  		// from 0,0
  		int clientWidth = Window.getClientWidth();
  		int cliehtHeight = Window.getClientHeight();
  		int clientCenterX = clientWidth / 2;
  		int clientCenterY = cliehtHeight / 2;

  		int centerDiffX = clientCenterX - centerX;
  		int centerDiffY = clientCenterY - centerY;

	  	// surface.getRootLayer().setTransform(-(left - width / 2), -(top - height / 2));
	  	surface.setTransform(-left + centerDiffX, -top + centerDiffY - 40); // toolbar
	  	notifyBackgroundMove(surface.getRootLayer().getContainer());
  	}
  }

  private native void notifyBackgroundMove(com.google.gwt.core.client.JavaScriptObject group)/*-{
		if (typeof $wnd.globalStreams !== 'undefined') {
	    $wnd.globalStreams.backgroundMoveStream.push({
	      type:'end',
	      matrix: group.getTransform()
	    })
  	}
  }-*/;

  private void _addDiagram(Diagram diagram, ISurfaceHandler surface, ReattachHelpers reattachHelpers, CommentFactory commentFactory, boolean asSelected) {
 		// diagram is saved always using PAPER theme colors, so compare to that
  	BoardColorHelper.applyThemeToDiagram(diagram, Theme.getColorScheme(ThemeName.PAPER), Theme.getCurrentColorScheme());
  	// need to process also comments if any connections on those
  	reattachHelpers.processDiagram(diagram);
   
//     if (diagram instanceof UMLPackageElement) {
//    	 containerElements.add(diagram);
//     }

	 	if (asSelected) {
	 		surface.addAsSelected(diagram, true);
	 	} else {
	 		surface.add(diagram, true);
	 	}
	 }
	
}
