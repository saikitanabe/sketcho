package net.sevenscales.editor.content.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.ot.BoardDocument;
import net.sevenscales.editor.content.ClientIdHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.domain.utils.SLogger;


public class DuplicateHelpers {
	private static final SLogger logger = SLogger.createLogger(DuplicateHelpers.class);

	private ISurfaceHandler surface;
	private SelectionHandler selectionHandler;

	static {
		SLogger.addFilter(DuplicateHelpers.class);
	}

	public DuplicateHelpers(ISurfaceHandler surface, SelectionHandler selectionHandler) {
		this.surface = surface;
		this.selectionHandler = selectionHandler;
	}

	private static class State {
		public Map<String, String> clientIdMapping = new HashMap<String, String>();
		public ReattachHelpers reattachHelpers = new ReattachHelpers();
		public Set<Relationship2> relationships = new HashSet<Relationship2>();
		public List<Diagram> newItems = new ArrayList<Diagram>();

		public void addRelationshipIfAny(Diagram diagram) {
			if (diagram instanceof Relationship2) {
				relationships.add((Relationship2) diagram);
			}
		}
	}
	
	public void duplicate(BoardDocument boardDocument) {
		Diagram[] selected = new Diagram[]{};
		selected = selectionHandler.getSelectedItems().toArray(selected);

		// iterate all items and duplicate + generate client id; map original client id to new duplicated id
		// reconnect relationships based on mapped client IDs

		State state = duplicateAndMapClientIds(selected, boardDocument);

		addItemsToTheBoard(state);
	}

	private void addItemsToTheBoard(State state) {
		// check if something was really duplicated; item might not support duplication like comment element.
		if (state.newItems.size() > 0) {
			// replace old connections with new client ids
			replaceOldConnections(state.relationships, state.clientIdMapping);

			boolean force = false;
			state.reattachHelpers.reattachRelationships(force);

			surface.addAsSelected(state.newItems, true, true);
		}
	}

	private State duplicateAndMapClientIds(Diagram[] selected, BoardDocument boardDocument) {
		State result = new State();
		int i = 0;
		for (Diagram diagram : selected) {
			Diagram duplicated = diagram.duplicate(selected.length > 1);
			if (duplicated != null) {
				// if item supports duplication
				result.newItems.add(duplicated);
				
				DiagramItemDTO di = (DiagramItemDTO) DiagramItemFactory.createOrUpdate(duplicated);
				// copy also custom data to be handled later
				// di.copyFrom(diagram.getDiagramItem());

				// same as copyFrom but doesn't copy location that has been just set in 
				// diagram.duplicate
				duplicated.duplicateFrom(diagram.getDiagramItem());

				// generate new client id
				di.setClientId(ClientIdHelpers.generateClientId(++i, boardDocument));
				
				result.addRelationshipIfAny(duplicated);
				
				result.clientIdMapping.put(diagram.getDiagramItem().getClientId(), di.getClientId());
				result.reattachHelpers.processDiagram(duplicated);
			}
		}
		return result;
	}

	public void paste(int x, int y, List<IDiagramItemRO> items, BoardDocument boardDocument, boolean editable) {
		// TODO check if editable, should be checked already before copy!!
		// TODO what do to with comments!! is it allowed to copy those!!!???
		if (editable) {
			logger.debug("paste... {}", items);

			State state = copyAndMapClientIds(x, y, items, boardDocument);
			addItemsToTheBoard(state);
		}
	}

	private State copyAndMapClientIds(int x, int y, List<IDiagramItemRO> items, BoardDocument boardDocument) {
		State state = new State();
		int i = 0;

		int left = Integer.MAX_VALUE;
		int top = Integer.MAX_VALUE;
		for (IDiagramItemRO di : items) {
			Info shape = ShapeParser.parse(di, 0, 0);
			// Info shape = di.parseShape();
			int l = shape.getLeft();
			int t = shape.getTop();
			left = l < left ? l : left;
			top = t < top ? t : top;
		}

		int moveX = x - left;
		int moveY = y - top;

		for (IDiagramItemRO di : items) {
  		copyAndMap(moveX, moveY, di, ++i, state, boardDocument);
		}
		return state;
	}

	private void copyAndMap(int x, int y, IDiagramItemRO diro, int i, State state, BoardDocument boardDocument) {
		if (allowedToPasteType(diro) && diro instanceof IDiagramItem) {
			IDiagramItem di = (IDiagramItem) diro;
			String newClientId = ClientIdHelpers.generateClientId(i, boardDocument);
			state.clientIdMapping.put(di.getClientId(), newClientId);
			di.setClientId(newClientId);

			// always editable at this point
			Diagram copied = DiagramItemFactory.create(x, y, di, surface, true);
			state.newItems.add(copied);
			state.reattachHelpers.processDiagram(copied);
			state.addRelationshipIfAny(copied);
		}
	}

	private boolean allowedToPasteType(IDiagramItemRO diro) {
		return !ElementType.COMMENT.getValue().equals(diro.getType())	&&
					 !ElementType.COMMENT_THREAD.getValue().equals(diro.getType());
	}

	private void replaceOldConnections(Set<Relationship2> relationships, Map<String, String> clientIdMapping) {
		for (Relationship2 r : relationships) {
			String cd = r.getDiagramItem().getCustomData();
			if (cd.indexOf(":") > 0) {
				String[] connections = cd.split(":");
				String start = getMapping(connections[0], clientIdMapping);
				String end = getMapping(connections[1], clientIdMapping);
				r.getDiagramItem().setCustomData(start + ":" + end);
				r.parseCustomData(r.getDiagramItem().getCustomData());
			}
		}
	}

	private String getMapping(String connection, Map<String, String> clientIdMapping) {
		return clientIdMapping.get(connection) != null ? clientIdMapping.get(connection) : "";
	}
}
