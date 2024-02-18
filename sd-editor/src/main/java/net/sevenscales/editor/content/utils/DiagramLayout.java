package net.sevenscales.editor.content.utils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

class DiagramLayout {
  public static void alignDiagrams(List<Diagram> diagrams, int minDistance, Set<Relationship2> relationships) {
    // Step 1: Identify Clusters
    List<Set<Diagram>> clusters = identifyClusters(diagrams, relationships);

    // This map will hold the bounding box for each cluster
    Map<Set<Diagram>, Rectangle> clusterBounds = new HashMap<>();

    // Step 2: Layout Individual Clusters
    for (Set<Diagram> cluster : clusters) {
      Diagram centerDiagram = findMostConnectedDiagram(new ArrayList<>(cluster), relationships);
      if (centerDiagram == null) {
        continue;
      }
      layoutCluster(cluster, centerDiagram, minDistance);
      clusterBounds.put(cluster, calculateClusterBounds(cluster));
    }

    // Step 3: Layout Clusters Relative to Each Other
    // Here, we'd need to implement a method to layout the clusters considering
    // their bounds
    layoutClustersRelative(clusterBounds, minDistance);
  }

  private static List<Set<Diagram>> identifyClusters(List<Diagram> diagrams, Set<Relationship2> relationships) {
    List<Set<Diagram>> clusters = new ArrayList<>();
    Set<Diagram> visited = new HashSet<>();

    for (Diagram diagram : diagrams) {
      if (!visited.contains(diagram)) {
        Set<Diagram> cluster = new HashSet<>();
        dfs(diagram, cluster, relationships, visited, diagrams);
        clusters.add(cluster);
      }
    }

    return clusters;
  }

  private static void dfs(
      Diagram diagram,
      Set<Diagram> cluster,
      Set<Relationship2> relationships,
      Set<Diagram> visited,
      List<Diagram> diagrams) {
    visited.add(diagram);
    cluster.add(diagram);
    for (Relationship2 relationship : relationships) {
      if (relationship.getStartClientId().equals(diagram.getClientId())) {
        Diagram nextDiagram = findDiagramById(diagrams, relationship.getEndClientId());
        if (nextDiagram != null && !visited.contains(nextDiagram)) {
          dfs(nextDiagram, cluster, relationships, visited, diagrams);
        }
      }
    }
  }

  private static void layoutCluster(Set<Diagram> cluster, Diagram centerDiagram, int minDistance) {
    double angleIncrement = 2 * Math.PI / cluster.size();
    double currentAngle = 0.0;
    int radius = minDistance; // This can be dynamically adjusted if needed

    for (Diagram diagram : cluster) {
      if (diagram.equals(centerDiagram))
        continue;

      int dx = (int) (radius * Math.cos(currentAngle));
      int dy = (int) (radius * Math.sin(currentAngle));

      diagram.setTransform(dx, dy);

      currentAngle += angleIncrement;
    }
  }

  private static Rectangle calculateClusterBounds(Set<Diagram> cluster) {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxX = 0;
    int maxY = 0;

    for (Diagram diagram : cluster) {
      minX = Math.min(minX, diagram.getLeft());
      minY = Math.min(minY, diagram.getTop());
      maxX = Math.max(maxX, diagram.getLeft() + diagram.getWidth());
      maxY = Math.max(maxY, diagram.getTop() + diagram.getHeight());
    }

    return new Rectangle(minX, minY, maxX - minX, maxY - minY);
  }

  private static void layoutClustersRelative(Map<Set<Diagram>, Rectangle> clusterBounds, int minDistance) {
    // Assuming we have a Rectangle class that has x, y, width, and height
    int currentX = 0;
    int currentY = 0;

    for (Map.Entry<Set<Diagram>, Rectangle> entry : clusterBounds.entrySet()) {
      Rectangle bounds = entry.getValue();
      moveCluster(entry.getKey(), currentX - bounds.x, currentY - bounds.y);

      // Update currentX and currentY to position the next cluster
      currentX += bounds.width + minDistance;
    }
  }

  private static void moveCluster(Set<Diagram> cluster, int dx, int dy) {
    for (Diagram diagram : cluster) {
      diagram.setTransform(dx, dy);
    }
  }

  private static Diagram findDiagramById(List<Diagram> diagrams, String id) {
    return diagrams.stream().filter(d -> d.getDiagramItem().getClientId().equals(id)).findFirst().orElse(null);
  }

  private static Diagram findMostConnectedDiagram(
      List<Diagram> group,
      Set<Relationship2> relationships) {
    Diagram mostConnected = null;
    int maxConnections = -1;

    for (Diagram diagram : group) {
      // int connections = diagram.getConnect().size();
      int connections = relationships.stream()
          .filter(r -> r.getStartClientId().equals(diagram.getDiagramItem().getClientId()) ||
              r.getEndClientId().equals(diagram.getDiagramItem().getClientId()))
          .collect(Collectors.toList()).size();

      if (connections > maxConnections) {
        maxConnections = connections;
        mostConnected = diagram;
      }
    }

    return mostConnected;
  }

  // private int calculateInitialRadius(List<Diagram> diagrams, int minDistance) {
  // // Calculate an initial radius based on the size of the diagrams to ensure
  // enough space between them
  // int maxDimension = diagrams.stream()
  // .mapToInt(d -> Math.max(d.getWidth(), d.getHeight()))
  // .max()
  // .orElse(0);
  // return maxDimension / 2 + minDistance; // Start with half the size of the
  // largest diagram plus the minimum distance
  // }
}