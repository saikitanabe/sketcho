package net.sevenscales.editor.diagram.utils;


import java.util.Set;
import java.util.HashSet;
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
      Set<Diagram> diagrams = followers(selectedItems);
      diagrams.addAll(DiagramHelpers.filterOwnerDiagramsAsList(selectedItems, actionType));
      surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(diagrams));
    }
  }

  public static void fireChangedWithRelatedRelationships(ISurfaceHandler surface, Diagram diagram, ActionType actionType) {
    if (surface.getEditorContext().isTrue(EditorProperty.ON_CHANGE_ENABLED)) {
      Set<Diagram> diagrams = new HashSet<Diagram>();
      addRelatedConnections(diagram, diagrams);
      diagrams.add(diagram.getOwnerComponent(actionType));
      surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(diagrams));
    }
  }

  private static Set<Diagram> followers(Set<Diagram> selectedItems) {
    Set<Diagram> diagrams = new HashSet<Diagram>();
    for (Diagram d : selectedItems) {
      addRelatedConnections(d, diagrams);
    }
    return diagrams;
  }

  private static void addRelatedConnections(Diagram diagram, Set<Diagram> diagrams) {
    for (AnchorElement ae : diagram.getAnchors()) {
      if (ae.getRelationship() != null) {
        diagrams.add(ae.getRelationship());
      }
    }
  }

}