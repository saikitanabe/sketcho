package net.sevenscales.editor.diagram.drag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.Window;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEvent;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEventHandler;
import net.sevenscales.editor.api.event.DiagramElementAddedEvent;
import net.sevenscales.editor.api.event.DiagramElementAddedEventHandler;
import net.sevenscales.editor.api.event.DiagramsLoadedEvent;
import net.sevenscales.editor.api.event.DiagramsLoadedEventHandler;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEvent;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEventHandler;
import net.sevenscales.editor.api.event.SelectionEvent;
import net.sevenscales.editor.api.event.SelectionEventHandler;
import net.sevenscales.editor.api.event.UnselectAllEvent;
import net.sevenscales.editor.api.event.UnselecteAllEventHandler;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramDragHandler;
import net.sevenscales.editor.diagram.DragState;
import net.sevenscales.editor.diagram.ISelectionHandler;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.diagram.MouseDiagramHandlerManager;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.diagram.utils.MouseDiagramEventHelpers;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.gfx.domain.Color;
// import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

/**
 * NOTE: not used for toolbar proxy dragging. See ProxyDragHandler for this. 
 *
 */
public class MouseDiagramDragHandler implements MouseDiagramHandler, DragState {
	private static final SLogger logger = SLogger.createLogger(MouseDiagramDragHandler.class);

	static {
		SLogger.addFilter(MouseDiagramDragHandler.class);
	}

	private Diagram currentDiagram;
	private Set<DiagramDragHandler> dragHandlers;
	private boolean dragging = false;
	private boolean mouseDown = false;
	private MouseDiagramHandlerManager parent;
//	private Point mouseDownPoint = new Point();
	private GridUtils gridUtils = new GridUtils();
//	private int prevX = 0;
//	private int prevY = 0;
	private int dispachSequence;
	private boolean forceOn;
	private ISelectionHandler selectionHandler;
	private Set<Diagram> forcedItems = new HashSet<Diagram>();
	private ISurfaceHandler surface;
	// private Map<Relationship2, ConnectionMoveHandler> moveHandlers = new HashMap<Relationship2, ConnectionMoveHandler>();
	private int prevDX;
	private int prevDY;
	private ILine verticalLine;
	private ILine horizontalLine;
//	private DiagramHelpers.ComplexElementHandler complexElementHandler = new DiagramHelpers.ComplexElementHandler();
	private int windowHeight;
	private int windowWidth;
	private Diagram oneSelected;
	private boolean delayX;
	private int delayStartX;
	private int delayStartY;
	private boolean delayY;
	private Map<Integer, List<Diagram>> highlightCentersX;
	private HashMap<Integer, List<Diagram>> highlightCentersY;
	private List<Diagram> prevXHigilights;
	private List<Diagram> prevYHighlights;
	private Relationship2 insertMoveToSingle;
	// private static final Color LINE_HELPER_COLOR = new Color(0xdd, 0xdd, 0xdd, 1); 
	private static final Color LINE_HELPER_COLOR = new Color(0xDF, 0x00, 0x3B, 1);
	
