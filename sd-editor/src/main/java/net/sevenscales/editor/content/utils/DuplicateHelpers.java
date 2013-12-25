package net.sevenscales.editor.content.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.ot.BoardDocument;
import net.sevenscales.editor.content.ClientIdHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

public class DuplicateHelpers {
	private ISurfaceHandler surface;
	private SelectionHandler selectionHandler;

	public DuplicateHelpers(ISurfaceHandler surface, SelectionHandler selectionHandler) {
		this.surface = surface;
		this.selectionHandler = selectionHandler;
	}
	
	public void duplicate(BoardDocument boardDocument) {
		Diagram[] selected = new Diagram[]{};
		selected = selectionHandler.getSelectedItems().toArray(selected);
		Map<String, String> clientIdMapping = new HashMap<String, String>();
		Set<Relationship2> relationships = new HashSet<Relationship2>();
		List<Diagram> duplicatedDiagrams = new ArrayList<Diagram>();
		ReattachHelpers reattachHelpers = new ReattachHelpers();

		// iterate all items and duplicate + generate client id; map original client id to new duplicated id
		// reconnect relationships based on mapped client IDs

		duplicateAndMapClientIds(selected, duplicatedDiagrams, relationships, clientIdMapping, reattachHelpers, boardDocument);

		// check if something was really duplicated; item might not support duplication like comment element.		
		if (duplicatedDiagrams.size() > 0) {
			// replace old connections with new client ids
			replaceOldConnections(relationships, clientIdMapping);

			boolean force = false;
			reattachHelpers.reattachRelationships(force);

			surface.addAsSelected(duplicatedDiagrams, true, true);
		}
	}

	private void duplicateAndMapClientIds(Diagram[] selected, 
																				List<Diagram> duplicatedDiagrams, 
																				Set<Relationship2> relationships, 
																				Map<String, String> clientIdMapping, 
																				ReattachHelpers reattachHelpers, BoardDocument boardDocument) {
		int i = 0;
		for (Diagram diagram : selected) {
			Diagram duplicated = diagram.duplicate(selected.length > 1);
			if (duplicated != null) {
				// if item supports duplication
				duplicatedDiagrams.add(duplicated);
				
				DiagramItemDTO di = (DiagramItemDTO) DiagramItemFactory.createOrUpdate(duplicated);
				// copy also custom data to be handled later
				// di.copyFrom(diagram.getDiagramItem());

				// same as copyFrom but doesn't copy location that has been just set in 
				// diagram.duplicate
				duplicated.duplicateFrom(diagram.getDiagramItem());

				// generate new client id
				di.setClientId(ClientIdHelpers.generateClientId(++i, boardDocument));
				
				if (duplicated instanceof Relationship2) {
					relationships.add((Relationship2) duplicated);
				}
				
				clientIdMapping.put(diagram.getDiagramItem().getClientId(), di.getClientId());
				reattachHelpers.processDiagram(duplicated);
			}
		}
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
