package net.sevenscales.editor.content.utils;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.AnchorUtils;
import net.sevenscales.editor.uicomponents.AnchorUtils.AnchorProperties;

public class ContainerAttachHelpers {
//	private AbstractDiagramItem diagram;

  // >>>>>>>>> Debugging
  // private static net.sevenscales.editor.gfx.domain.ICircle tempCircle;
  // <<<<<<<<< Debugging

	public ContainerAttachHelpers() {
//		this.diagram = diagram;
	}

	// Returns 1 if the lines intersect, otherwise 0. In addition, if the lines 
	// intersect the intersection point may be stored in the floats i_x and i_y.

	public static AnchorElement onAttachAreaRotated(
  	AbstractDiagramItem diagram,
  	Anchor anchor,
  	int x,
  	int y,
    net.sevenscales.editor.gfx.domain.IGroup layer
  ) {

    // >>>>>>> DEBUGGING
    // if (tempCircle == null) {
    //   tempCircle = net.sevenscales.editor.gfx.domain.IShapeFactory.Util.factory(true).createCircle(layer);
    //   tempCircle.setShape(0, 0, 10);
    //   tempCircle.setStroke(218, 57, 57, 1);
    //   // tempCircle.setFill(218, 57, 57, 1);
    //   tempCircle.setStrokeWidth(2);
    // }

    // tempCircle.setShape(x, y, 10);
    // <<<<<<<< DEBUGGING

  	return ContainerAttachHelpers.onAttachArea(
  		diagram,
  		anchor,
  		x,
  		y
  	);
	}

  public static AnchorElement onAttachArea(
  	AbstractDiagramItem diagram, 
  	Anchor anchor, 
  	int x,
  	int y
  ) {
		// container attach is different only border areas can attach
		// TODO make this as utility to be used by other container elements
	//	return super.onAttachArea(anchor, x, y, rectSurface.getX(), rectSurface.getY() - CORNER_HEIGHT, rectSurface.getWidth(), rectSurface.getHeight() + CORNER_HEIGHT);
		// put all values to 0 not to attach any other than connection handle
		AnchorElement a = diagram.onAttachArea(anchor, x, y, 0, 0, 0, 0);
		if (a != null) {
			return a;
		}
		
		AnchorElement result = ContainerAttachHelpers.onAttachAreaManualOnly(diagram, anchor, x, y);

		if (result != null) {
			return result;
		}

		if (AnchorUtils.onAttachAreaAuto(
			x,
			y,
			diagram.getLeft(), 
			diagram.getTop(), 
			diagram.getWidth(), 
			diagram.getHeight(),
			diagram.getSurfaceHandler())) {

			result = diagram.getAnchorElement(anchor);
			AnchorProperties tempAnchorProperties = diagram.getTempAnchorProperties();

			Diagram second = anchor.getTheOtherEnd().getDiagram();
			if (second != null) {
				AnchorUtils.ClosestSegment closestSegment = AnchorUtils.closestSegment(diagram.getLeft(), diagram.getTop(), diagram.getWidth(), diagram.getHeight(), diagram.getDiagramItem().getRotateDegrees(), second.getLeft(), second.getTop(), second.getWidth(), second.getHeight(), second.getDiagramItem().getRotateDegrees());
							
				tempAnchorProperties.x = closestSegment.start.x;
				tempAnchorProperties.y = closestSegment.start.y;
			} else {
				AnchorUtils.anchorPoint(x, y, tempAnchorProperties, diagram.getLeft(), diagram.getTop(), diagram.getWidth(), diagram.getHeight());

				result.setAx(tempAnchorProperties.x);
				result.setAy(tempAnchorProperties.y);
				result.setRelativeX(tempAnchorProperties.relativeValueX);
				result.setRelativeY(tempAnchorProperties.relativeValueY);
				result.setCardinalDirection(tempAnchorProperties.cardinalDirection);
			}

			diagram.setAnchorPointShape(tempAnchorProperties.x, tempAnchorProperties.y);
	
			if (anchor.getRelationship() != null) {
				anchor.getRelationship().asClosestPath();
			}

			return result;
		}
		
	  return null;
	}
	
	public static AnchorElement onAttachAreaManualOnly(
		AbstractDiagramItem diagram, 
		Anchor anchor, 
		int x, 
		int y
	) {
		if (AnchorUtils.onAttachAreaManual(
			x,
			y,
			diagram.getLeft(),
			diagram.getTop(),
			diagram.getWidth(),
			diagram.getHeight(),
			diagram.getRotate(),
			diagram.getSurfaceHandler()
		)) {

			AnchorElement result = diagram.getAnchorElement(anchor);
			AnchorProperties tempAnchorProperties = diagram.getTempAnchorProperties();
			AnchorUtils.anchorPointRotated(
				x, 
				y,
				tempAnchorProperties,
				diagram.getLeft(),
				diagram.getTop(),
				diagram.getWidth(),
				diagram.getHeight(),
				diagram.getRotate()
			);

			result.setAx(tempAnchorProperties.x);
			result.setAy(tempAnchorProperties.y);
			result.setRelativeX(tempAnchorProperties.relativeValueX);
			result.setRelativeY(tempAnchorProperties.relativeValueY);
			result.setCardinalDirection(tempAnchorProperties.cardinalDirection);

			diagram.setAnchorPointShape(tempAnchorProperties.x, tempAnchorProperties.y);

			if (anchor.getRelationship() != null) {
				anchor.getRelationship().clearOnlyClosestPath();
			}

			return result;
		}

		return null;

	}
}
