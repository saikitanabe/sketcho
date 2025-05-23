package net.sevenscales.editor.api;

import java.util.ArrayList;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEvent;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEventHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.shape.RelationshipShape2;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.diagram.utils.RelationshipHelpers;
import net.sevenscales.editor.gfx.domain.IGraphics;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;


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
  private Relationship2 tobeadded;
  private AnchorElement currentAnchorElement;
  private CircleElement currentHandle;
  private IModeManager modeManager;

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

  public boolean onMouseDown(OrgEvent event, Diagram sender, MatrixPointJS point, int keys) {
    boolean result = false;
    try {
      // potentially problematic place, so reloading page if fails
      result = mouseDown(sender, point, keys);
    } catch (Exception e) {
      net.sevenscales.domain.utils.Error.reload(e);
    }
    return result;
  }

  private boolean mouseDown(Diagram sender, MatrixPointJS point, int keys) {

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

      // Default arrow type is curved
      RelationshipShape2 rshape = new RelationshipShape2(points);
      rshape.asCurve();
      DiagramItemDTO di = new DiagramItemDTO();
      if (Tools.isCurvedArrow()) {
        di.setShapeProperties(ShapeProperty.CURVED_ARROW.getValue());
      }

      // regression bug: sequence elements arrows are all moved back
      // to original center position!
      // di.addShapeProperty(ShapeProperty.CLOSEST_PATH);

      // Automatically center connection at first time.
      if (!sender.isSequenceElement()) {
        di.addShapeProperty(ShapeProperty.CENTERED_PATH);
      }
      // Default arrow type is curved ends configuration

      this.createdRelationship = new Relationship2(surface, rshape, defaultRelationship, Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true, di);
      logger.debug("createdRelationship {}...", createdRelationship);
      createdRelationship.setVisible(false);
      
      // attach
      // - ask from diagram onAttachArea and attach start handle to given event point
      // - diagram could provide force attach to nearest point
      this.currentAnchorElement = sender.onAttachArea(createdRelationship.getStartAnchor(), xx, yy);
      if (currentAnchorElement != null) {
        createdRelationship.getStartAnchor().setPoint(currentAnchorElement.getAx(), currentAnchorElement.getAy());
        
        // simulate dragging with relationship end handle
        // - call MouseDiagramDragHandler:: mouse down
        // - then just normal mouse move and MouseDiagramDragHandler handles that and rest goes naturally
        // if however relationship is not attached to anywhere, remove it
        this.currentHandle = createdRelationship.getEndHandler();
        if (currentHandle == null) {
          return false;
        }
        currentHandle.setVisible(false);

        // guarantee that handle is dragged and not bubbled diagram itself
        surface.getMouseDiagramManager().getDragHandler().force(currentHandle);
        result = true;
        logger.debug("SketchDiagramAreaHandler connection created... done");
      } else {
        // cleanup
        createdRelationship = null;
      }
    }
    return result;

  }

  @Override
  public void onMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point) {
    // TODO Auto-generated method stub
    
  }

  public void onMouseLeave(Diagram sender, MatrixPointJS point) {
    // TODO Auto-generated method stub
    
  }

  public void onMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
    try {
      mouseMove(event, sender, point);
    } catch (Exception e) {
      net.sevenscales.domain.utils.Error.reload(e);
    } finally {
      // always restore state to store to server
      surface.getEditorContext().set(EditorProperty.ON_CHANGE_ENABLED, true);
    }
  }

  private void mouseMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
    // with connection helpers, it is not necessary to have a threshold
    // ST 28.2.2019: iPad Pro at least needs this or start to create a self
    // connection on a click
    if (Math.abs(downX - point.getX()) < 10 && Math.abs(downY - point.getY()) < 10) {
     return;
    }
    
    if (createdRelationship != null && currentAnchorElement != null) {
      logger.debug("SketchDiagramAreaHandler.onMouseMove createdRelationship {}...", createdRelationship);
      // make surface think that handle is clicked...
      surface.getMouseDiagramManager().getDragHandler().onMouseDown(event, currentHandle, point, IGraphics.SHIFT);
      surface.getSelectionHandler().onMouseDown(event, currentHandle, point, 0);
      createdRelationship.setVisible(true);
      createdRelationship.attachAnchor(
          currentAnchorElement, 
          createdRelationship.getStartPosX(), 
          createdRelationship.getStartPosY(), 
          createdRelationship.getStartAnchor(), true);
      logger.debug("createdRelationship getStartClientId() {}...", createdRelationship.getStartClientId());

      // do not send insert event to server
      // connection insert + move => insert only
      surface.getEditorContext().set(EditorProperty.ON_CHANGE_ENABLED, false);
      surface.add(createdRelationship, true);
      tobeadded = createdRelationship;
      surface.getEditorContext().set(EditorProperty.ON_CHANGE_ENABLED, true);

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
  public void onTouchStart(OrgEvent event, Diagram sender, MatrixPointJS point) {
  	
  }
  
  @Override
  public void onTouchMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
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

  public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
    try {
      mouseUp(sender, point, keys);
    } catch (Exception e) {
      net.sevenscales.domain.utils.Error.reload(e);
    } finally {
      // in debug mode can be checked if still is able to save to server
      surface.getEditorContext().set(EditorProperty.ON_CHANGE_ENABLED, true);
    }
  }

  private void mouseUp(Diagram sender, MatrixPointJS point, int keys) {
    if (createdRelationship != null) {
      // this was a just select for item not creating a relationship
      removeAutoRelation();
    }

    // set this relationship to be added on drag end
    // - now addition will be only a one single insert, which can be undo as well
    surface.getMouseDiagramManager().getDragHandler().insertMoveElement(tobeadded);
    surface.getMouseDiagramManager().getDragHandler().releaseForce(currentHandle);

    // there is no manual mode at the moment, it is always possible to make connections
    modeManager.setConnectionMode(false);
    
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
