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
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.gfx.domain.IRelationship;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.uicomponents.CircleElement;

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
      addChildren(d, diagrams);
    }
    return diagrams;
  }

  private static void addChildren(Diagram diagram, List<Diagram> diagrams) {
    if (diagram instanceof CircleElement) {
      addChildren(diagram.getOwnerComponent(), diagrams);
    }
    if (diagram instanceof IRelationship) {
      // cannot use IParentElement since comment thread is handling sending differently
      // when child is dragged parent is moved, all children are relative to parent
      // in here parent is moved (relationship) and children should store their position as well
      doAddChildren((IRelationship)diagram, diagrams);
    }
  }

  private static void doAddChildren(IRelationship parent, List<Diagram> diagrams) {
    for (IChildElement child : parent.getChildren()) {
      if (!diagrams.contains(child)) {
        diagrams.add(child.asDiagram());
      }
    }
  }

  private static void addRelatedConnections(Diagram diagram, List<Diagram> diagrams) {
    for (AnchorElement ae : diagram.getAnchors()) {
      if (ae.getRelationship() != null) {
        IDiagramItemRO di = ae.getRelationship().getDiagramItem();
        if (di.getClientId() != null && !diagrams.contains(ae.getRelationship())) {
          diagrams.add(ae.getRelationship());
          addChildren(ae.getRelationship(), diagrams);
        }
      }
    }
  }

}