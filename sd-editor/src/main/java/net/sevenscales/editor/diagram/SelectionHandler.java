package net.sevenscales.editor.diagram;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.KeyboardListener;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.utils.Error;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.BoardDimensions;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.ReactAPI;
import net.sevenscales.editor.api.auth.AuthHelpers;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEvent;
import net.sevenscales.editor.api.event.DeleteSelectedEvent;
import net.sevenscales.editor.api.event.DeleteSelectedEventHandler;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEvent;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEventHandler;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEventHandler;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.event.SelectionEvent;
import net.sevenscales.editor.api.event.SelectionMouseUpEvent;
import net.sevenscales.editor.api.event.UnselectAllEvent;
import net.sevenscales.editor.api.event.PointersDownEvent;
import net.sevenscales.editor.api.event.PointersDownEventHandler;
import net.sevenscales.editor.api.event.PointersUpEventHandler;
import net.sevenscales.editor.api.event.PointersUpEvent;
import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.editor.content.ui.UIKeyHelpers;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.IGraphics;
import net.sevenscales.editor.gfx.domain.IRelationship;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;
import net.sevenscales.editor.silver.KeyCodeMap;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;


public class SelectionHandler implements MouseDiagramHandler, KeyEventListener {
  private static final SLogger logger = SLogger.createLogger(SelectionHandler.class);
  static {
    SLogger.addFilter(SelectionHandler.class);
  }
	
//	private Diagram mouseDownSender = null;
	// private List<Diagram> diagrams;
	private SelectionHandlerCollection selectionHandlers;
	private ISurfaceHandler surface;
	private Set<Diagram> tmpSelectedItems;
	private Set<DiagramDragHandler> dragHandlers;
	private boolean shiftOn;
	private Diagram currentHandler;
  private int dispachSequence;
	private Diagram lastMultimodeSelectedDiagram;
  private boolean freehandModeOn;
  private Set<Diagram> tobeRemovedInCycle;
  private boolean potentialClearSelection;
  private MouseState mouseState;
  private List<GroupSelection> groupSelections;
  private boolean preventSelect;
	
	public SelectionHandler(ISurfaceHandler surface, Set<DiagramDragHandler> dragHandlers, MouseState mouseState) {
		this.surface = surface;
		// this.diagrams = diagrams;
		this.dragHandlers = dragHandlers;
    this.mouseState = mouseState;
		this.selectionHandlers = new SelectionHandlerCollection();
		tmpSelectedItems = new HashSet<Diagram>();
    tobeRemovedInCycle = new HashSet<Diagram>();
    groupSelections = new ArrayList<GroupSelection>();

    if (!surface.isLibrary()) {
      if (surface.isDeleteSupported()) {
        surface.getEditorContext().getEventBus().addHandler(DeleteSelectedEvent.TYPE, new DeleteSelectedEventHandler() {
          @Override
          public void onSelection(DeleteSelectedEvent event) {
            removeSelected();
          }
        });
      }
      
      surface.getEditorContext().getEventBus().addHandler(EditDiagramPropertiesStartedEvent.TYPE, new EditDiagramPropertiesStartedEventHandler() {
        @Override
        public void on(EditDiagramPropertiesStartedEvent event) {
          // long press will hide mouse up and current handler is not nulled.
          // Need to listen property editor open and use that to set currentHandler to null.
          currentHandler = null;
        }
      });

      surface.getEditorContext().getEventBus().addHandler(FreehandModeChangedEvent.TYPE, new FreehandModeChangedEventHandler() {
        public void on(FreehandModeChangedEvent event) {
          freehandModeOn = event.isEnabled();
          if (freehandModeOn) {
            unselectAll();
          }
        }
      });

      surface.getEditorContext().getEventBus().addHandler(PointersDownEvent.TYPE, new PointersDownEventHandler() {
        @Override
        public void on(PointersDownEvent event) {
          if (event.getPointersDownCount() == 2) {
            // on iPad it is disturbing when selecting and dragging
            logger.debug("pointers down");
            unselectAll();
            preventSelect = true;
          }
        }
      });
      surface.getEditorContext().getEventBus().addHandler(PointersUpEvent.TYPE, new PointersUpEventHandler() {
        @Override
        public void on(PointersUpEvent event) {
          preventSelect = false;
          logger.debug("pointers up");
        }
      });

      Event.addNativePreviewHandler(new NativePreviewHandler() {
        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
          handleBackspaceDelete(event);
        }
      });

      handleStreams(this);
    }
		
	}

  private native void handleStreams(SelectionHandler me)/*-{
    $wnd.cancelStream.onValue(function(v) {
      me.@net.sevenscales.editor.diagram.SelectionHandler::onEsc()();
    })
  }-*/;

  private void onEsc() {
    unselectAll();
  }

  private void handleBackspaceDelete(NativePreviewEvent event) {
    NativeEvent ne = event.getNativeEvent();
    if (event.getTypeInt() == Event.ONKEYDOWN && UIKeyHelpers.noMetaKeys(ne) && !surface.getEditorContext().isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN)) {
      if (ne.getKeyCode() == KeyCodes.KEY_BACKSPACE && UIKeyHelpers.allMenusAreClosed() && confluenceCheck()) {
        event.getNativeEvent().preventDefault();
        removeSelected();
      }
    }
  }

  private boolean confluenceCheck() {
    EditorContext editorContext = surface.getEditorContext();
    if (editorContext.isTrue(EditorProperty.CONFLUENCE_MODE) && !editorContext.isTrue(EditorProperty.SKETCHBOARD_OPEN)) {
      // backspace cannot be prevented when confluence sketchboard is closed
      return false;
    }
    return true;
  }
		
