package net.sevenscales.editor.diagram.utils;

import java.util.Set;
import java.util.HashSet;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.diagram.drag.AnchorElement;


public class DiagramEventHelpers {
	public static void fireChangedWithRelatedRelationships(ISurfaceHandler surface, Diagram diagram) {
		Set<Diagram> diagrams = new HashSet<Diagram>();
		addRelatedConnections(diagram, diagrams);
		diagrams.add(diagram);
    surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(diagrams));
	}

	private static void addRelatedConnections(Diagram diagram, Set<Diagram> diagrams) {
		for (AnchorElement ae : diagram.getAnchors()) {
			if (ae.getRelationship() != null) {
				diagrams.add(ae.getRelationship());
			}
		}
	}

	public static void fireChangedWithRelatedRelationships(ISurfaceHandler surface, Set<Diagram> diagrams) {
		for (Diagram d : diagrams) {
			addRelatedConnections(d, diagrams);
		}
		surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(diagrams));
	}

}