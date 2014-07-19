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
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEvent;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.AnchorUtils;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;


class QuickConnectionHandler implements MouseDiagramHandler {
	private ISurfaceHandler surface;
	private DiagramSearch search;
	private Diagram previouslySelected;

	public QuickConnectionHandler(ISurfaceHandler surface) {
		this.surface = surface;
		search = surface.createDiagramSearch();

		// TODO ESC key handler deletes created elements and those are
		// deleted also from undo cache!
	}

	public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
		return false;
	}

	public void onMouseMove(Diagram sender, MatrixPointJS point) {

	}

	public void onMouseUp(Diagram sender, MatrixPointJS point) {
		// get previously selected!! selection handler could keep that state
		Set<Diagram> selected = surface.getSelectionHandler().getSelectedItems();
		if (selected.size() == 1) {
			previouslySelected = selected.iterator().next();
		} else if (selected.size() == 0 && previouslySelected != null && exists(previouslySelected)) {
			ScaledAndTranslatedPoint stp = ScaleHelpers.scaleAndTranslateScreenpoint(point.getScreenX(), point.getScreenY(), surface);
			int x = stp.scaledAndTranslatedPoint.x;
			int y = stp.scaledAndTranslatedPoint.y;

			createConnectedDiagram(previouslySelected, x, y);

			previouslySelected = null;
		} else {
			previouslySelected = null;
		}
	}

	private void createConnectedDiagram(Diagram d, int x, int y) {
		boolean doNotAllow = (d instanceof IChildElement) || d instanceof Relationship2 || d instanceof CircleElement;
		// could ask from diagram next in diagram
		// activity start could return activity, activity end could return note...
		// but more files would be modified and that is not certainly nice...
		if (!doNotAllow) {
			ReattachHelpers reattachHelpers = new ReattachHelpers();
			reattachHelpers.processDiagram(d);
			// do not duplicate child or relationships, since rel e.g. cannot be connected
			// if child then e.g. create a note
			IDiagramItem item = d.getDiagramItem().copy();
			// generate id and add elements normally
			// null to regenerate new client id
			item.setClientId(null);
			ClientIdHelpers.generateClientIdIfNotSet(item, 0, surface.getEditorContext().getGraphicalDocumentCache());
	    AbstractDiagramFactory factory = ShapeParser.factory(item);
	    int left = previouslySelected.getLeft();
	    int top = previouslySelected.getTop();
	    Info shape = factory.parseShape(item, x - left, y - top);

	    // TODO quick handler should not work if not editable!
	    // TODO if child text of comment, then need to decide a special case
	    Diagram newelement = factory.parseDiagram(surface, shape, true, item, null);
			reattachHelpers.processDiagram(newelement);

	    // create connection with closest connection
	    Relationship2 relationship = createRelationshipInBetween(d, newelement);
			// relationship.anchorStart(true);
			// relationship.anchorEnd(true);
			reattachHelpers.processDiagram(relationship);

	    List<Diagram> newitems = new ArrayList<Diagram>();
	    newitems.add(newelement);
	    newitems.add(relationship);
	    surface.add(newitems, true, false);

			reattachHelpers.reattachRelationshipsAndDraw();
		
	    // open editor for the created element
  		surface.getEditorContext().getEventBus().fireEvent(new ShowDiagramPropertyTextEditorEvent(newelement).setJustCreated(true));
		}
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
		result.setText("->");
		result.setShapeProperties(ShapeProperty.CURVED_ARROW.getValue());
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

