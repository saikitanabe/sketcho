package net.sevenscales.editor.diagram;

import java.util.Set;
import java.util.List;

import net.sevenscales.editor.uicomponents.uml.Relationship2;

public interface DiagramSearch {
	Diagram findByClientId(String clientId);
	List<Diagram> findAllByType(String elementType);
	Set<Relationship2> findRelationshipsByConnectedToClientId(String clientId);
}