	public MouseDiagramDragHandler(ISurfaceHandler surface, MouseDiagramHandlerManager parent,
			ISelectionHandler selectionHandler) {
		this.surface = surface;
		this.parent = parent;
		currentDiagram = null;
		dragHandlers = new HashSet<DiagramDragHandler>();
		this.selectionHandler = selectionHandler;
		
		surface.getEditorContext().getEventBus().addHandler(DiagramsLoadedEvent.TYPE, new DiagramsLoadedEventHandler() {
			@Override
			public void on(DiagramsLoadedEvent event) {
				initLineHelpersDiagrams();
			}
		});
		
		surface.getEditorContext().getEventBus().addHandler(DiagramElementAddedEvent.TYPE, new DiagramElementAddedEventHandler() {
			@Override
			public void onAdded(DiagramElementAddedEvent event) {
				addLineHelpersDiagrams(event.getDiagrams());
			}
		});
		
		surface.getEditorContext().getEventBus().addHandler(BoardRemoveDiagramsEvent.TYPE, new BoardRemoveDiagramsEventHandler() {
			@Override
			public void on(BoardRemoveDiagramsEvent event) {
				removeLineHelpersDiagrams(event.getRemoved());
			}
		});
		
		surface.getEditorContext().getEventBus().addHandler(EditDiagramPropertiesStartedEvent.TYPE, new EditDiagramPropertiesStartedEventHandler() {
			@Override
			public void on(EditDiagramPropertiesStartedEvent event) {
				hideLineHelpers();
			}
		});
		
		surface.getEditorContext().getEventBus().addHandler(UnselectAllEvent.TYPE, new UnselecteAllEventHandler() {
      @Override
      public void onUnselectAll(UnselectAllEvent event) {
        restorePreHighlightColors();
      }
    });
		
    surface.getEditorContext().getEventBus().addHandler(SelectionEvent.TYPE, new SelectionEventHandler() {
      @Override
      public void onSelection(SelectionEvent event) {
        restorePreHighlightColors();
      }
    });

	}
		
	private ILine createLine(Color strokeColor, double strokeWidth, IGroup group) {
		ILine line = IShapeFactory.Util.factory(true).createLine(group);
		line.setStyle(ILine.SOLID);
		line.setStrokeWidth(strokeWidth);
//		String strokeColor = ColorHelpers.createOppositeColor(parent.getBackgroundColor());
		line.setStroke(strokeColor);
		line.setVisibility(false);
		return line;
	}

	public boolean onMouseDown(OrgEvent event, Diagram sender, MatrixPointJS point, int keys) {
		if (sender != null && !parent.isResizing() && !surface.getEditorContext().isFreehandMode()) {
			// Debug.print("onMouseDown:"+x+"y:"+y);
			// System.out.println("drag mouse down"+sender);
			// drag handler is not interested in canvas events
			if (!forceOn) {
				currentDiagram = sender;
			}
			mouseDown = true;
			dragging = false;
//			mouseDownPoint.x = point.getScreenX();
//			mouseDownPoint.y = point.getScreenY();
//			prevX = point.getScreenX();
//			prevY = point.getScreenY();

			gridUtils.init(point.getScreenX(), point.getScreenY(), surface.getScaleFactor());
			
			initLineHelpers();
//	    complexElementHandler.reset();
		} 
		return false;
	}

