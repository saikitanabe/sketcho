package net.sevenscales.editor.content.utils;

import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sevenscales.domain.utils.Debug;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

public class LayoutForceDirected {
  private List<Cluster> clusters;
  private Set<Relationship2> relationships;
  private Set<Diagram> allDiagrams;
  private ISurfaceHandler surface;
  private Map<Diagram, Vector> forces = new HashMap<>();
  private Random random = new Random();

  private static final double DAMPING_FACTOR = 0.85;
  private double repulsionConstant = 5000;
  private double attractionConstant = 0.8;
  private double interClusterRepulsion = 10000; // Increased repulsion for inter-cluster layout
  private double epsilon = 0.1; // A small value to avoid division by zero

  public LayoutForceDirected(
      List<Cluster> clusters,
      Set<Relationship2> relationships,
      Set<Diagram> allDiagrams,
      ISurfaceHandler surface) {
    this.clusters = clusters;
    this.relationships = relationships;
    this.allDiagrams = allDiagrams;
    this.surface = surface;
  }

  public void layout() {
    initializePositions();
    for (int i = 0; i < 5; i++) {
      for (Cluster cluster : clusters) {
        prepareForceMaps(cluster);
        applyRepulsiveForces(cluster);
        applyAttractiveForces(cluster);
      }
      // applyInterClusterForces();
      updatePositions();
      coolSystem();
    }
  }

  private void initializePositions() {
    for (Diagram diagram : allDiagrams) {
      int initialX = random.nextInt(400); // Random initial X within a range
      int initialY = random.nextInt(400); // Random initial Y within a range
      diagram.setTransform(initialX, initialY); // Optionally, apply initial random positions
    }
  }  

  private void applyRepulsiveForces(Cluster cluster) {
    for (Diagram d1 : cluster.getDiagrams()) {
      for (Diagram d2 : cluster.getDiagrams()) {
        if (d1 != d2) {
          double dx = d1.getLeft() - d2.getLeft();
          double dy = d1.getTop() - d2.getTop();
          double distance = Math.sqrt(dx * dx + dy * dy) + epsilon;
          // Updated to linear repulsion: force inversely proportional to distance
          double forceMagnitude = repulsionConstant / distance;
          Vector force = new Vector(forceMagnitude * dx / distance, forceMagnitude * dy / distance);
          forces.get(d1).add(force);
          forces.get(d2).add(new Vector(-force.x, -force.y));

          Debug.log("force repul:" + d1.getText() + " " + force.x + " " + force.y);
          Debug.log("force repul:" + d2.getText() + " " + force.x + " " + force.y);
        }
      }
    }
  }

  private void applyAttractiveForces(Cluster cluster) {
    for (Relationship2 rel : relationships) {
      Diagram d1 = getStartDiagram(rel);
      Diagram d2 = getEndDiagram(rel);
      if (d1 != null && d2 != null && cluster.contains(d1) && cluster.contains(d2)) {
        double dx = d1.getLeft() - d2.getLeft();
        double dy = d1.getTop() - d2.getTop();
        double distance = Math.sqrt(dx * dx + dy * dy) + epsilon; // Add epsilon to avoid division by zero
        if (distance > epsilon) { // Ensuring there is a meaningful distance to compute forces
          double forceMagnitude = distance / attractionConstant; // Linear scaling of the force magnitude
          Vector force = new Vector(forceMagnitude * dx / distance, forceMagnitude * dy / distance);
          forces.get(d1).add(new Vector(-force.x, -force.y));
          forces.get(d2).add(force);

          Debug.log("force attr:" + d1.getText() + " " + force.x + " " + force.y);
          Debug.log("force attr:" + d2.getText() + " " + force.x + " " + force.y);
        }
      }
    }
  }

  private void updatePositions() {
    for (Cluster cluster : clusters) {
      for (Diagram diagram : cluster.getDiagrams()) {
        Vector force = forces.get(diagram);
        if (force.x != 0 && force.y != 0) {
          double newLeft = diagram.getLeft() + force.x;
          double newTop = diagram.getTop() + force.y;
          double dx = newLeft - diagram.getLeft();
          double dy = newTop - diagram.getTop();
          diagram.setTransform((int) dx, (int) dy);
          forces.put(diagram, new Vector(0, 0));
        }
      }
    }
  }

  private void applyInterClusterForces() {
    for (int i = 0; i < clusters.size(); i++) {
      for (int j = i + 1; j < clusters.size(); j++) {
        Cluster c1 = clusters.get(i);
        Cluster c2 = clusters.get(j);
        for (Diagram d1 : c1.getDiagrams()) {
          for (Diagram d2 : c2.getDiagrams()) {
            if (d1 != d2) {
              double dx = d1.getLeft() - d2.getLeft();
              double dy = d1.getTop() - d2.getTop();
              double distance = Math.sqrt(dx * dx + dy * dy);
              double forceMagnitude = interClusterRepulsion / (distance * distance);
              Vector force = new Vector(forceMagnitude * dx / distance, forceMagnitude * dy / distance);
              forces.get(d1).add(force);
              forces.get(d2).add(new Vector(-force.x, -force.y));
            }
          }
        }
      }
    }
  }  

  private Diagram getStartDiagram(Relationship2 relationship) {
    return findDiagramByClientId(relationship.getStartClientId());
  }

  private Diagram getEndDiagram(Relationship2 relationship) {
    return findDiagramByClientId(relationship.getEndClientId());
  }

  private Diagram findDiagramByClientId(String clientId) {
    for (Diagram diagram : allDiagrams) {
      if (diagram.getClientId().equals(clientId)) {
        return diagram;
      }
    }
    return null;
  }

  private void coolSystem() {
    repulsionConstant *= DAMPING_FACTOR;
    attractionConstant *= DAMPING_FACTOR;
  }

  private void prepareForceMaps(Cluster cluster) {
    for (Diagram diagram : cluster.getDiagrams()) {
      forces.put(diagram, new Vector(0, 0)); // Initialize force vectors for each diagram
    }
  }

  private static class Vector {
    double x, y;

    Vector(double x, double y) {
      this.x = x;
      this.y = y;
    }

    void add(Vector other) {
      this.x += other.x;
      this.y += other.y;
    }
  }
}
