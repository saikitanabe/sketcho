package net.sevenscales.editor.uicomponents.impl;

import net.sevenscales.editor.content.RelationShipType;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.RelationshipShape2;

public class RelationshipTextUtil2 implements RelationshipParser {
	public static final int LEGACY_REL_FORMAT_VERSION_NUMBER = 3;
	public static final int NEW_REL_FORMAT_VERSION_NUMBER = 4;

	private Integer currentDataVersion;
	private RelationshipParser parser;

	public RelationshipTextUtil2(/*Integer currentDataVersion*/) {
		// default to parse legacy text format
		// this.currentDataVersion = currentDataVersion == null ? LEGACY_REL_FORMAT_VERSION_NUMBER : currentDataVersion;
		this.parser = new LegacyParser(); // createParser(this.currentDataVersion);
	}

	// private RelationshipParser createParser(Integer version) {
	// 	if (version <= LEGACY_REL_FORMAT_VERSION_NUMBER) {
	// 		return new LegacyParser();
	// 	}
	// 	return new NewParser();
	// }

	public String parseArrowLine() {
		return parser.parseArrowLine();
	}
	public Info parseShape(boolean curved) {
		return parser.parseShape(curved);
	}
	public String parseLeftText() {
		return parser.parseLeftText();
	}
	public String parseRightText() {
		return parser.parseRightText();
	}
	public String parseLabel() {
		return parser.parseLabel();
	}
	public String parseConnection() {
		return parser.parseConnection();
	}

	public void setText(String text) {
		parser.setText(text);
	}

	/**
	* New Relationship text parser, when text contains only shape...
	*/
	private static class NewParser implements RelationshipParser {
		private String text;
		public NewParser() {
		}

		public String parseArrowLine() {
			return null;			
		}
		public Info parseShape(boolean curved) {
			return null;
		}
		public String parseLeftText() {
			return null;
		}
		public String parseRightText() {
			return null;
		}
		public String parseLabel() {
			return null;
		}
		public String parseConnection() {
			return null;
		}
		public void setText(String text) {
			this.text = text;
		}
	}

	private static class LegacyParser implements RelationshipParser {
		private String text;
		private int leftEnd = 0;
		private int rightStart = 0;
		private RelationshipShape2 relationshipShape = new RelationshipShape2();

		public String parseArrowLine() {
			String[] lines = text.split("\\n");
			if (lines.length > 1) {
				return lines[lines.length - 1];
			}
			return text;
		}
		
		public Info parseShape(boolean curved) {
			int result = 0;
			
			String arrowLine = parseArrowLine(); 
					
			// NOTE: order matters! Put most complex as first
			// sub elements needs be after more complex ones!
			if (arrowLine.matches(".*-\\|\\*>.*")) {
				result |= RelationshipShape2.INHERITANCE | RelationshipShape2.FILLED;
				leftEnd = arrowLine.indexOf(RelationShipType.SYNCHRONIZED.getValue());
				rightStart = leftEnd + RelationShipType.SYNCHRONIZED.getValue().length();
				relationshipShape.type = RelationShipType.SYNCHRONIZED;
			} else if (arrowLine.matches(".*-\\|>.*")) {
				result |= RelationshipShape2.INHERITANCE;
				leftEnd = arrowLine.indexOf(RelationShipType.INHERITANCE.getValue());
				rightStart = leftEnd + RelationShipType.INHERITANCE.getValue().length();
				relationshipShape.type = RelationShipType.INHERITANCE;
			} else if (arrowLine.matches(".*<-->.*")) {
				result |= RelationshipShape2.DEPENDANCY | RelationshipShape2.DIRECTED | RelationshipShape2.DIRECTED_START;
				leftEnd = arrowLine.indexOf(RelationShipType.DEPENDANCY_DIRECTED_BOTH.getValue());
				rightStart = leftEnd
						+ RelationShipType.DEPENDANCY_DIRECTED_BOTH.getValue().length();
				relationshipShape.type = RelationShipType.DEPENDANCY_DIRECTED_BOTH;
			} else if (arrowLine.matches(".*-->.*")) {
				result |= RelationshipShape2.DEPENDANCY | RelationshipShape2.DIRECTED;
				leftEnd = arrowLine.indexOf(RelationShipType.DEPENDANCY_DIRECTED.getValue());
				rightStart = leftEnd
						+ RelationShipType.DEPENDANCY_DIRECTED.getValue().length();
				relationshipShape.type = RelationShipType.DEPENDANCY_DIRECTED;
			} else if (arrowLine.matches(".*--.*")) {
				result |= RelationshipShape2.DEPENDANCY;
				leftEnd = arrowLine.indexOf(RelationShipType.DEPENDANCY.getValue());
				rightStart = leftEnd + RelationShipType.DEPENDANCY.getValue().length();
				relationshipShape.type = RelationShipType.DEPENDANCY;
			} else if (arrowLine.matches(".*<\\*>->.*")) {
				result |= RelationshipShape2.AGGREGATE | RelationshipShape2.DIRECTED | RelationshipShape2.FILLED;
				leftEnd = arrowLine.indexOf(RelationShipType.AGGREGATION_DIRECTED_FILLED.getValue());
				rightStart = leftEnd
						+ RelationShipType.AGGREGATION_DIRECTED_FILLED.getValue().length();
				relationshipShape.type = RelationShipType.AGGREGATION_DIRECTED_FILLED;
			} else if (arrowLine.matches(".*<>->.*")) {
				result |= RelationshipShape2.AGGREGATE | RelationshipShape2.DIRECTED;
				leftEnd = arrowLine.indexOf(RelationShipType.AGGREGATION_DIRECTED.getValue());
				rightStart = leftEnd
						+ RelationShipType.AGGREGATION_DIRECTED.getValue().length();
				relationshipShape.type = RelationShipType.AGGREGATION_DIRECTED;
			} else if (arrowLine.matches(".*<\\*>-.*")) {
				result |= RelationshipShape2.AGGREGATE | RelationshipShape2.FILLED;
				leftEnd = arrowLine.indexOf(RelationShipType.AGGREGATION_FILLED.getValue());
				rightStart = leftEnd + RelationShipType.AGGREGATION_FILLED.getValue().length();
				relationshipShape.type = RelationShipType.AGGREGATION_FILLED;
			} else if (arrowLine.matches(".*<>-.*")) {
				result |= RelationshipShape2.AGGREGATE;
				leftEnd = arrowLine.indexOf(RelationShipType.AGGREGATION.getValue());
				rightStart = leftEnd + RelationShipType.AGGREGATION.getValue().length();
				relationshipShape.type = RelationShipType.AGGREGATION;
			} else if (arrowLine.matches(".*<->.*")) {
				result |= RelationshipShape2.DIRECTED | RelationshipShape2.DIRECTED_START;
				leftEnd = arrowLine.indexOf(RelationShipType.DIRECTED_BOTH.getValue());
				rightStart = leftEnd + RelationShipType.DIRECTED_BOTH.getValue().length();
				relationshipShape.type = RelationShipType.DIRECTED_BOTH;
			} else if (arrowLine.matches(".*->.*")) {
				result |= RelationshipShape2.DIRECTED;
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

			if (curved) {
				result |= RelationshipShape2.CURVED;
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
}