  @Override
	public void onMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point) {

	}

	public void onMouseLeave(Diagram sender, MatrixPointJS point) {
	}

  @Override
	public void onMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
		if (sender != null) {
			// handle only canvas move events
			return;
		}

		drag(point);
	}
	
	private void initLineHelpers() {
		// logger.debug("initLineHelpers...");
		oneSelected = null;
		
		if (verticalLine == null && horizontalLine == null) {
			verticalLine = createLine(LINE_HELPER_COLOR, 1.0, surface.getElementLayer());
			horizontalLine = createLine(LINE_HELPER_COLOR, 1.0, surface.getElementLayer());
		}
		
		if (moveItems().size() == 1) {
			oneSelected = moveItems().iterator().next();
			windowHeight = Window.getClientHeight();
			windowWidth = Window.getClientWidth();
		}
	}
	
  private void initLineHelpersDiagrams() {
  	highlightCentersX = new HashMap<Integer, List<Diagram>>();
  	highlightCentersY = new HashMap<Integer, List<Diagram>>();
  	addLineHelpersDiagrams(surface.getDiagrams());
	}

	private void addLineHelpersDiagrams(List<Diagram> diagrams) {
    for (Diagram d : diagrams) {
    	if ( !(d instanceof CircleElement) && !(d instanceof Relationship2) && d.supportsAlignHighlight()) {
    		List<Diagram> xs = highlightCentersX.get(d.getCenterX());
    		if (xs == null) {
    			xs = new ArrayList<Diagram>();
    			highlightCentersX.put(d.getCenterX(), xs);
    		}
    		xs.add(d);

    		List<Diagram> ys = highlightCentersY.get(d.getCenterY());
    		if (ys == null) {
    			ys = new ArrayList<Diagram>();
    			highlightCentersY.put(d.getCenterY(), ys);
    		}
    		ys.add(d);
    	}
    }
	}
	
	private void removeLineHelpersDiagrams(Set<Diagram> removed) {
		initLineHelpersDiagrams();
//		for (Map.Entry<Integer, List<Diagram>> hx : highlightCentersX.entrySet()) {
//			for (Diagram r : removed) {
//				if (hx.getValue() == r) {
//					hx.getValue().remove(r);
//					if (hx.getValue().size() == 0) {
//						// free some memory
//						highlightCentersX.remove(hx.getKey());
//					}
//				}
//			}
//		}
	}

	private void hideLineHelpers() {
		if (verticalLine != null) { 
			verticalLine.setVisibility(false);
		}
		if (horizontalLine != null) {
			horizontalLine.setVisibility(false);
		}
  	if (oneSelected != null) {
//  		restoreHighlightColors();
  	}
	}

	private void showLineHelpers() {
		if (moveItems().size() == 1) {
			// do not show line helpers for multiple selected items
			verticalLine.setVisibility(true);
			verticalLine.moveToBack();
			horizontalLine.setVisibility(true);
			horizontalLine.moveToBack();
		}
	}

	private void restoreHighlightColors() {
    for (Diagram d : surface.getDiagrams()) {
    	if ( !(d instanceof CircleElement) && !(d instanceof Relationship2) && d != oneSelected) {
  			d.restoreHighlighColor(null);
    	}
    }
	}

	private void drag(MatrixPointJS point) {
 		if (currentDiagram != null && !parent.isResizing() && gridUtils.passTreshold(point, 5)) {
			// if not resizing area => drag
			if (mouseDown && !dragging) {
				// Debug.print("dragstarted");
				// mouse down but dragging not yet started => dragStart
				// logger.debug("drag start, moveItems={}", moveItems());
				startDragging(currentDiagram);
			}
//			logger.debug("drag {}", currentDiagram);
			MatrixPointJS dp = MatrixPointJS.createScaledTransform(gridUtils.dx(point.getScreenX()), gridUtils.dy(point.getScreenY()), surface.getScaleFactor());

			int dx = 0;
			int dy = 0;
			int dpx = prevDX;
			int dpy = prevDY;

			if (oneSelected == null || allowToMoveX(dp.getDX())) {
				dx = dp.getDX() - prevDX;
		    prevDX = dp.getDX();
		    dpx = dp.getDX();
			}
			
			if (oneSelected == null || allowToMoveY(dp.getDY())) {
				dy = dp.getDY() - prevDY;
		    prevDY = dp.getDY();
		    dpy = dp.getDY();
			}

			move(dx, dy);
			drawLineHelpers(dpx, dpy, dp.getDX(), dp.getDY());
		}
	}
	
	private void move(int dx, int dy) {
		int sequence = ++MouseDiagramDragHandler.this.dispachSequence;
		for (Diagram dd : moveItems()) {
			if (!(dd.getOwnerComponent() instanceof CircleElement) || currentDiagram == dd) {
//					dd.resetTransform();
				
//				complexElementHandler.hideComplexElement(dd);
				// move element itself
				// if (UiUtils.isSafari()) {
				// safari doesn't support cumulative transformations
					dd.setTransform(prevDX + dd.getSnaphsotTransformX(), prevDY + dd.getSnaphsotTransformY());
				// } else {
					// Chrome supports cumulative transformations
				// 	dd.applyTransform(dx, dy);
				// }

				// NOTE: this is generic, but consumes more processing power. Only some few global handlers should
				// use this, like resize helpers and connection helpers!!
				for (DiagramDragHandler h : dragHandlers) {
					h.onDrag(dd, dx, dy);
				}
				
        // dd might be a circle element that is connection handle of a relationship
				// currently Relationship2 and SequenceElement follow CircleElement to adjust line position
				moveRelatedDragHandlerAccordingToCircleHandle(dd, dx, dy);

				// some connections are following this element drag => drag with relationship connection
				moveAnchors(dd.getAnchors(), dx, dy, sequence);
			}
		}

		selectionHandler.movedMaybeGroup(dx, dy);
	}

	private void moveAnchors(Collection<AnchorElement> anchors, int dx, int dy, int sequence) {
		for (AnchorElement ae : anchors) {
			ae.dispatch(dx, dy, sequence);
		}
	}

	private void moveRelatedDragHandlerAccordingToCircleHandle(Diagram dd, int dx, int dy) {
    if (dd instanceof CircleElement) {
      Diagram owner = dd.getOwnerComponent();
      if (owner instanceof DiagramDragHandler) {
        ((DiagramDragHandler) owner).onDrag(dd, dx, dy);
      }
    }
  }

  private boolean allowToMoveX(int x) {
		if (!delayX) {
			return true;
		}
		
		int diffx = x - delayStartX;

		if (Math.abs(diffx) > 5) {
			delayX = false;
			dehighlightLine(verticalLine);
		}
		return !delayX;
	}
	
	private boolean allowToMoveY(int y) {
		if (!delayY) {
			return true;
		}
		
		int diffy = y - delayStartY;
		if (Math.abs(diffy) > 5) {
			delayY = false;
			dehighlightLine(horizontalLine);
		}
		return !delayY;
	}

	private void drawLineHelpers(int dx, int dy, int dpx, int dpy) {
		if (oneSelected != null) {
			int centerX = oneSelected.getCenterX();
			int centerY = oneSelected.getCenterY();
			int vy1 = ScaleHelpers.scaleValue(0 - surface.getRootLayer().getTransformY(), surface.getScaleFactor());
			int vy2 = ScaleHelpers.scaleValue(windowHeight - surface.getRootLayer().getTransformY(), surface.getScaleFactor());
	    verticalLine.setShape(centerX, vy1, centerX, vy2);
	    
	    int hx1 = ScaleHelpers.scaleValue(0 - surface.getRootLayer().getTransformX(), surface.getScaleFactor());
	    int hx2 = ScaleHelpers.scaleValue(windowWidth - surface.getRootLayer().getTransformX(), surface.getScaleFactor());
	    horizontalLine.setShape(hx1, centerY, hx2, centerY);
	    
	    highlightSameCenters(oneSelected, centerX, centerY, dpx, dpy);
		}
	}

	private void highlightSameCenters(Diagram oneSelected, int centerX, int centerY, int dpx, int dpy) {
		restoreHighlightColor(prevXHigilights, oneSelected);
		restoreHighlightColor(prevYHighlights, oneSelected);
		prevXHigilights = highlightList(highlightCentersX.get(centerX), oneSelected);
		if (prevXHigilights != null && oneSelected != null) {
			prevXHigilights.remove(oneSelected);
		}
		if (!delayX && prevXHigilights != null && prevXHigilights.size() > 0) {
			delayX = true;
			delayStartX = dpx;
			highlightLine(verticalLine);
		}
		prevYHighlights = highlightList(highlightCentersY.get(centerY), oneSelected);
		if (prevYHighlights != null && oneSelected != null) {
			prevYHighlights.remove(oneSelected);
		}
		if (!delayY && prevYHighlights != null && prevYHighlights.size() > 0) {
			delayY = true;
			delayStartY = dpy;
			highlightLine(horizontalLine);
		}
	}

	private void highlightLine(ILine line) {
		// line.setStroke(AbstractDiagramItem.DEFAULT_SELECTION_COLOR);
		line.setVisibility(true);
	}

	private void dehighlightLine(ILine line) {
		// line.setStroke(LINE_HELPER_COLOR);
		line.setVisibility(false);
	}
	
	private void restorePreHighlightColors() {
    restoreHighlightColor(prevXHigilights, null);
    restoreHighlightColor(prevYHighlights, null);
	}
	
	private void restoreHighlightColor(List<Diagram> diagrams, Diagram oneSelected) {
		if (diagrams != null) {
			for (Diagram d : diagrams) {
				if (d != oneSelected && !d.isSelected()) {
					d.restoreHighlighColor(null);
				}
			}
		}
	}

	private List<Diagram> highlightList(List<Diagram> diagrams, Diagram oneSelected) {
		if (diagrams != null) {
	    for (Diagram d : diagrams) {
	    	if (d != oneSelected) {
	    		d.setHighlightColor(AbstractDiagramItem.DEFAULT_SELECTION_COLOR);
	    	}
	    }
		}
		return diagrams;
	}

	@Override
	public void onTouchStart(OrgEvent event, Diagram sender, MatrixPointJS point) {
  	if (surface.getEditorContext().isEditable()) {
			onMouseDown(event, sender, point, 0);
		}
	}
	
  @Override
  public void onTouchMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
  	if (surface.getEditorContext().isEditable()) {
	  	drag(point);
  	}
  }

  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
  	if (surface.getEditorContext().isEditable()) {
	  	onMouseUp(sender, point, 0);
	  }
  }

  private void startDragging(Diagram sender) {
		dragging = true;

    prevDX = 0;
    prevDY = 0;
		dragHandlersStart(sender);
		// showLineHelpers();

		// if (UiUtils.isSafari()) {
		// safari doesn't support cumulative transformations
			snapshotTransformations();
		// }

		_notifyDrag("start", getMoveShapeIDs());
	}
	
	private String[] getMoveShapeIDs() {
		Set<Diagram> shapes = moveItems();
		String[] result = new String[shapes.size()];
		
		int i = 0;
		for (Diagram d : shapes) {
			IDiagramItem item = d.getDiagramItem();
			if (item != null) {
				result[i++] = item.getClientId();
			}
		}

		return result;
	}

  /**
  * Need to take snapshot from all selected diagram transformations
  * since Safari doesn't support cumulative applyTransform. So need
  * to make cumulative transformations by 
  */
  private void snapshotTransformations() {
  	for (Diagram d : moveItems()) {
  		d.snapshotTransformations();
  	}
  }

	private void dragHandlersStart(Diagram sender) {

    logger.debug2("dragHandlersStart currentDiagram {}...", currentDiagram);
		for (DiagramDragHandler h : dragHandlers) {
			h.dragStart(currentDiagram);
		}
		
    if (sender instanceof CircleElement) {
      Diagram owner = sender.getOwnerComponent();
      if (owner instanceof DiagramDragHandler) {
        ((DiagramDragHandler) owner).dragStart(sender);
      }
    }

	}

	private Set<Diagram> moveItems() {
		if (!forceOn) {
			return selectionHandler.getSelectedItems();
		}
		return forcedItems;
	}

	@Override
	public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
		// logger.start("MouseDiagramDragHandler.onMouseUp SUM");
		// logger.start("MouseDiagramDragHandler.onMouseUp 1");

		initLineHelpersDiagrams();

		// logger.debugTime();
		// logger.start("MouseDiagramDragHandler.onMouseUp 2");

		if (currentDiagram != null) {
			// need to save last location to continue
			// on next time when object is moved
			if (dragging) {
				dragEnd(selectionHandler.getSelectedItems(), point);
//		  	complexElementHandler.showComplexElements(selectionHandler.getSelectedItems());
			}
		}

		// logger.debugTime();

		currentDiagram = null;
		mouseDown = false;
		dragging = false;

		// logger.debugTime();
	}

	public void addDragHandler(DiagramDragHandler handler) {
		dragHandlers.add(handler);
	}

	public void removeDragHandler(DiagramDragHandler handler) {
		dragHandlers.remove(handler);
	}

	public Set<DiagramDragHandler> getDragHandlers() {
		return dragHandlers;
	}

	public void onDoubleClick(Diagram sender, int x, int y) {
		// TODO Auto-generated method stub

	}

	public boolean bending() {
		return dragging;
	}

	public void reset() {
		dragHandlers.clear();
	}

	public void insertMoveElement(Relationship2 relationship) {
		this.insertMoveToSingle = relationship;

		// ST 2.11.2018: Fix relationship not inserted when created
		// from connection handlers.
		// Dragging treshold was not passed
		// and ghost relationship was created that lead
		// to crash when used from undo queue.
		// now force dragging for inserted relationship
		this.currentDiagram = this.insertMoveToSingle;
		this.dragging = true;
	}

	public void releaseForce(CircleElement currentHandle) {
		forceOn = false;
	}

	public void force(Diagram sender) {
		logger.debug("force {}", sender);
		forceOn = true;
		currentDiagram = sender;
		forcedItems.clear();
		forcedItems.add(sender);
	}

	public boolean isDragging() {
		return dragging;
	}
	
	private void dragEnd(Set<Diagram> selectedItems, MatrixPointJS point) {
		logger.debug("dragEnd...");
		// logger.start("MouseDiagramDragHandler.dragEnd SUM");
		// logger.start("MouseDiagramDragHandler.dragEnd 1");
		dragging = false;
//		MatrixPointJS dp = MatrixPointJS.createScaledTransform(gridUtils.dx(point.getScreenX()), gridUtils.dy(point.getScreenY()), surface.getScaleFactor());
		// prevX and prevY is the last full transition...
		
		hideLineHelpers();

		// logger.debugTime();
		// logger.start("MouseDiagramDragHandler.dragEnd 2");
		
		ReattachHelpers reattachHelpers = new ReattachHelpers();
		for (Diagram dd : selectedItems) {
			dd.saveLastTransform(prevDX, prevDY);

			for (DiagramDragHandler h : dragHandlers) {
				if (!h.isSelected()) {
					// some calculation problem in relation ship if item is selected
					// so disabled, anyway it is probably wrong to notify drag end
					// since relationship itself is selected and then circle element is not dragged alone
					// and relationship doesn't need to be handle that dragging.
					h.dragEnd(dd);
				}
			}

	    if (dd instanceof CircleElement) {
	      Diagram owner = dd.getOwnerComponent();
	      if (owner instanceof DiagramDragHandler) {
	        ((DiagramDragHandler) owner).dragEnd(dd);
	      }
	    }
			
			reattachHelpers.processDiagram(dd);
			AnchorElement.dragEndAnchors(dd);
		}

		// relationship can be dragged as a whole and force it to find anchor both ends.
		// E.g. to fixed anchor element and adjust it's position.
		// There are not usually many relationships dragged at once
		// and in that sence it is fast. Makes possible to drag sequence relationships
		// and fix position to a new location.
		boolean reanchorMovedRelationships = surface.getSelectionHandler().getSelectedItems().size() <= 1;
		reattachHelpers.reattachRelationships(reanchorMovedRelationships, false, false);

		if (insertMoveToSingle != null) {
			MouseDiagramEventHelpers.fireDiagramAddedEvent(insertMoveToSingle, surface, ActionType.DRAGGING);
			insertMoveToSingle = null;
		} else {
			MouseDiagramEventHelpers.fireDiagramsChangedEvenet(selectedItems, surface, ActionType.DRAGGING);
		}

		_notifyDrag("end", null);
	}

	private native void _notifyDrag(String type, String[] shapeIDs)/*-{
		$wnd.globalStreams.shapeDragStream.push({
			type: type,
			shape_ids: shapeIDs
		})
	}-*/;
		
// 	public void attach(Relationship2 relationship, AnchorElement anchorElement) {
// 		ConnectionMoveHandler moveHandler = moveHandlers.get(relationship);
// 		if (moveHandler == null) {
// 			// one connection move handler/relationship
// 			moveHandler = new ConnectionMoveHandler(relationship);
// 			moveHandlers.put(relationship, moveHandler);
// 		}
//     anchorElement.attach(moveHandler);
// 	}

// 	public void detach(Relationship2 relationship) {
// //		ConnectionMoveHandler handler = moveHandlers.get(relationship);
// //		relationship.getStartAnchor().getDiagram().getAnchors()
// //		if (handler != null) {
// //			handler.
// 			moveHandlers.remove(relationship);
// //		}
// 	}

	public void releaseDrag() {
		currentDiagram = null;
		mouseDown = false;
		dragging = false;
	}

}
