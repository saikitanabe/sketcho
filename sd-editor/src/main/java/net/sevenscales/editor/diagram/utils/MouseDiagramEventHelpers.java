package net.sevenscales.editor.diagram.utils;


import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.event.DiagramElementAddedEvent;


public class MouseDiagramEventHelpers {
  public static void fireDiagramAddedEvent(Diagram item, ISurfaceHandler surface, ActionType actionType) {
    if (surface.getEditorContext().isTrue(EditorProperty.ON_CHANGE_ENABLED)) {
      // get all follow up connections
      // Set<Diagram> diagrams = followers(selectedItems);
      // diagrams.addAll(DiagramHelpers.filterOwnerDiagramsAsList(selectedItems, actionType));
      surface.getEditorContext().getEventBus().fireEvent(new DiagramElementAddedEvent(item, false));
    }
  }

  public static void fireDiagramsChangedEvenet(Set<Diagram> selectedItems, ISurfaceHandler surface, ActionType actionType) {
    if (surface.getEditorContext().isTrue(EditorProperty.ON_CHANGE_ENABLED)) {
      // get all follow up connections
      List<Diagram> diagrams = DiagramHelpers.filterOwnerDiagramsAsList(selectedItems, actionType);
      List<Diagram> rels = followers(selectedItems);
      diagrams.addAll(rels);
      surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(diagrams));
    }
  }

  public static void fireChangedWithRelatedRelationships(ISurfaceHandler surface, Diagram diagram, ActionType actionType) {
    if (surface.getEditorContext().isTrue(EditorProperty.ON_CHANGE_ENABLED)) {
      // order is important, so cardinal direction is calculated correctly on the other side!
      // therefore attached relationship are added after the diagram
      List<Diagram> diagrams = new ArrayList<Diagram>();
      diagrams.add(diagram.getOwnerComponent(actionType));
      addRelatedConnections(diagram, diagrams);
      surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(diagrams));
    }
  }

  private static List<Diagram> followers(Set<Diagram> selectedItems) {
    List<Diagram> diagrams = new ArrayList<Diagram>();
    for (Diagram d : selectedItems) {
      addRelatedConnections(d, diagrams);
    }
    return diagrams;
  }

  private static void addRelatedConnections(Diagram diagram, List<Diagram> diagrams) {
    for (AnchorElement ae : diagram.getAnchors()) {
      if (ae.getRelationship() != null) {
        IDiagramItemRO di = ae.getRelationship().getDiagramItem();
        if (di.getClientId() != null && !diagrams.contains(ae.getRelationship())) {
          diagrams.add(ae.getRelationship());
        }
      }
    }
  }

}