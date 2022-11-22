package net.sevenscales.editor.api.impl;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramSearch;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

public class SurfaceDiagramSearch implements DiagramSearch {
	// private List<Diagram> diagrams;
  private ISurfaceHandler surface;

	public SurfaceDiagramSearch(ISurfaceHandler surface) {
		this.surface = surface;
	}
	
	@Override
	public Diagram findByClientId(String clientId) {
		if (clientId == null || "".equals(clientId)) {
			return null;
		}
		
		for (Diagram d : surface.getDiagrams()) {
			if ( !(d instanceof CircleElement) && d.getDiagramItem() != null && clientId.equals(d.getDiagramItem().getClientId())) {
				return d;
			}
		}
		return null;
	}

	@Override
	public List<Diagram> findAllByType(String elementType) {
		List<Diagram> result = new ArrayList<Diagram>();

		if (elementType == null) {
			return result;
		}

		for (Diagram d : surface.getDiagrams()) {
			IDiagramItem di = d.getDiagramItem();
			if (di != null && elementType.equals(di.getType())) {
				result.add(d);
			}
		}
		return result;
	}
	
	@Override
	public Set<Relationship2> findRelationshipsByConnectedToClientId(String clientId) {
		Set<Relationship2> result = new HashSet<Relationship2>();
		if (clientId == null || "".equals(clientId)) {
			return null;
		}
		
		for (Diagram d : surface.getDiagrams()) {
			if (d instanceof Relationship2) {
				Relationship2 r = (Relationship2) d;
				if (clientId.equals(r.getStartClientId())) {
					result.add(r);
				} else if (clientId.equals(r.getEndClientId())) {
					result.add(r);
				}
			}
		}
		
		return result;
	}

}
