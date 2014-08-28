package net.sevenscales.editor.diagram;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers.ScaledAndTranslatedPoint;
import net.sevenscales.editor.content.utils.AbstractDiagramFactory;
import net.sevenscales.editor.content.utils.ShapeParser;
import net.sevenscales.editor.content.ClientIdHelpers;
import net.sevenscales.editor.content.BoardColorHelper;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.Dimension;
import net.sevenscales.domain.utils.DiagramItemHelpers;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEvent;
import net.sevenscales.editor.api.event.UndoEvent;
import net.sevenscales.editor.api.event.SelectionEvent;
import net.sevenscales.editor.api.event.SelectionEventHandler;
import net.sevenscales.editor.api.ot.CompensationModel;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.diagram.utils.RelationshipHelpers;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.GenericElement;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.AnchorUtils;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.IGraphics;
import net.sevenscales.domain.utils.SLogger;


class QuickConnectionHandler implements MouseDiagramHandler {
	private static SLogger logger = SLogger.createLogger(QuickConnectionHandler.class);

	static {
		SLogger.addFilter(QuickConnectionHandler.class);
	}

	private ISurfaceHandler surface;
	private DiagramSearch search;
	private Diagram previouslySelected;
	private List<CompensationModel> lastModel;
	private boolean notAddedFromLibrary = true;
	private int mouseUpKeys;
	private MatrixPointJS mouseUpPoint;

	public QuickConnectionHandler(ISurfaceHandler surface) {
		this.surface = surface;
		search = surface.createDiagramSearch();


		surface.getEditorContext().getEventBus().addHandler(SelectionEvent.TYPE, new SelectionEventHandler() {
			@Override
			public void onSelection(SelectionEvent event) {
				checkSelection();
			}
		});

		// surface.getEditorContext().getEventBus().addHandler(SwitchElementToEvent.TYPE, new SwitchElementToEventHandler() {
		// 	@Override
		// 	public void onSelection(SwitchElementToEvent event) {
		// 		switchElementTo(event.getElementType());
		// 	}
		// });

		// ESC key handler deletes created elements
		handleEscKey(this);
	}

	private void checkSelection() {
		if (!Tools.isQuickMode()) {
			return;
		}

		Set<Diagram> selected = surface.getSelectionHandler().getSelectedItems();
		logger.debug("checkSelection {}", selected);
		if (selected.size() == 1) {
			previouslySelected = selected.iterator().next();
			notAddedFromLibrary = !surface.isProxyDragAdding();
		} else {
			previouslySelected = null;
		}
	}

  private native void handleEscKey(QuickConnectionHandler me)/*-{
    $wnd.cancelStream.onValue(function(v) {
      me.@net.sevenscales.editor.diagram.QuickConnectionHandler::onEsc()();
    })
  }-*/;

  private void onEsc() {
  	previouslySelected = null;
  	cancelLastOperationIfLastQuickConnection();
  }

  private void cancelLastOperationIfLastQuickConnection() {
  	if (lastModel != null && lastModel == surface.getOTBuffer().topModel()) {
  		logger.debug("canselling cancelLastOperationIfLastQuickConnection...");
  		surface.getEditorContext().getEventBus().fireEvent(new UndoEvent());
  	}
		lastModel = null;
  }

