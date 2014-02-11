package net.sevenscales.editor.api;

import java.util.ArrayList;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.diagram.utils.RelationshipHelpers;
import net.sevenscales.editor.gfx.domain.IGraphics;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEvent;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEventHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.editor.diagram.shape.RelationshipShape2;


public class SketchDiagramAreaHandler implements MouseDiagramHandler {
	private static final SLogger logger = SLogger.createLogger(SketchDiagramAreaHandler.class);

  static {
    SLogger.addFilter(SketchDiagramAreaHandler.class);
  }
	
	private ISurfaceHandler surface;
  private int downX;
  private int downY;
  private boolean noSelectedItems;
  private GridUtils gridUtils = new GridUtils();
  private Relationship2 createdRelationship;
  private AnchorElement currentAnchorElement;
  private CircleElement currentHandle;
  private IModeManager modeManager;
  private boolean modeManual;

	public SketchDiagramAreaHandler(ISurfaceHandler surface, IModeManager modeManager) {
		this.surface = surface;
		this.modeManager = modeManager;
		// kept just to remind of surface event registry usage :)
//		surface.getEventRegistry().register(SurfaceRegistryEvent.CRETE_RELATIONSHIP, new IRegistryEventObserver() {
//      public void handleEvent(Integer eventId, Object data) {
//        RelationshipHandle.RelationshipHandleEvent event = (RelationshipHandleEvent) data;
//      }
//    });

    surface.getEditorContext().getEventBus().addHandler(EditDiagramPropertiesStartedEvent.TYPE, new EditDiagramPropertiesStartedEventHandler() {
        public void on(EditDiagramPropertiesStartedEvent event) {
          // hide if there is double clicked on connection point on a element
          // and move is kinda started => would create a self link
          removeAutoRelation();
        }
    });
	}

	public void onClick(Diagram sender, int x, int y, int keys) {
	}

	public void onDoubleClick(Diagram sender, MatrixPointJS point) {
		// Currently double click opens text editor.
//		Diagram owner = sender.getOwnerComponent();
//		Diagram d = null;
//		if (owner != null) {
//			// duplicate always from owner if exists
//			d = owner.duplicate();
//		} else {
//			d = sender.duplicate();
//		}
//
//		boolean duplicated = true;
//		surface.addAsSelected(d, true, duplicated);
	}