////////////////////////////////////
  @Override
	public boolean onMouseDown(OrgEvent event, Diagram sender, MatrixPointJS point, int keys) {
    if (mouseState.isResizing()) {
      return false;
    }

		// logger.debug("onMouseDown sender={}, currentHandler={}...", sender, currentHandler);
    potentialClearSelection = false;
		if (sender == null && currentHandler == null) {
			// if null it is canvas mouse down event
			// but prevent handling bubbled event which is already sent by 
			// real target object
			// handleCanvasMouseDown();
      potentialClearSelection = true;
			return true;
//			System.out.println("canvas selected:" + sender+" x:"+x+" y:"+y);
		} else if (sender != null) {
		  // pass shift parameter
		  shiftOn = keys == IGraphics.SHIFT ? true : false;
		  select(sender);
		  shiftOn = false;
		}
		return false;
	}

  @Override
  public void onMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseLeave(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

  @Override
	public void onMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
    if (!(keys == IGraphics.SHIFT) && potentialClearSelection && !mouseState.isMovingBackground() && !mouseState.isDragging()) {
      // plain canvas click
      handleCanvasMouseDown();
    }

    if (sender != null && mouseState.isLassoOn() && !mouseState.isLassoing()) {
      // lasso selection is on, mouse up just selects more
      shiftOn = true;
      select(sender);
      shiftOn = false;
    }

		// logger.debug("onMouseUp sender={}...", sender);
		if (sender != null && 
				getSelectedItems().size() > 0 && 
				!surface.getMouseDiagramManager().getDragHandler().isDragging()) {
			// allow only to show if really clicked an element => then other rules
			// should not show context menu if this was a drag and select
			Set<Diagram> selected = getSelectedItems();
			Diagram[] diagrams = new Diagram[1];
      Diagram lastSelected = sender;
      if (!selected.contains(lastSelected)) {
        // lastSelected is not one of those that was original clicked on mouse down
        // so select random from selected, this should be case of only one selected...
        // e.g. relationship is selected and after selection circle becomes visible and gets mouse up
        // if cursor is on top of a circle
        lastSelected = selected.iterator().next();
      }

			surface.getEditorContext().getEventBus().fireEvent(new SelectionMouseUpEvent(selected.toArray(diagrams), lastSelected));
      fireSelectionEnd();
		}
		currentHandler = null;
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
	
////////////////////////////////////////////////////////////

	private void handleCanvasMouseDown() {
	  unselectAll();
	}

	public void addDiagramSelectionHandler(DiagramSelectionHandler handler) {
		selectionHandlers.add(handler);
	}

  public void remove(Diagram diagram, boolean withoutConnections) {
    // clear to be removed so hooks are valid in this cycle
    clearToBeRemovedCycle();

    Set<Diagram> removed = new HashSet<Diagram>();
    _remove(diagram, removed, withoutConnections);

    handleAdditionalRemovals(removed);
    fireDeletedOrModifyEvent(removed);
  }

  /**
  * Removes selected and allows parent child removal hooks
  * in the middle of the loop. Parents can be added
  * to be deleted after a child element. Possibility to make undo/redo
  * work in correct order.
  */
	public void removeSelected() {
    try {
      // ST 29.10.2018: Catch this not to go as GWT unhandled exception
      // where it is more difficult to get stack trace
      if (surface.isLibrary() || !surface.getEditorContext().isEditable()) {
        // HACK: not allowed to remove anything from library
        // not allowed to remove anything from read only board
        return;
      }

      Diagram[] items = new Diagram[]{};
      items = surface.getDiagrams().toArray(items);
      // clear to be removed so hooks are valid in this cycle
      clearToBeRemovedCycle();
      Set<Diagram> removed = new HashSet<Diagram>();
      for (Diagram d : items) {
        // Diagram d = diagrams.get(i);
        logger.debug("removeSelected: item {}", d);
        if (d.isSelected()) {
          _remove(d, removed, false);
        }
      }
      logger.debug("removeSelected: removed {}", removed);

      handleAdditionalRemovals(removed);
      fireDeletedOrModifyEvent(removed);
      removeGroups();
    } catch(Exception e) {
      Error.reload("removeSelected", e);
    }
	}

  private void fireDeletedOrModifyEvent(Set<Diagram> removed) {
    Set<Diagram> confirmedRemove = new HashSet<Diagram>();
    Set<Diagram> convertedToModify = new HashSet<Diagram>();
    for (Diagram d : removed) {
      if (d.changeRemoveToModify()) {
        convertedToModify.add(d);
        if (d.getOwnerComponent() instanceof IRelationship) {
          // need to add owner component children as well
          for (IChildElement child : ((IRelationship) d.getOwnerComponent()).getChildren()) {
            convertedToModify.add(child.asDiagram());
          }
        }
      } else {
        confirmedRemove.add(d);
      }
    }

    if (confirmedRemove.size() > 0) {
      surface.getEditorContext().getEventBus().fireEvent(new BoardRemoveDiagramsEvent(confirmedRemove));
      notifyRemove();
    }
    if (convertedToModify.size() > 0) {
      surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(convertedToModify));
    }
  }

  private native void notifyRemove()/*-{
    $wnd.globalStreams.deleteShapeStream.push()
  }-*/;

  /**
  * This can be used to hook parent deletion after
  * a child element removal.
  */
  public void addToBeRemovedCycle(Diagram diagram) {
    tobeRemovedInCycle.add(diagram);
  }

  private void handleAdditionalRemovals(Set<Diagram> removed) {
    for (Diagram remove : tobeRemovedInCycle) {
      removed.add(remove);
      dragHandlers.remove(remove);
      remove.removeFromParent();
    }
    clearToBeRemovedCycle();
  }

  private void clearToBeRemovedCycle() {
    tobeRemovedInCycle.clear();
  }

  public void remove(Diagram[] forRemoval) {
    // clear to be removed so hooks are valid in this cycle
    clearToBeRemovedCycle();

    Set<Diagram> removed = new HashSet<Diagram>();
    for (Diagram remove : forRemoval) {
      _remove(remove, removed, false);
    }

    handleAdditionalRemovals(removed);
    fireDeletedOrModifyEvent(removed);
  }

  private void _remove(Diagram diagram, Set<Diagram> removed, boolean withoutConnections) {

    // first remove children if any since e.g. comment thread
    // cannot be deleted straight, but through 0 child automatically.
    Diagram removeItem = diagram.getOwnerComponent(ActionType.DELETE);
    removeChildElements(removeItem, removed);
    if (AuthHelpers.allowedToDelete(removeItem)) {
      removed.add(removeItem);
      if (withoutConnections) {
        removeItem.removeFromParentWithoutConnections();
      } else {
        removeItem.removeFromParent();
      }
      dragHandlers.remove(removeItem);
    }
  }

  private void removeChildElements(Diagram removeItem, Set<Diagram> removed) {
    List<? extends Diagram> childElements = removeItem.getChildElements();
    if (childElements != null) {
      for (int i = childElements.size() - 1; i >= 0; --i) {
        Diagram d = childElements.get(i);
        _remove(d, removed, false);
      }
    }
  }

