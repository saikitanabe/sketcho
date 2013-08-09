package net.sevenscales.editor.uicomponents.impl;

import net.sevenscales.editor.content.ui.LineSelections.RelationShipType;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.RelationshipShape2;
import net.sevenscales.editor.silver.RelationshipShape;

public class RelationshipTextUtil2 {
	private String text;
	private RelationshipShape2 relationshipShape = new RelationshipShape2();

	private int leftEnd = 0;
	private int rightStart = 0;

	public RelationshipTextUtil2() {
	}

	public String parseArrowLine() {
		String[] lines = text.split("\\n");
		if (lines.length > 1) {
			return lines[lines.length - 1];
		}
		return text;
	}
	
	public Info parseShape() {
		int result = 0;
		
		String arrowLine = parseArrowLine(); 
				
		// NOTE: order matters! Put most complex as first
		// sub elements needs be after more complex ones!
		if (arrowLine.matches(".*-\\|>.*")) {
			result |= RelationshipShape.INHERITANCE;
			leftEnd = arrowLine.indexOf(RelationShipType.INHERITANCE.getValue());
			rightStart = leftEnd + RelationShipType.INHERITANCE.getValue().length();
			relationshipShape.type = RelationShipType.INHERITANCE;
		} else if (arrowLine.matches(".*-->.*")) {
			result |= RelationshipShape.DEPENDANCY | RelationshipShape.DIRECTED;
			leftEnd = arrowLine.indexOf(RelationShipType.DEPENDANCY_DIRECTED.getValue());
			rightStart = leftEnd
					+ RelationShipType.DEPENDANCY_DIRECTED.getValue().length();
			relationshipShape.type = RelationShipType.DEPENDANCY_DIRECTED;
		} else if (arrowLine.matches(".*--.*")) {
			result |= RelationshipShape.DEPENDANCY;
			leftEnd = arrowLine.indexOf(RelationShipType.DEPENDANCY.getValue());
			rightStart = leftEnd + RelationShipType.DEPENDANCY.getValue().length();
			relationshipShape.type = RelationShipType.DEPENDANCY;
		} else if (arrowLine.matches(".*<>->.*")) {
			result |= RelationshipShape.AGGREGATE | RelationshipShape.DIRECTED;
			leftEnd = arrowLine.indexOf(RelationShipType.AGGREGATION_DIRECTED.getValue());
			rightStart = leftEnd
					+ RelationShipType.AGGREGATION_DIRECTED.getValue().length();
			relationshipShape.type = RelationShipType.AGGREGATION_DIRECTED;
		} else if (arrowLine.matches(".*<>-.*")) {
			result |= RelationshipShape.AGGREGATE;
			leftEnd = arrowLine.indexOf(RelationShipType.AGGREGATION.getValue());
			rightStart = leftEnd + RelationShipType.AGGREGATION.getValue().length();
			relationshipShape.type = RelationShipType.AGGREGATION;
		} else if (arrowLine.matches(".*->.*")) {
			result |= RelationshipShape.DIRECTED;
			leftEnd = arrowLine.indexOf(RelationShipType.DIRECTED.getValue());
			rightStart = leftEnd + RelationShipType.DIRECTED.getValue().length();
			relationshipShape.type = RelationShipType.DIRECTED;
		} else if (arrowLine.matches(".*-.*")) {
			// just zero
			leftEnd = arrowLine.indexOf(RelationShipType.LINE.getValue());
			rightStart = leftEnd + RelationShipType.LINE.getValue().length();
			relationshipShape.type = RelationShipType.LINE;
		} else {
			// fall back to plain line
			relationshipShape.type = RelationShipType.LINE;
		}

		relationshipShape.caps = result;
		return relationshipShape;
	}

	public String parseLeftText() {
		return parseArrowLine().substring(0, leftEnd);
	}

	public String parseRightText() {
		return parseArrowLine().substring(rightStart);
	}

	public String parseLabel() {
		String[] lines = text.split("\\n");
		if (lines.length > 1) {
			return lines[0];
		}
		return "";
	}
	
	public String parseConnection() {
		String result = parseArrowLine().substring(leftEnd, rightStart);
		if ("".equals(result)) {
			// fallback to line
			result = RelationShipType.LINE.getValue();
		}
		return result;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
