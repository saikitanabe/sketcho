package net.sevenscales.editor.diagram.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.logging.client.LogConfiguration;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramSearch;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

public class ReattachHelpers {
	private static final SLogger logger = SLogger.createLogger(ReattachHelpers.class);
	
	private Set<Relationship2> relationships = new HashSet<Relationship2>();
	private Map<String, Diagram> clientIdMapping = new HashMap<String, Diagram>();
//	private DummySearch
	private DiagramSearch diagramSearch = new DiagramSearch() {
		@Override
		public Diagram findByClientId(String clientId) {
			return null;
		}

		@Override
		public Set<Relationship2> findRelationshipsByConnectedToClientId(String clientId) {
			return null;
		}
	};
	private boolean searchConnections;
	
	public ReattachHelpers() {
	}
	
	public ReattachHelpers(DiagramSearch diagramSearch) {
		this.diagramSearch = diagramSearch;
		this.searchConnections = true;
	}
	
	public void processDiagram(Diagram diagram) {
		if (diagram != null && !(diagram instanceof CircleElement)) {
	    if (diagram instanceof Relationship2) {
	    	Relationship2 r = (Relationship2) diagram;

	    	// OT operations might have updated custom data anchor points
	    	// not visible yet on screen, but underlying model has been
	    	// changed and this algorightm uses underlying model
	    	r.applyCustomData();

	    	relationships.add(r);
	    	
	    	if (searchConnections) {
	    		addConnection(r.getStartClientId());
	    		addConnection(r.getEndClientId());
	    	}
	    } else {
	    	if (searchConnections) {
	    		addMissingRelationships(diagram);
	    	}
	    }
	    
	    // TODO: this should not be catched, just for debugging!!
	    try {
	    	clientIdMapping.put(diagram.getDiagramItem().getClientId(), diagram);
	    } catch (Exception e) {
	    	String asstring = diagram == null ? "null" : diagram.toString();
	    	String s2 = diagram.getDiagramItem() == null ? "null" : diagram.getDiagramItem().toString();
	    	String s3 = diagram.getDiagramItem() == null ? "null" : diagram.getDiagramItem().getClientId().toString();
	    	throw new RuntimeException(logger.format("diagram {} s2 {} s3 {}", asstring, s2, s3), e);
	    }
		}
	}
	
	private void addMissingRelationships(Diagram diagram) {
  	Set<Relationship2> rels = diagramSearch.findRelationshipsByConnectedToClientId(diagram.getDiagramItem().getClientId());
  	for (Relationship2 rel : rels) {
  		relationships.add(rel);
  	}
	}

	private void addConnection(String clientId) {
		Diagram diagram = diagramSearch.findByClientId(clientId);
		if (diagram != null) {
			clientIdMapping.put(clientId, diagram);
		}
	}

  public void reattachRelationships() {
  	reattachRelationships(false);
  }
  
  /**
   * 
   * @param force forces to runtime searching anchor elements
   */
  public void reattachRelationships(boolean force) {
    for (Relationship2 r : relationships) {
      // reattach anchors if any
    	if (r.isLegacyAnchor() || force) {
    		r.anchor(false);
   	 	} else {
   	 		// first detach relationship connections
   	 		// to make sure removal worked; it will be restored if connection still 
   	 		// exists
   	 		r.detachConnections();
   	 		// read again from data model to run time anchor
   	 		r.applyCustomData();
   	 		reattachRelationship(r, clientIdMapping);
   	 	}
    }
	}

	private void reattachRelationship(Relationship2 r, Map<String, Diagram> clientIdMapping) {
	 	mapClientId(r.getStartClientId(), r.getStartX(), r.getStartY(), r.getStartAnchor(), clientIdMapping, r);
	 	mapClientId(r.getEndClientId(), r.getEndX(), r.getEndY(), r.getEndAnchor(), clientIdMapping, r);
	}

	private void mapClientId(String clientId, int x, int y, Anchor anchor, Map<String, Diagram> clientIdMapping, Relationship2 relationship) {
		if (clientId != null && !"".equals(clientId) && clientIdMapping.containsKey(clientId)) {
			Diagram d = clientIdMapping.get(clientId);
			anchorPositionToDiagram(relationship, x, y, anchor, d);
 	 	}
  }
	
	public static void anchorPositionToDiagram(Relationship2 relationship, int x, int y, Anchor anchor, Diagram anchorTarget) {
		if (anchorTarget != null) {
		 	anchor.setDiagram(anchorTarget, false);
		 	// anchor.setRelationship(relationship);
		 	anchorTarget.anchorWith(anchor, x, y);
		}
	}
}