//  @Override
  public boolean onKeyEventDown(int keyCode, boolean shift, boolean ctrl) {
    int multiplier = !ctrl ? 5 : 1; // if ctrl is pressed goes to finest granularity
    multiplier = shift ? multiplier * 3 : multiplier; // if shift is pressed speed will be increased
    // TODO: components should do boundary checking so that those cannot be moved outside surface
    
    this.shiftOn = keyCode == 4 ? true : false;
    
    switch (keyCode) {
      // Delete key doesn't check
      // case KeyCodeMap.DELETE: { // delete key 
      //   removeSelected();
      //   return true;
      // }
      case KeyCodeMap.LEFT: { // TODO: test constant from gwt, down arrow
        moveSelected(-1 * multiplier, 0);
        return true;
      }
      case KeyCodeMap.UP: { // TODO: test constant from gwt, down arrow
        moveSelected(0, -1  * multiplier);
        return true;
      }
      case KeyCodeMap.RIGHT: { // TODO: test constant from gwt, down arrow
        moveSelected(1 * multiplier, 0);
        return true;
      }
      case KeyCodeMap.DOWN: { // TODO: test constant from gwt, down arrow
        moveSelected(0, 1 * multiplier);
        return true;
      }
//      case KeyCodeMap.A: { // TODO: a
//        if (ctrl) {
//          selectAll();
//        }
//        return true;
//      }
        
    }
    return false;
  }

