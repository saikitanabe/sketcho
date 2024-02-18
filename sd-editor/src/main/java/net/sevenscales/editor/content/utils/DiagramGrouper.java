package net.sevenscales.editor.content.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.domain.utils.Debug;
import net.sevenscales.editor.diagram.Diagram;

public class DiagramGrouper {

  private Set<Diagram> visited = new HashSet<>();
  private List<Set<Diagram>> connectedGroups = new ArrayList<>();
  private Set<Relationship2> relationships;
  private Set<Diagram> shapes;

  public DiagramGrouper(Set<Diagram> shapes, Set<Relationship2> relationships) {
    this.shapes = shapes;
    this.relationships = relationships;
  }

  public List<Set<Diagram>> groupDiagrams() {
    for (Diagram shape : shapes) {
      if (!visited.contains(shape)) {
        Set<Diagram> newGroup = new HashSet<>();
        dfs(shape, newGroup);
        connectedGroups.add(newGroup);
      }
    }

    // int i = 1;
    // for (Set<Diagram> group : connectedGroups) {
    //   Debug.log("group", i, group.size());

    //   for (Diagram d : group) {
    //     Debug.log("  group", d.getDiagramItem().getText());
    //   }
    //   ++i;
    // }

    return connectedGroups;
  }

  private void dfs(Diagram shape, Set<Diagram> group) {
    visited.add(shape);
    group.add(shape);

    for (Diagram nextShape : getConnectedShapes(shape)) {
      if (!visited.contains(nextShape)) {
        dfs(nextShape, group);
      }
    }
  }

  private Set<Diagram> getConnectedShapes(Diagram shape) {
    Set<Diagram> connectedShapes = new HashSet<>();
    String cid = shape.getDiagramItem().getClientId();

    for (Relationship2 r : relationships) {
      if (r.getStartClientId().equals(cid) || r.getEndClientId().equals(cid)) {
        String otherCid = r.getStartClientId().equals(cid) ? r.getEndClientId() : r.getStartClientId();
        // Assuming `shapes` is accessible or passed to this method to find Diagram by
        // clientId
        Diagram otherShape = findDiagramByClientId(otherCid);
        if (otherShape != null) {
          connectedShapes.add(otherShape);
        }
      }
    }
    return connectedShapes;
  }

  private Diagram findDiagramByClientId(String clientId) {
    // Implement this method to find and return the Diagram object based on its
    // clientId
    // This might involve searching through a list of all Diagrams that was
    // initially provided
    for (Diagram d : this.shapes) {
      if (d.getClientId().equals(clientId)) {
        return d;
      }
    }

    return null; // placeholder return
  }

}
