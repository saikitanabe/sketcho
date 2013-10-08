package net.sevenscales.domain;

import com.google.gwt.json.client.*;

public class JSONDiagramParser {
	private DiagramItemDTO diagramItem;
	private CommentDTO comment;

	public JSONDiagramParser(JSONObject obj) {
		String type = JSONParserHelpers.getString(obj.get("elementType"));
		if (ElementType.COMMENT.getValue().equals(type)) {
			comment = parseComment(obj);
		} else {
			diagramItem = parseDiagram(obj);
		}
	}

	private CommentDTO parseComment(JSONObject obj) {
		return new CommentDTO(JSONParserHelpers.getString(obj.get("text")),
												  JSONParserHelpers.getString(obj.get("elementType")),
												  JSONParserHelpers.getString(obj.get("shape")),
												  JSONParserHelpers.getString(obj.get("backgroundColor")),
												  JSONParserHelpers.getString(obj.get("textColor")),
												  JSONParserHelpers.getInt(obj.get("version")),
												  JSONParserHelpers.getLong(obj.get("id")),
												  JSONParserHelpers.getString(obj.get("clientId")),
												  JSONParserHelpers.getString(obj.get("cd")),
												  JSONParserHelpers.getDouble(obj.get("crc")),
												  JSONParserHelpers.getString(obj.get("p")),
												  JSONParserHelpers.getString(obj.get("cby")),
												  JSONParserHelpers.getString(obj.get("cbyd")),
												  JSONParserHelpers.getLong(obj.get("cat")),
												  JSONParserHelpers.getLong(obj.get("uat")));
	}

	private DiagramItemDTO parseDiagram(JSONObject obj) {
		return new DiagramItemDTO(JSONParserHelpers.getString(obj.get("text")),
														  JSONParserHelpers.getString(obj.get("elementType")),
														  JSONParserHelpers.getString(obj.get("shape")),
														  JSONParserHelpers.getString(obj.get("backgroundColor")),
														  JSONParserHelpers.getString(obj.get("textColor")),
														  JSONParserHelpers.getInt(obj.get("version")),
														  JSONParserHelpers.getLong(obj.get("id")),
														  JSONParserHelpers.getString(obj.get("clientId")),
														  JSONParserHelpers.getString(obj.get("cd")),
														  JSONParserHelpers.getDouble(obj.get("crc")));
	}

	public CommentDTO isComment() {
		return comment;
	}

	public DiagramItemDTO isDiagram() {
		return diagramItem;
	}

	public IDiagramItemRO isItem() {
		if (diagramItem != null) {
			return diagramItem;
		} else if (comment != null) {
			return comment;
		}
		return comment;
	}

}
