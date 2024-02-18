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
import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.IGroup;

public class LayoutAlgorithm {
  private final Set<Diagram> diagrams;
  private final Set<Relationship2> relationships;
  private int currentY;

  // >>>>> DEBUG layout algorithm placement
  // static private ICircle tempCircle;
  // <<<<< DEBUG layout algorithm placement

  public LayoutAlgorithm(
    Set<Diagram> diagrams,
    Set<Relationship2> relationships,
    int currentY,
    ISurfaceHandler surface
  ) {
    this.diagrams = diagrams;
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
    Diagram center = findCenterDiagram();
    if (center == null)
      return;

    center.setTransform(0, this.currentY);

    // Calculate the initial radius
    int radius = calculateInitialRadius(center);

    // Place surrounding shapes
    placeSurroundingShapes(center, radius);

    // Adjust positions to be more centered
    adjustPositions();
  }

  private Diagram findCenterDiagram() {
    // Find the diagram with the most connections
    return diagrams.stream().max(Comparator.comparingInt(d -> countConnections(d.getClientId()))).orElse(null);
  }

  private int countConnections(String id) {
    // Count the number of relationships where this diagram is involved
    return (int) relationships.stream().filter(r -> r.getStartClientId().equals(id) || r.getEndClientId().equals(id))
        .count();
  }

  private int calculateInitialRadius(Diagram center) {
    // Calculate the initial radius based on the size of the center diagram
    return Math.max(center.getWidth(), center.getHeight()) / 2 + 50; // 50 is an arbitrary padding
  }

  private void placeSurroundingShapes(Diagram center, int initialRadius) {
    // Define the maximum radius to prevent shapes from being placed too far
    final int maxRadius = initialRadius + 2500; // Example value, adjust as needed
    double angleIncrement = 2 * Math.PI / (diagrams.size() - 1);
    double currentAngle = 0.0;
    int radius = initialRadius;

    for (Diagram diagram : diagrams) {
      if (diagram.equals(center))
        continue;

      boolean placed = false;
      int dx = 0;
      int dy = 0;

      while (!placed) {
        int newLeft = (center.getLeft() + center.getWidth() / 2) + (int) (radius * Math.cos(currentAngle));
        int newTop = (center.getTop() + center.getHeight() / 2) + (int) (radius * Math.sin(currentAngle));

        // >>>>> DEBUG layout algorithm placement
        // tempCircle.setShape(newLeft, newTop, 10);
        // tempCircle.setStroke(218, 57, 57, 1);
        // tempCircle.moveToFront();
        // <<<<< DEBUG layout algorithm placement

        // dx += newLeft - diagram.getLeft() - diagram.getWidth() / 2;
        // dy += newTop - diagram.getTop() - diagram.getHeight() / 2;
        dx += newLeft - diagram.getLeft();
        dy += newTop - diagram.getTop();

        diagram.setTransform(dx, dy);

        if (!checkOverlap(diagram)) {
          placed = true;
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
    for (Diagram other : diagrams) {
      if (other != diagram) {
        if (diagram.getLeft() < other.getLeft() + other.getWidth() &&
            diagram.getLeft() + diagram.getWidth() > other.getLeft() &&
            diagram.getTop() < other.getTop() + other.getHeight() &&
            diagram.getTop() + diagram.getHeight() > other.getTop()) {
          return true; // Overlap detected
        }
      }
    }
    return false; // No overlap detected
  }

  private void adjustPositions() {
    Diagram center = findCenterDiagram();
    if (center == null) return;
  
    int centerX = center.getLeft() + center.getWidth() / 2;
    int centerY = center.getTop() + center.getHeight() / 2;
  
    for (Diagram diagram : diagrams) {
      if (diagram.equals(center)) continue; // Skip the center diagram
  
      int diagramCenterX = diagram.getLeft() + diagram.getWidth() / 2;
      int diagramCenterY = diagram.getTop() + diagram.getHeight() / 2;
  
      // Calculate the differences
      int diffx = diagramCenterX - centerX;
      int diffy = diagramCenterY - centerY;
  
      // Check for other diagrams that are center-aligned along the intended axis and on the same side
      boolean hasAlignedDiagramOnSameSide = diagrams.stream()
                                           .filter(d -> !d.equals(diagram) && !d.equals(center))
                                           .anyMatch(d -> {
                                             int otherCenterX = d.getLeft() + d.getWidth() / 2;
                                             int otherCenterY = d.getTop() + d.getHeight() / 2;
                                             boolean isSameVerticalSide = (otherCenterY > centerY) == (diagramCenterY > centerY);
                                             boolean isSameHorizontalSide = (otherCenterX > centerX) == (diagramCenterX > centerX);
                                             return (Math.abs(diffx) < Math.abs(diffy)) ? (otherCenterX == centerX && isSameHorizontalSide) : (otherCenterY == centerY && isSameVerticalSide);
                                           });
  
      if (!hasAlignedDiagramOnSameSide) {
        // Adjust based on the smaller absolute difference and absence of overlap
        if (Math.abs(diffx) < Math.abs(diffy) && !checkOverlapAfterAdjustment(diagram, -diffx, 0)) {
          diagram.setTransform(diagram.getTransformX() - diffx, diagram.getTransformY());
        } else if (!checkOverlapAfterAdjustment(diagram, 0, -diffy)) {
          diagram.setTransform(diagram.getTransformX(), diagram.getTransformY() - diffy);
        }
      }
    }
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