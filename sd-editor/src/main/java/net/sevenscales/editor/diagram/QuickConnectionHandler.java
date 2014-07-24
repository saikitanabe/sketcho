package net.sevenscales.editor.diagram;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers.ScaledAndTranslatedPoint;
import net.sevenscales.editor.content.utils.AbstractDiagramFactory;
import net.sevenscales.editor.content.utils.ShapeParser;
import net.sevenscales.editor.content.ClientIdHelpers;
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
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.GenericElement;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.AnchorUtils;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.domain.utils.SLogger;


class QuickConnectionHandler implements MouseDiagramHandler {
	private static SLogger logger = SLogger.createLogger(QuickConnectionHandler.class);

	static {
		SLogger.addFilter(QuickConnectionHandler.class);
	}

	private ISurfaceHandler surface;
	private DiagramSearch search;
	private Diagram previouslySelected;
	private CompensationModel lastModel;
	private boolean notAddedFromLibrary = true;

	public QuickConnectionHandler(ISurfaceHandler surface) {
		this.surface = surface;
		search = surface.createDiagramSearch();


		surface.getEditorContext().getEventBus().addHandler(SelectionEvent.TYPE, new SelectionEventHandler() {
			@Override
			public void onSelection(SelectionEvent event) {
				checkSelection();
			}
		});

		// ESC key handler deletes created elements
		handleEscKey(this);
	}

	private void checkSelection() {
		if (!Tools.isQuickMode()) {
			return;
		}

		Set<Diagram> selected = surface.getSelectionHandler().getSelectedItems();
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

	public void onMouseUp(Diagram sender, MatrixPointJS point) {
		if (Tools.isQuickMode()) {
		cancelLastOperationIfLastQuickConnection();
			if (sender == null) {
				// let's check only board sent mouse up event;
				// since library drop would be reseted otherwise
				// anyway better to have some optimization
				checkToCreateQuickConnection(point.getScreenX(), point.getScreenY());
			}
		}
	}

	private void checkToCreateQuickConnection(int screenX, int screenY) {
		Set<Diagram> selected = surface.getSelectionHandler().getSelectedItems();
		if (notAddedFromLibrary &&
			  selected.size() == 0 && 
				previouslySelected != null && 
				exists(previouslySelected)) {
			ScaledAndTranslatedPoint stp = ScaleHelpers.scaleAndTranslateScreenpoint(screenX, screenY, surface);
			int x = stp.scaledAndTranslatedPoint.x;
			int y = stp.scaledAndTranslatedPoint.y;

			createConnectedDiagram(previouslySelected, x, y);
		}
		// makes sure that plain drag & drop doesn't create quick connection, but still
		// remembers what has been dragged and dropped
		notAddedFromLibrary = true;
	}

	private void createConnectedDiagram(Diagram d, int x, int y) {
		boolean doNotAllow = (d instanceof IChildElement) || (d instanceof CommentThreadElement) || d instanceof Relationship2 || d instanceof CircleElement;
		// could ask from diagram next in diagram
		// activity start could return activity, activity end could return note...
		// but more files would be modified and that is not certainly nice...
		if (!doNotAllow) {
			ReattachHelpers reattachHelpers = new ReattachHelpers();
			reattachHelpers.processDiagram(d);

	    int left = previouslySelected.getLeft();
	    int top = previouslySelected.getTop();
	    int width = d.getWidth();
	    int height = d.getHeight();

			// do not duplicate child or relationships, since rel e.g. cannot be connected
			// if child then e.g. create a note
			IDiagramItem item = createQuickNext(d);

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
		}
	}

	private IDiagramItem createQuickNext(Diagram d) {
		IDiagramItem result = switchType(d);
		if (result == null) {
			result = d.getDiagramItem().copy();
		} else {
			// switch type needs some background color
			result.setBackgroundColor(Theme.getCurrentColorScheme().getBackgroundColor().toRgbWithOpacity());
		}
		return result;
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
				result = createNoteItem(d);
				break;
			}
			case MIND_CENTRAL: {
				result = createTopicItem(d);
				break;
			}
			case CHOICE:
			case ACTIVITY_START: {
				result = createActivityItem(d);
				break;
			}
			case TEXT_ITEM: {
				result = d.getDiagramItem().copy();
				result.setText("Just text");
				break;
			}
			case FORK: {
				result = createActivityItem(d);
				break;
			}
			case ACTOR: {
				result = createUseCase(d);
				break;
			}
		}
		return result;
	}

	private IDiagramItem createNoteItem(Diagram d) {
		IDiagramItem result = new DiagramItemDTO();
		result.setType(ElementType.NOTE.getValue());
		result.setShape(d.getLeft() + "," + d.getTop() + "," + "150,45");
		result.setText("Note");
		return result;
	}

  private IDiagramItem createTopicItem(Diagram d) {
		IDiagramItem result = new DiagramItemDTO();
		result.setType(ElementType.ACTIVITY.getValue());
		result.setShape(d.getLeft() + "," + d.getTop() + "," + "92,42");
		result.setText("Main Topic");
		return result;
  }

  private IDiagramItem createActivityItem(Diagram d) {
		IDiagramItem result = new DiagramItemDTO();
		result.setType(ElementType.ACTIVITY.getValue());
		result.setShape(d.getLeft() + "," + d.getTop() + "," + "92,42");
		result.setText("My Activity");
		return result;
  }

	private IDiagramItem createUseCase(Diagram d) {
		IDiagramItem result = new DiagramItemDTO();
		result.setType(ElementType.ELLIPSE.getValue());
		result.setShape(d.getLeft() + "," + d.getTop() + "," + "63,21");
		result.setText("Use Case");
		return result;
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
		// TODO last selected type
		result.setText(RelationshipHelpers.relationship(start, surface.getEditorContext(), end));
		result.setShapeProperties(ShapeProperty.CURVED_ARROW.getValue() | 
															ShapeProperty.CLOSEST_PATH.getValue());
		result.setShape(closestSegment.start.x + "," + 
									closestSegment.start.y + "," + 
									closestSegment.end.x + "," +
									closestSegment.end.y);
		result.setCustomData(start.getDiagramItem().getClientId() + ":" + end.getDiagramItem().getClientId());
		return result;
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

