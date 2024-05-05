package net.sevenscales.editor.content.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import net.sevenscales.domain.utils.Debug;
import net.sevenscales.editor.diagram.Diagram;

public class Cluster {

  static class Node {
    Diagram item;
    boolean placed;
    Set<String> connections = new HashSet<>();

    Node(Diagram item) {
      this.item = item;
    }
  }


  Node center;
  Set<Node> members;
  List<Diagram> diagrams;

  Cluster(Node center, Set<Node> members) {
    this.center = center;
    this.members = members;

    List<Diagram> diagrams = new ArrayList<>();
    diagrams.add(center.item);
    for (Node m : members) {
      diagrams.add(m.item);
    }

    this.diagrams = diagrams;
  }

  public List<Diagram> getDiagrams() {
    return this.diagrams;
  }

  public boolean contains(Diagram diagram) {
    if (diagram.getClientId().equals(center.item.getClientId())) {
      return true;
    }

    for (Node node : members) {
      if (node.item.getClientId().equals(diagram.getClientId())) {
        return true;
      }
    }

    return false;
  }

  public void print() {
    String result = this.members.stream()
        .map(m -> m.item.getClientId())
        .collect(Collectors.joining(", "));
    Debug.log("cluster: " + this.center.item.getClientId() + ": " + result);
  }
}
