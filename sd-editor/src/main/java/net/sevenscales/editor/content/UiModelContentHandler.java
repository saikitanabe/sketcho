package net.sevenscales.editor.content;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
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
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.uicomponents.helpers.ConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.IConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.diagram.utils.CommentFactory;
import net.sevenscales.editor.uicomponents.uml.CommentElement;

public class UiModelContentHandler implements SurfaceLoadedEventListener {
	private static final SLogger logger = SLogger.createLogger(UiModelContentHandler.class);
  public interface IUiDiagramContent {
    public IModelingPanel getModelingPanel();
    public IContent getContent();
  }

	private IUiDiagramContent uiContent;
	private boolean editable;
	private EditorContext editorContext;
	private IModeManager modeManager;
	private ResizeHelpers resizeHelpers;
	private IConnectionHelpers connectionHelpers;

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

  public void addContentItems(final IDiagramContent dContent, final ISurfaceHandler surface) {
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

    surface.getMouseDiagramManager().getSelectionHandler().unselectAll();
    surface.getMouseDiagramManager().getSelectionHandler().selectionOn(asSelected);

    surface.suspendRedraw();
    
    IDiagramItem[] items = new IDiagramItem[dContent.getDiagramItems().size()];
    dContent.getDiagramItems().toArray(items);
    
    // TODO this is deprecated, now using layers; elements use those by default
//    Arrays.sort(items, new Comparator<IDiagramItem>() {
//			@Override
//			public int compare(IDiagramItem arg0, IDiagramItem arg1) {
//				if (arg0.getType().equals("package")) {
//					return -2;
//		    } else if (arg0.getType().equals("relationship")) {
//					return -1;
//		    }
//
//				if (arg1.getType().equals("package")) {
//					return 2;
//		    } else if (arg1.getType().equals("relationship")) {
//					return 1;
//		    }
//				return 0;
//			}
//    });
    
    int i = 0;
    boolean atLeastOneAnnotation = false;
    for (IDiagramItem item : items) {
    	// client id cannot clash because single user environment and done only for 
    	// legacy Confluence boards that didn't use client id for diagram items.
    	ClientIdHelpers.generateClientIdIfNotSet(item, ++i, null);
    	if (CommentElement.TYPE.equals(item.getType())) {
    		// create comments lazily or any parent child relation elements
    		commentFactory.add(item);
    	} else {
		  	Diagram diagram = DiagramItemFactory.create(item, surface, editable);
		  	commentFactory.process(diagram);
    		_addDiagram(diagram, surface, reattachHelpers, commentFactory, asSelected);
    	}

    	if (item.isAnnotation()) {
    		atLeastOneAnnotation = true;
    	}
    }

    Tools.setAtLeastOneAnnotation(atLeastOneAnnotation);

    // could be more common; interface ChildElement e.g. with Relationship
    // Text for reusing "Just Text" elements
    // need to create comments later to use comment thread
    // group
    commentFactory.lazyInit(new CommentFactory.Factory() {
    	public void addDiagram(Diagram diagram) {
				_addDiagram(diagram, surface, reattachHelpers, commentFactory, asSelected);
    	}
    });

    commentFactory.resizeCommentThreads();
    
    reattachHelpers.reattachRelationships();

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
   
   surface.getMouseDiagramManager().getSelectionHandler().selectionOn(false);
   surface.unsuspendRedrawAll();
  }

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