//@Override
  public boolean onKeyEventUp(int keyCode, boolean shift, boolean ctrl) {
    this.shiftOn = false;
//    System.out.println("up shiftOn: " + shiftOn);    
    return false;
  }
  
//  public void onKeyDown(Event event) {
//  }
  
//  public void onKeyUp(Event event) {
//	// TODO Auto-generated method stub
//	
//  }
  
//  public void onKeyPress(Event event) {
//  }
  
  public void onKeyDown(KeyDownEvent event) {
    // TODO Auto-generated method stub
  	boolean ctrl = DOM.eventGetCtrlKey(Event.as(event.getNativeEvent()));
  	boolean shift = DOM.eventGetShiftKey(Event.as(event.getNativeEvent()));
      int multiplier = !ctrl ? 5 : 1; // if ctrl is pressed goes to finest granularity
      multiplier = shift ? multiplier * 3 : multiplier; // if shift is pressed speed will be increased
      // TODO: components should do boundary checking so that those cannot be moved outside surface
      
      int keyCode = DOM.eventGetKeyCode(Event.as(event.getNativeEvent()));
      this.shiftOn = keyCode == KeyboardListener.KEY_SHIFT ? true : false;
//  			    System.out.println("down shiftOn: " + shiftOn);
      
      // safari key codes has been added, because
      // on left arrow key down event is not received :(
      switch (keyCode) {
//        case 63272: // safari delete on key press
        case KeyboardListener.KEY_DELETE: { // delete key 
          removeSelected();
          break;
        }
//  	  case 63234: // left key on safari
        case KeyboardListener.KEY_LEFT: { // TODO: test constant from gwt, down arrow
          moveSelected(-1 * multiplier, 0);
          break;
        }
//        case 63232: // safari up on key press
        case KeyboardListener.KEY_UP: { // TODO: test constant from gwt, down arrow
          moveSelected(0, -1  * multiplier);
          break;
        }
//        case 63235: // safari right on key press
        case KeyboardListener.KEY_RIGHT: { // TODO: test constant from gwt, down arrow
          moveSelected(1 * multiplier, 0);
          break;
        }
//        case 63233: // safari down on key press
        case KeyboardListener.KEY_DOWN: { // TODO: test constant from gwt, down arrow
          moveSelected(0, 1 * multiplier);
          break;
        }
//        case 'a': { // TODO: selecting all is not supported
//          if (ctrl) {
//            selectAll();
//          }
//          break;
//        }
      }
  }
  public void onKeyPress(KeyPressEvent event) {
    // TODO Auto-generated method stub
  }
  public void onKeyUp(KeyUpEvent event) {
    // TODO Auto-generated method stub
  }

  public void moveSelected(int dx, int dy) {
  	MatrixPointJS dp = MatrixPointJS.createScaledPoint(dx, dy, surface.getScaleFactor());
    for (Diagram d : surface.getDiagrams()) {
      if (d.isSelected()) {
        d.applyTransform(dp.getDX(), dp.getDY());
        moveDiagram(d, dx, dy);
      }
    }
  }
  
  public void select(Diagram sender) {
    logger.debug("select");

    if (preventSelect) {
      return;
    }

    if (freehandModeOn) {
      // if freehand mode is on, selection is not supported!
      return;
    }
    if (surface.isLibrary()) {
      // library doesn't support selection
      return;
    }

    // logger.start("SelectionHandler.select SUM");
    // logger.start("SelectionHandler.select 1");

  	this.currentHandler = sender;
    // if (sender.isSelected()) {
    //   return;
    // }

    // logger.debugTime();
    // logger.start("SelectionHandler.select 2");

//  System.out.println("selected:"+sender);
  // store that diagram has handled event
//  mouseDownSender = sender;
  
  // select sender
  boolean selected = sender.isSelected();

  // logger.debugTime();
  // logger.start("SelectionHandler.select 3");

  // logger.debugTime();
  // logger.start("SelectionHandler.select 6");
  
  if (!shiftOn && !selected) {
    // remove other selected items, because shift is not pressed and current element is not selected        
    for (int i = 0; i < surface.getDiagrams().size(); ++i) {
      Diagram d = (Diagram) surface.getDiagrams().get(i);
      // if d equals to sender it has been just selected 
      // if sender differs, need to check that owner component
      // is not d
      if (d != sender && d.isSelected()) {
        unselectGroup(d);
//        selectedItems.remove(d);
      }
    }
  }

  if (selected && shiftOn) {
    unselectGroup(sender);
  } else {
    selectGroup(sender);
  }

  // logger.debugTime();
  // logger.start("SelectionHandler.select 4");

//  selectedItems.add(sender);
  
  List<Diagram> notifyDiagrams = new ArrayList<Diagram>();
  Diagram notifyDiagram = sender.getOwnerComponent(ActionType.SELECT);
  notifyDiagrams.add(notifyDiagram);
  selectionHandlers.fireSelection(notifyDiagrams);

  // logger.debugTime();
  // logger.start("SelectionHandler.select 5");

  surface.getEditorContext().getEventBus().fireEvent(new SelectionEvent());


  // logger.debugTime();
  // logger.debugTime();
  
  // select again, to have correct colors in relationship circle elements :)
//  sender.select();
	}

  public void selectGroup(Diagram diagram) {
    if (diagram.getDiagramItem() != null) {
      String group = diagram.getDiagramItem().getGroup();
      if (group != null && !"".equals(group)) {
        List<Diagram> groupShapes = new ArrayList<Diagram>();
        for (Diagram d : surface.getDiagrams()) {
          if (d.getDiagramItem() != null && group.equals(d.getDiagramItem().getGroup())) {
            d.select();
            groupShapes.add(d);
          }
        }
        showGroupOutline(groupShapes);
      } else {
        diagram.select();
      }
    }
  }

  private void showGroupOutline(List<Diagram> groupShapes) {
    groupSelections.add(new GroupSelection(surface, groupShapes));
  }

  public void movedMaybeGroup(int dx, int dy) {
    for (GroupSelection gs : groupSelections) {
      gs.setTransform(dx, dy);
    }
  }

  public void unselectGroup(Diagram diagram) {
    removeGroups();

    String group = diagram.getDiagramItem().getGroup();
    if (group != null && !"".equals(group)) {
      for (Diagram d : surface.getDiagrams()) {
        if (d.getDiagramItem() != null && group.equals(d.getDiagramItem().getGroup())) {
          d.unselect();
        }
      }
    } else {
      diagram.unselect();
    }
  }

  private void removeGroups() {
    for (GroupSelection gs : groupSelections) {
      gs.remove();
    }
    groupSelections.clear();
  }

	public void select(List<Diagram> toSelectDiagrams) {
		unselectAll();
	  List<Diagram> notifyDiagrams = new ArrayList<Diagram>();
		for (Diagram d : toSelectDiagrams) {
			d.select();
			hideRelationshipHandles(d);
			notifyDiagrams.add(d.getOwnerComponent());
		}
		
	  selectionHandlers.fireSelection(notifyDiagrams);
	  surface.getEditorContext().getEventBus().fireEvent(new SelectionEvent());
  }

	/**
	 * HACK: Used when selecting multiple items. Relationship should not show
	 * handles at that time.
	 * @param d
	 */
  private void hideRelationshipHandles(Diagram d) {
		if (d instanceof Relationship2) {
			((Relationship2) d).hideAllHandles();
		}
	}

	/**
   * Used when lassoing selected items.
   */
  public void selectInMultimode(Diagram diagram) {
    if (diagram instanceof CircleElement) {
      // handles cannot be selected in multi selection mode
      return;
    }

    lastMultimodeSelectedDiagram = diagram;

  	diagram.select();
  	hideRelationshipHandles(diagram);
  	List<Diagram> diagrams = new ArrayList<Diagram>();
  	diagrams.add(diagram);
    selectionHandlers.fireSelection(diagrams);
    surface.getEditorContext().getEventBus().fireEvent(new SelectionEvent());
  }

  public void fireSelectionEnd() {
    JsArrayString items = JavaScriptObject.createArray().cast();

    for (Diagram d : getSelectedItems()) {
      if (d.getDiagramItem() != null &&
        !"".equals(d.getDiagramItem().getClientId())) {
          items.push(d.getDiagramItem().getClientId());
        }
    }

    GlobalState.notifySelected(items, "select_end");
  }
  
  public Diagram getLastMultimodeSelectedDiagram() {
		return lastMultimodeSelectedDiagram;
	}
  public void setLastMultimodeSelectedDiagram(Diagram lastMultimodeSelectedDiagram) {
		this.lastMultimodeSelectedDiagram = lastMultimodeSelectedDiagram;
	}
  
  public boolean isMultiMode() {
    return lastMultimodeSelectedDiagram != null;
  }
  
  public void unselect(Diagram diagram) {
  	diagram.unselect();
  	
  	if (getSelectedItems().size() <= 0) {
  	  logger.start("fireUnselectAll");
  		selectionHandlers.fireUnselectAll();
  		logger.debugTime();
  	}
  }