  private boolean notFreehandMode() {
    return !surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE);
  }

  public boolean onMouseDown(Diagram sender, MatrixPointJS point, final int keys) {
    boolean result = false;
  	// logger.debug("onMouseDown...");
    this.downX = point.getX();
    this.downY = point.getY();
    
    // to check that surface doesn't have item selected
    SelectionHandler sh = surface.getSelectionHandler();
    this.noSelectedItems = sh.getSelectedItems().size() == 0;
    
    // doesn't support Relationship2 yet
    // removed shift key down
    boolean connectionMode = modeManager.isConnectMode();
  	// logger.debug("onMouseDown connectionMode({}), sender({}) ... 1", connectionMode, sender);
    if ( sender != null && !(sender instanceof Relationship2) && connectionMode && notFreehandMode() && createdRelationship == null) {
    	logger.debug("Starting to create quick connection sender({})... 2", sender);
      // set connection mode on automatically as long as shift key is down
      modeManual = modeManager.isConnectMode();
      modeManager.setConnectionMode(true);
      
      // create relationship
      // - just 1 pixel length relationship
      int xx = GridUtils.align(point.getX() - ScaleHelpers.scaleValue(surface.getRootLayer().getTransformX(), surface.getScaleFactor()));
      int yy = GridUtils.align(point.getY() - ScaleHelpers.scaleValue(surface.getRootLayer().getTransformY(), surface.getScaleFactor()));

      if (modeManager.isForceConnectionPoint()) {
      	// force connection point to earlier specified location => currently connection helpers sets this
      	xx = modeManager.getConnectionPointX();
      	yy = modeManager.getConnectionPointY();
      }

      xx = xx - sender.getTransformX();
      yy = yy - sender.getTransformY();
      
      ArrayList<Integer> points = new ArrayList<Integer>();
      points.add(xx);
      points.add(yy);
      points.add(xx+1);
      points.add(yy+1);
      String defaultRelationship = RelationshipHelpers.relationship(sender, surface.getEditorContext());
      this.createdRelationship = new Relationship2(surface, new RelationshipShape2(points), defaultRelationship, Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), true, new DiagramItemDTO());
      logger.debug("createdRelationship {}...", createdRelationship);
      createdRelationship.setVisible(false);
      
      // attach
      // - ask from diagram onAttachArea and attach start handle to given event point
      // - diagram could provide force attach to nearest point
      this.currentAnchorElement = sender.onAttachArea(createdRelationship.getStartAnchor(), xx, yy);
      if (currentAnchorElement == null) {
        throw new RuntimeException("currentAnchorElement is null");
      }
      createdRelationship.getStartAnchor().setPoint(currentAnchorElement.getAx(), currentAnchorElement.getAy());
      
      // simulate dragging with relationship end handle
      // - call MouseDiagramDragHandler:: mouse down
      // - then just normal mouse move and MouseDiagramDragHandler handles that and rest goes naturally
      // if however relationship is not attached to anywhere, remove it
      this.currentHandle = createdRelationship.getEndHandler();
      currentHandle.setVisible(false);

      // guarantee that handle is dragged and not bubbled diagram itself
      surface.getMouseDiagramManager().getDragHandler().force(currentHandle);
      result = true;
      logger.debug("SketchDiagramAreaHandler connection created... done");
    }
    return result;
  }

  public void onMouseEnter(Diagram sender, MatrixPointJS point) {
    // TODO Auto-generated method stub
    
  }

  public void onMouseLeave(Diagram sender, MatrixPointJS point) {
    // TODO Auto-generated method stub
    
  }

  public void onMouseMove(Diagram sender, MatrixPointJS point) {
  	// with connection helpers, it is not necessary to have a threshold
   	// if (Math.abs(downX - point.getX()) < 5 && Math.abs(downY - point.getY()) < 5) {
   	// 	return;
   	// }
  	
    if (createdRelationship != null && currentAnchorElement != null) {
      logger.debug("SketchDiagramAreaHandler.onMouseMove createdRelationship {}...", createdRelationship);
    	// make surface think that handle is clicked...
      surface.getMouseDiagramManager().getDragHandler().onMouseDown(currentHandle, point, IGraphics.SHIFT);
      surface.getSelectionHandler().onMouseDown(currentHandle, point, 0);
      createdRelationship.setVisible(true);
      createdRelationship.attachAnchor(
          currentAnchorElement, 
          createdRelationship.getStartPosX(), 
          createdRelationship.getStartPosY(), 
          createdRelationship.getStartAnchor(), true);
      logger.debug("createdRelationship getStartClientId() {}...", createdRelationship.getStartClientId());
      surface.add(createdRelationship, true);
      createdRelationship.calculateHandles();
      currentAnchorElement.dispatch(0);
      
      currentHandle.setVisible(true);

      createdRelationship = null;
      currentAnchorElement = null;
      currentHandle = null;
    } else if (createdRelationship != null && currentAnchorElement == null) {
      removeAutoRelation();
    }
  }
  
  @Override
  public void onTouchStart(Diagram sender, MatrixPointJS point) {
  	
  }
  
  @Override
  public void onTouchMove(Diagram sender, MatrixPointJS point) {
  }
  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
  }

  private void removeAutoRelation() {
    if (createdRelationship != null) {
      createdRelationship.removeFromParent();
    }
    createdRelationship = null;
    currentAnchorElement = null;
    currentHandle = null;
  }

  public void onMouseUp(Diagram sender, MatrixPointJS point) {
    if (createdRelationship != null) {
      // this was a just select for item not creating a relationship
      removeAutoRelation();
    }
    surface.getMouseDiagramManager().getDragHandler().releaseForce(currentHandle);

    if (!modeManual) {
      // if mode has not been set manually then release connection mode
      modeManager.setConnectionMode(false);
    }

    
    // Currently one click adding is removed and only
    // drag and drop or double click is supported
//
//    Diagram sample = toolSelection.selectedElement();
//    
//    if (noSelectedItems && sample != null && checkPoint(x, y, 2)) { 
//      // in case of e.g. relationship bend handle might be selected
//      sample = sample.getOwnerComponent() == null ? sample : sample.getOwnerComponent();
//      
//      x = GridUtils.align(x);
//      y = GridUtils.align(y);
//      Diagram d = sample.duplicate(surface, x, y);
//      surface.addAsSelected(d, true);
//    }
  }
  
  private boolean checkPoint(int x, int y, int gridDivision) {
    return (x >= downX - GridUtils.GRID_SIZE/gridDivision && x <= downX + GridUtils.GRID_SIZE/gridDivision) &&
           (y >= downY - GridUtils.GRID_SIZE/gridDivision && y <= downY + GridUtils.GRID_SIZE/gridDivision); 
  }
}
