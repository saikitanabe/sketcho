package net.sevenscales.editor.content.utils;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.AnchorUtils;
import net.sevenscales.editor.uicomponents.AnchorUtils.AnchorProperties;

public class ContainerAttachHelpers {
//	private AbstractDiagramItem diagram;

	public ContainerAttachHelpers() {
//		this.diagram = diagram;
	}

  public static AnchorElement onAttachArea(AbstractDiagramItem diagram, Anchor anchor, int x, int y) {
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
				AnchorUtils.ClosestSegment closestSegment = AnchorUtils.closestSegment(diagram.getLeft(), diagram.getTop(), diagram.getWidth(), diagram.getHeight(), second.getLeft(), second.getTop(), second.getWidth(), second.getHeight());
							
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
		AbstractDiagramItem diagram, Anchor anchor, int x, int y
	) {
		if (AnchorUtils.onAttachAreaManual(x, y, diagram.getLeft(), diagram.getTop(), diagram.getWidth(),
				diagram.getHeight(), diagram.getSurfaceHandler())) {

			AnchorElement result = diagram.getAnchorElement(anchor);
			AnchorProperties tempAnchorProperties = diagram.getTempAnchorProperties();
			AnchorUtils.anchorPoint(x, y, tempAnchorProperties, diagram.getLeft(), diagram.getTop(), diagram.getWidth(),
					diagram.getHeight());

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
