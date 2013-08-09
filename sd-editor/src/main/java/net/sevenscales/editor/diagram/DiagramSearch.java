package net.sevenscales.editor.diagram;

import java.util.Set;

import net.sevenscales.editor.uicomponents.uml.Relationship2;

public interface DiagramSearch {
	Diagram findByClientId(String clientId);
	Set<Relationship2> findRelationshipsByConnectedToClientId(String clientId);
}