//  private boolean isSelected(Diagram sender) {
//    // TODO: Diagram should support at some point selected property
//    // and have call back to selection handler interface to unselect/select
//    // directly through a diagram
//    for (Diagram d : selectedItems) {
//      if (d == sender) {
//        return true;
//      }
//    }
//    return false;
//  }

  public void selectAll() {
    for (Diagram d : surface.getDiagrams()) {
      // select diagram
      d.select();
//      selectedItems.add(d);
    }
    
    selectionHandlers.fireSelection(surface.getDiagrams());
    surface.getEditorContext().getEventBus().fireEvent(new SelectionEvent());
  }
  
  public Set<Diagram> getSelectedItems() {
    tmpSelectedItems.clear();
    for (Diagram d : surface.getDiagrams()) {
      if (d.isSelected()) {
        tmpSelectedItems.add(d);
      }
    }
    return tmpSelectedItems;
  }

  public List<String> getSeletedShapeIdsJavaVersion() {
    List<String> result = new ArrayList<String>();

    for (Diagram diagram : getSelectedItems()) {
      IDiagramItemRO di = diagram.getDiagramItem();
      if (di != null && di.getClientId() != null) {
        result.add(di.getClientId());
      }
    }
    return result;
  }

  public JsArrayString getSeletedShapeIds() {
    return BoardDocumentHelpers.getDiagramClientIdsOrdered(
      getSelectedItems()
    );
  }

  public void selectShapes(JsArrayString shapeIds) {
    DiagramSearch search = surface.createDiagramSearch();
    for (int i = 0; i < shapeIds.length(); ++i) {
      String shapeId = shapeIds.get(i);
      Diagram d = search.findByClientId(shapeId);
      if (d != null) {
        d.select();
      }
    }
  }

  public void focusShapes(
    JsArrayString shapeIds,
    boolean select
  ) {
    focusShapes(
      shapeIds,
      select,
      false
    );
  }

  public void focusShapes(
    JsArrayString shapeIds,
    boolean select,
    boolean center
  ) {
    if (select) {
      unselectAll();
    }

    DiagramSearch search = surface.createDiagramSearch();
    List<Diagram> selected = new ArrayList<Diagram>();
    for (int i = 0; i < shapeIds.length(); ++i) {
      String shapeId = shapeIds.get(i);
      Diagram d = search.findByClientId(shapeId);
      if (d != null) {

        if (select) {
          d.select();
        }

        selected.add(d);
      }
    }

    if (selected.size() == 0) {
      // don't do anything if no shapes are found.
      return;
    }

    BoardDimensions.resolveDimensions(selected);

    int left = BoardDimensions.getLeftmost();
    int top = BoardDimensions.getTopmost();
    // int right = left + BoardDimensions.getWidth();
    // int bottom = top + BoardDimensions.getHeight();

    int width = BoardDimensions.getWidth();
    int height = BoardDimensions.getHeight();

    // from 0,0
    int centerX = width / 2;
    int centerY = height / 2;

    // from 0,0
    int clientWidth = ReactAPI.getCanvasWidth();
    int cliehtHeight = ReactAPI.getCanvasHeight();
    int clientCenterX = clientWidth / 2;
    int clientCenterY = cliehtHeight / 2;

    int centerDiffX = clientCenterX - centerX;
    int centerDiffY = clientCenterY - centerY;

    // NOTE this logic should not really be here, or 
    // it could be a param to method or a separate method
    // that can be called from comments part

    // needed to center shapes
    int commentsSectionWidth = clientWidth;
    if (select && !center) {
      commentsSectionWidth = 460;
    }

    int visibleSpace = clientWidth - commentsSectionWidth;
    int extrax = 0;

    if (width < visibleSpace) {
      // in case selection can fit to visible area after comments
      // move to center of visible space
      extrax = visibleSpace / 2 - width / 2;
    }

    resetScale();
    int stx = surface.getRootLayer().getTransformX();
    int sty = surface.getRootLayer().getTransformY();
    int tx = extrax + (int) ((-left + centerDiffX) * surface.getScaleFactor()) + ReactAPI.getLeftPanelWidth();
    int ty = (int) ((-top + centerDiffY - 40) * surface.getScaleFactor());

    animate(surface.getRootLayer().getContainer(), stx, sty, tx, ty, this);
  }

  private native void resetScale()/*-{
    $wnd.globalStreams.scaleResetStream.push()
  }-*/;

  private native void animate(com.google.gwt.core.client.JavaScriptObject el, int stx, int sty, int tx, int ty, SelectionHandler me)/*-{
    $wnd.globalStreams.animateStream.push('start')
    $wnd.animate({
      el: el.rawNode,
      translateX: [stx, tx],
      translateY: [sty, ty],
      easing: 'easeOutQuad',
      begin: function() {
      },
      complete: function() {
        el.rawNode.style.transform = ''
        me.@net.sevenscales.editor.diagram.SelectionHandler::setRootTransform(II)(tx,ty)
        $wnd.globalStreams.animateStream.push('end')
      }
    })

  }-*/;

  private void setRootTransform(int tx, int ty) {
    surface.setTransform(tx, ty);
  }

  /**
  * Returns null if none or more than one is selected.
  * Otherwise selected diagram.
  */
  public Diagram getOnlyOneSelected() {
    Diagram result = null;
    for (Diagram d : surface.getDiagrams()) {
      if (d.isSelected()) {
        if (result != null) {
          // more than one is selected
          return null;
        } else {
          result = d;
        }
      }
    }
    return result;
  }

  public void unselectAll() {
    // Debug.log("SelectionHandler unselectAll...");
    // Debug.callstack("SelectionHandler unselectAll...");

    logger.start("unselectAll");
    for (int i = 0; i < surface.getDiagrams().size(); ++i) {
      // nobody handled, so unselect all
      Diagram d = (Diagram) surface.getDiagrams().get(i);
      if (d.isSelected()) {
        d.unselect();
      }
//      selectedItems.remove(d);
    }
    logger.debugTime();

    removeGroups();
    
    logger.start("unselectAll B");
    selectionHandlers.fireUnselectAll();
    surface.getEditorContext().getEventBus().fireEvent(new UnselectAllEvent());
    logger.debugTime();
  }