	public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
		return false;
	}

	public void onMouseMove(Diagram sender, MatrixPointJS point) {
	}

	@Override
	public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
		logger.debug("onMouseUp keys {}", keys);

		this.mouseUpKeys = keys;
		this.mouseUpPoint = point;
		if (Tools.isQuickMode()) {
			cancelLastOperationIfLastQuickConnection();
		}
	}

	public boolean handleDoubleTap() {
		return maybeStartSuperFlow();
	}

	private boolean maybeStartSuperFlow() {
		boolean result = false;
		if (Tools.isQuickMode()) {
			// cancelLastOperationIfLastQuickConnection();
			// TODO how to check if just board is double clicked?
			// if (sender == null) {
				// let's check only board sent mouse up event;
				// since library drop would be reseted otherwise
				// anyway better to have some optimization
			if (mouseUpPoint != null) {
				boolean fromPreviousIfAny = this.mouseUpKeys == IGraphics.SHIFT;
				result = checkToCreateQuickConnection(mouseUpPoint.getScreenX(), mouseUpPoint.getScreenY(), fromPreviousIfAny);
			}
			// }
		}
		return result;
	}

	private Diagram findPrevious(Diagram d) {
		Diagram result = d;
    for (AnchorElement ae : d.getAnchors()) {
      if (ae.getRelationship() != null) {
      	Relationship2 rel = ae.getRelationship();
      	Diagram start = rel.getStartAnchor().getDiagram();
      	Diagram end = rel.getEndAnchor().getDiagram();

      	// take the end that is not current diagram
      	if (start != null && start != d) {
      		result = start;
      		break;
      	} else if (end != null) {
      		result = end;
      		break;
      	}
      }
    }
    return result;
	}

	private boolean checkToCreateQuickConnection(int screenX, int screenY, boolean fromPreviousIfAny) {
		boolean result = false;
		Set<Diagram> selected = surface.getSelectionHandler().getSelectedItems();
		if (notAddedFromLibrary &&
			  selected.size() == 0 && 
				previouslySelected != null && 
				exists(previouslySelected)) {
			ScaledAndTranslatedPoint stp = ScaleHelpers.scaleAndTranslateScreenpoint(screenX, screenY, surface);
			int x = stp.scaledAndTranslatedPoint.x;
			int y = stp.scaledAndTranslatedPoint.y;

			Diagram from = previouslySelected;
			if (fromPreviousIfAny) {
				from = findPrevious(previouslySelected);
			}
			result = createConnectedDiagram(from, previouslySelected.getDiagramItem(), x, y);
		}
		// makes sure that plain drag & drop doesn't create quick connection, but still
		// remembers what has been dragged and dropped
		notAddedFromLibrary = true;
		return result;
	}

	private boolean createConnectedDiagram(Diagram d, IDiagramItem prevSelectedItem, int x, int y) {
		boolean result = false;
		boolean doNotAllow = (d instanceof IChildElement) || (d instanceof CommentThreadElement) || d instanceof Relationship2 || d instanceof CircleElement;
		// could ask from diagram next in diagram
		// activity start could return activity, activity end could return note...
		// but more files would be modified and that is not certainly nice...
		if (!doNotAllow) {
			ReattachHelpers reattachHelpers = new ReattachHelpers();
			reattachHelpers.processDiagram(d);

	    int left = d.getLeft();
	    int top = d.getTop();
	    int width = d.getWidth();
	    int height = d.getHeight();

			// do not duplicate child or relationships, since rel e.g. cannot be connected
			// if child then e.g. create a note
			IDiagramItem item = createQuickNext(d, prevSelectedItem);			

			Dimension dimension = DiagramItemHelpers.parseDimension(item);
			if (dimension != null) {
				width = dimension.width;
				height = dimension.height;
			}

			// generate id and add elements normally
			// null to regenerate new client id
			item.setClientId(null);
			ClientIdHelpers.generateClientIdIfNotSet(item, 0, surface.getEditorContext().getGraphicalDocumentCache());

	    AbstractDiagramFactory factory = ShapeParser.factory(item);
	    Info shape = factory.parseShape(item, x - left - width / 2, y - top - height / 2);

	    // TODO quick handler should not work if not editable!
	    Diagram newelement = factory.parseDiagram(surface, shape, true, item, null);
	    if (newelement.getWidth() != width || newelement.getHeight() != height) {
	    	// recreate element in correct position
	    	width = newelement.getWidth();
	    	height = newelement.getHeight();
	    	item = item.copy();
		    shape = factory.parseShape(item, x - left - width / 2, y - top - height / 2);
	    	newelement.removeFromParent();
	    	newelement = factory.parseDiagram(surface, shape, true, item, null);
	    }

    	BoardColorHelper.applyThemeToDiagram(newelement, Theme.getColorScheme(Theme.ThemeName.PAPER), Theme.getCurrentColorScheme());

			reattachHelpers.processDiagram(newelement);

	    // create connection with closest connection
	    Relationship2 relationship = createRelationshipInBetween(d, newelement);
			reattachHelpers.processDiagram(relationship);

	    List<Diagram> newitems = new ArrayList<Diagram>();
	    newitems.add(newelement);
	    newitems.add(relationship);
	    surface.add(newitems, true, false);

	    surface.getSelectionHandler().select(newelement);
	    previouslySelected = newelement;

			reattachHelpers.reattachRelationshipsAndDraw();

			lastModel = surface.getOTBuffer().topModel();
		
	    // open editor for the created element
  		surface.getEditorContext().getEventBus().fireEvent(new ShowDiagramPropertyTextEditorEvent(newelement).setJustCreated(true));
			result = true;
		}
		return result;
	}

	private IDiagramItem createQuickNext(Diagram d, IDiagramItem prevSelectedItem) {
		IDiagramItem result = switchType(d);
		if (result == null) {
			result = d.getDiagramItem().copy();
			result.setType(prevSelectedItem.getType());
		}

		// for now let's use default values like colors
		// user most probably don't want to highligh created node in a same way
		return setDefaultValues(result);
	}

	private IDiagramItem switchType(Diagram d) {
		IDiagramItem result = null;
		switch (ElementType.getEnum(d.getDiagramItem().getType())) {
			case IMAGE:
			case NOTE:
			case SEQUENCE:
			case VERTICAL_PARTITION:
			case HORIZONTAL_PARTITION:
			case FREEHAND2: {
				result = d.getDiagramItem().copy();
				result = switchToNoteItem(d, result);
				break;
			}
			case MIND_CENTRAL: {
				result = d.getDiagramItem().copy();
				result = switchToTopicItem(d, result);
				break;
			}
			case FORK: 
			case CHOICE:
			case ACTIVITY_START: {
				result = d.getDiagramItem().copy();
				result = switchToActivityItem(d, result);
				break;
			}
			case TEXT_ITEM: {
				result = d.getDiagramItem().copy();
				result = switchToTextItem(d, result);
				break;
			}
			case ACTOR: {
				result = d.getDiagramItem().copy();
				result = switchToUseCase(d, result);
				break;
			}
		}
		return result;
	}

	private IDiagramItem switchToNoteItem(Diagram d, IDiagramItem copy) {
		copy.setType(ElementType.NOTE.getValue());
		copy.setShape(d.getLeft() + "," + d.getTop() + "," + "150,45");
		copy.setText("Note");
		return copy;
	}

	private IDiagramItem switchToTextItem(Diagram d, IDiagramItem copy) {
		copy.setType(ElementType.TEXT_ITEM.getValue());
		copy.setShape(d.getLeft() + "," + d.getTop() + "," + "102,34");
		copy.setText("Just text");
		return copy;
	}

  private IDiagramItem switchToTopicItem(Diagram d, IDiagramItem copy) {
		copy.setType(ElementType.ACTIVITY.getValue());
		copy.setShape(d.getLeft() + "," + d.getTop() + "," + "92,42");
		copy.setText("Main Topic");
		return copy;
  }

  private IDiagramItem switchToActivityItem(Diagram d, IDiagramItem copy) {
		copy.setType(ElementType.ACTIVITY.getValue());
		copy.setShape(d.getLeft() + "," + d.getTop() + "," + "92,42");
		copy.setText("My Activity");
		return copy;
  }

	private IDiagramItem switchToUseCase(Diagram d, IDiagramItem copy) {
		copy.setType(ElementType.ELLIPSE.getValue());
		copy.setShape(d.getLeft() + "," + d.getTop() + "," + "63,21");
		copy.setText("Use Case");
		return copy;
	}

	private Relationship2 createRelationshipInBetween(Diagram start, Diagram end) {
		AnchorUtils.ClosestSegment closestPoints = AnchorUtils.closestSegment(start.getLeft(), start.getTop(), start.getWidth(), start.getHeight(), end.getLeft(), end.getTop(), end.getWidth(), end.getHeight());
		IDiagramItem item = createRelationshipDTO(closestPoints, start, end);
    AbstractDiagramFactory factory = ShapeParser.factory(item);
    Info shape = factory.parseShape(item, 0, 0);
    return (Relationship2) factory.parseDiagram(surface, shape, true, item, null);
	}

	private IDiagramItem createRelationshipDTO(AnchorUtils.ClosestSegment closestSegment, Diagram start, Diagram end) {
		IDiagramItem result = new DiagramItemDTO();
		result.setType(ElementType.RELATIONSHIP.getValue());
		result.setText(RelationshipHelpers.relationship(start, surface.getEditorContext(), end));
		result.setShapeProperties(ShapeProperty.CURVED_ARROW.getValue() | 
															ShapeProperty.CLOSEST_PATH.getValue());
		setDefaultColors(result);
		result.setShape(closestSegment.start.x + "," + 
									closestSegment.start.y + "," + 
									closestSegment.end.x + "," +
									closestSegment.end.y);
		result.setCustomData(start.getDiagramItem().getClientId() + ":" + end.getDiagramItem().getClientId());
		return result;
	}

	private IDiagramItem setDefaultValues(IDiagramItem item) {
		setDefaultColors(item);
		item.setShapeProperties(ShapeProperty.clear(item.getShapeProperties(), ShapeProperty.DISABLE_SHAPE_AUTO_RESIZE.getValue()));
		item.setFontSize(null);
		return item;
	}

	private IDiagramItem setDefaultColors(IDiagramItem item) {
		item.setBackgroundColor(Theme.createDefaultBackgroundColor().toRgbWithOpacity() + ":" + Theme.createDefaultBorderColor().toRgbWithOpacity());
		item.setTextColor(Theme.createDefaultTextColor().toRgbWithOpacity());
		return item;
	}

	public void onMouseLeave(Diagram sender, MatrixPointJS point) {

	}

	public void onMouseEnter(Diagram sender, MatrixPointJS point) {

	}
	
	public void onTouchStart(Diagram sender, MatrixPointJS point) {

	}

	public void onTouchMove(Diagram sender, MatrixPointJS point) {

	}

	public void onTouchEnd(Diagram sender, MatrixPointJS point) {

	}

	private boolean exists(Diagram d) {
 		return search.findByClientId(d.getDiagramItem().getClientId()) != null;
	}

}

