package net.sevenscales.editor.content.utils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Comparator;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.IGroup;

public class LayoutAlgorithm {
  private final Cluster cluster;
  private final Set<Relationship2> relationships;
  private int currentY;

  // >>>>> DEBUG layout algorithm placement
  // static private ICircle tempCircle;
  // <<<<< DEBUG layout algorithm placement

  public LayoutAlgorithm(
    Cluster cluster,
    Set<Relationship2> relationships,
    int currentY,
    ISurfaceHandler surface
  ) {
    this.cluster = cluster;
    this.relationships = relationships;
    this.currentY = currentY;

    // >>>>> DEBUG layout algorithm placement
    // IGroup group = IShapeFactory.Util.factory(true).createGroup(surface.getElementLayer());
    // if (tempCircle == null) {
    //   tempCircle = IShapeFactory.Util.factory(true).createCircle(group);
    // }
    // <<<<< DEBUG layout algorithm placement
  }

  public void layout() {
    // Find the center shape
    // Diagram center = findCenterDiagram();
    // if (center == null)
    //   return;

    if (!this.cluster.center.placed) {
      this.cluster.center.item.setTransform(0, this.currentY);
      this.cluster.center.placed = true;
    }

    // this.cluster.center.item.setBackgroundColor(new Color(0, 191, 255, 1));

    // Calculate the initial radius
    int radius = calculateInitialRadius(this.cluster.center.item);

    // Place surrounding shapes
    placeSurroundingShapes(radius);

    // Adjust positions to be more centered
    adjustPositions();
  }

  // private Diagram findCenterDiagram() {
  //   // Find the diagram with the most connections
  //   return diagrams.stream().max(Comparator.comparingInt(d -> countConnections(d.getClientId()))).orElse(null);
  // }

  private int countConnections(String id) {
    // Count the number of relationships where this diagram is involved
    return (int) relationships.stream().filter(r -> r.getStartClientId().equals(id) || r.getEndClientId().equals(id))
        .count();
  }

  private int calculateInitialRadius(Diagram center) {
    // Calculate the initial radius based on the size of the center diagram
    return Math.max(center.getWidth(), center.getHeight()) / 2 + 500; // 500 is an arbitrary padding
  }

  private void placeSurroundingShapes(int initialRadius) {
    // Define the maximum radius to prevent shapes from being placed too far
    final int maxRadius = initialRadius + 0; // Example value, adjust as needed
    double angleIncrement = 2 * Math.PI / (this.cluster.members.size());
    double currentAngle = 0.0;
    int radius = initialRadius;

    for (Cluster.Node node : this.cluster.members) {
      // if (diagram.equals(center))
      //   continue;

      int dx = 0;
      int dy = 0;

      while (!node.placed) {
        int newLeft = (this.cluster.center.item.getLeft() + this.cluster.center.item.getWidth() / 2) + (int) (radius * Math.cos(currentAngle));
        int newTop = (this.cluster.center.item.getTop() + this.cluster.center.item.getHeight() / 2) + (int) (radius * Math.sin(currentAngle));

        // >>>>> DEBUG layout algorithm placement
        // tempCircle.setShape(newLeft, newTop, 10);
        // tempCircle.setStroke(218, 57, 57, 1);
        // tempCircle.moveToFront();
        // <<<<< DEBUG layout algorithm placement

        // dx += newLeft - diagram.getLeft() - diagram.getWidth() / 2;
        // dy += newTop - diagram.getTop() - diagram.getHeight() / 2;
        dx += newLeft - node.item.getLeft();
        dy += newTop - node.item.getTop();

        node.item.setTransform(dx, dy);

        if (!checkOverlap(node.item)) {
          node.placed = true;
          currentAngle += angleIncrement;
        } else {
          // Try adjusting the angle more finely before increasing the radius
          // currentAngle += angleIncrement / 4;
          currentAngle += angleIncrement;
          if (currentAngle >= 2 * Math.PI) {
            currentAngle = 0.0;
            radius += 50; // Increase radius after trying all angles
            if (radius > maxRadius) {
              // Implement logic to start a new layer or handle maximum radius reached
              break;
            }
          }
        }
      }
    }
  }

  private boolean checkOverlap(Diagram diagram) {
    // for (DiagramGrouper.Node other : this.cluster.members) {
    //   if (other.item != diagram) {
    //     if (diagram.getLeft() < other.item.getLeft() + other.item.getWidth() &&
    //         diagram.getLeft() + diagram.getWidth() > other.item.getLeft() &&
    //         diagram.getTop() < other.item.getTop() + other.item.getHeight() &&
    //         diagram.getTop() + diagram.getHeight() > other.item.getTop()) {
    //       return true; // Overlap detected
    //     }
    //   }
    // }
    return false; // No overlap detected
  }

  private void adjustPositions() {
    // Diagram center = findCenterDiagram();
    // if (center == null) return;
  
    // int centerX = this.cluster.center.item.getLeft() + this.cluster.center.item.getWidth() / 2;
    // int centerY = this.cluster.center.item.getTop() + this.cluster.center.item.getHeight() / 2;
  
    // for (DiagramGrouper.Node node : this.cluster.members) {
    //   if (node.item.equals(center)) continue; // Skip the center diagram
  
    //   int diagramCenterX = node.item.getLeft() + node.item.getWidth() / 2;
    //   int diagramCenterY = node.item.getTop() + node.item.getHeight() / 2;
  
    //   // Calculate the differences
    //   int diffx = diagramCenterX - centerX;
    //   int diffy = diagramCenterY - centerY;
  
    //   // Check for other diagrams that are center-aligned along the intended axis and on the same side
    //   boolean hasAlignedDiagramOnSameSide = diagrams.stream()
    //                                        .filter(d -> !d.equals(diagram) && !d.equals(center))
    //                                        .anyMatch(d -> {
    //                                          int otherCenterX = d.getLeft() + d.getWidth() / 2;
    //                                          int otherCenterY = d.getTop() + d.getHeight() / 2;
    //                                          boolean isSameVerticalSide = (otherCenterY > centerY) == (diagramCenterY > centerY);
    //                                          boolean isSameHorizontalSide = (otherCenterX > centerX) == (diagramCenterX > centerX);
    //                                          return (Math.abs(diffx) < Math.abs(diffy)) ? (otherCenterX == centerX && isSameHorizontalSide) : (otherCenterY == centerY && isSameVerticalSide);
    //                                        });
  
    //   if (!hasAlignedDiagramOnSameSide) {
    //     // Adjust based on the smaller absolute difference and absence of overlap
    //     if (Math.abs(diffx) < Math.abs(diffy) && !checkOverlapAfterAdjustment(diagram, -diffx, 0)) {
    //       diagram.setTransform(diagram.getTransformX() - diffx, diagram.getTransformY());
    //     } else if (!checkOverlapAfterAdjustment(diagram, 0, -diffy)) {
    //       diagram.setTransform(diagram.getTransformX(), diagram.getTransformY() - diffy);
    //     }
    //   }
    // }
  }
  
  private boolean checkOverlapAfterAdjustment(Diagram diagram, int dx, int dy) {
    // Temporarily adjust diagram position
    int originalX = diagram.getTransformX();
    int originalY = diagram.getTransformY();
    diagram.setTransform(originalX + dx, originalY + dy);
    boolean overlaps = checkOverlap(diagram);
    // Revert the adjustment
    diagram.setTransform(originalX, originalY);
    return overlaps;
  }
  
}