//  public void diagramRemoved(Diagram diagram) {
//    selectedItems.remove(diagram);
//  }
  
  public void selectionOn(boolean selectionOn) {
    shiftOn = selectionOn;
  }

	public void moveItems(Set<Diagram> diagrams, int dx, int dy) {
		for (Diagram d : diagrams) {
			moveDiagram(d, dx, dy);
		}
	}

	private void moveDiagram(Diagram d, int dx, int dy) {
//		MatrixPointJS point = MatrixPointJS.createScaledTransform(dx, dy, surface.getScaleFactor());
    // handle anchor move events from current diagram
    ++this.dispachSequence;
    Collection<AnchorElement> anchors = d.getAnchors();
    for (AnchorElement ae : anchors) {
      ae.dispatch(dx, dy, dispachSequence);
    }
    
    for (DiagramDragHandler h : dragHandlers) {
      h.dragStart(d);
    }
    
    for (DiagramDragHandler h : dragHandlers) {
      h.onDrag(d, dx, dy);
    }
    
    d.saveLastTransform(dx, dy);
    for (DiagramDragHandler h : dragHandlers) {
      h.dragEnd(d);
    }
	}

  public int size() {
    int result = 0;
    for (Diagram d : surface.getDiagrams()) {
      if (d.isSelected()) {
        ++result;
      }
    }
    return result;
  }

  public void reset() {
    tmpSelectedItems.clear();
    dragHandlers.clear();
  }

}
