package net.sevenscales.domain;

import com.google.gwt.json.client.*;

public class JSONDiagramParser {
	private DiagramItemDTO diagramItem;
	private CommentDTO comment;
	// private CommentThreadDTO commentThread;

	public JSONDiagramParser(JSONObject obj, boolean diff) {
		String type = JSONParserHelpers.getString(obj.get("elementType"));
		if (ElementType.COMMENT.getValue().equals(type)) {
			comment = parseComment(obj);
		// } else if (ElementType.COMMENT_THREAD.getValue().equals(type)) {
		// 	commentThread = parseCommentThread(obj);
		} else {
			diagramItem = parseDiagram(obj, diff);
		}
	}

	private CommentDTO parseComment(JSONObject obj) {
		return new CommentDTO(JSONParserHelpers.getString(obj.get("text")),
												  JSONParserHelpers.getString(obj.get("elementType")),
												  JSONParserHelpers.getString(obj.get("shape")),
												  JSONParserHelpers.getExtension(obj.get(DiagramItemField.EXTENSION.getValue())),
												  JSONParserHelpers.getString(obj.get("backgroundColor")),
												  JSONParserHelpers.getString(obj.get("textColor")),
												  JSONParserHelpers.getInteger(obj.get("fsize")),
												  JSONParserHelpers.getInt(obj.get("version")),
												  JSONParserHelpers.getLong(obj.get("id")),
												  JSONParserHelpers.getString(obj.get("clientId")),
												  JSONParserHelpers.getString(obj.get("cd")),
												  JSONParserHelpers.getInt(obj.get(DiagramItemField.ROTATE_DEGREES.getValue())),
												  JSONParserHelpers.getDouble(obj.get("crc")),
												  JSONParserHelpers.getInt(obj.get("a")),
												  JSONParserHelpers.getInt(obj.get("r")),
												  JSONParserHelpers.getListUrl(obj.get("links")),
												  JSONParserHelpers.getString(obj.get("p")),
												  JSONParserHelpers.getString(obj.get("cby")),
												  JSONParserHelpers.getString(obj.get("cbyd")),
												  JSONParserHelpers.getLong(obj.get("cat")),
												  JSONParserHelpers.getLong(obj.get("uat")));
	}

	// this is a way to add new types
	// private CommentThreadDTO parseCommentThread(JSONObject obj) {
	// 	return new CommentThreadDTO(JSONParserHelpers.getString(obj.get("text")),
	// 													  JSONParserHelpers.getString(obj.get("elementType")),
	// 													  JSONParserHelpers.getString(obj.get("shape")),
	// 													  JSONParserHelpers.getString(obj.get("backgroundColor")),
	// 													  JSONParserHelpers.getString(obj.get("textColor")),
	// 													  JSONParserHelpers.getInt(obj.get("version")),
	// 													  JSONParserHelpers.getLong(obj.get("id")),
	// 													  JSONParserHelpers.getString(obj.get("clientId")),
	// 													  JSONParserHelpers.getString(obj.get("cd")),
	// 													  JSONParserHelpers.getDouble(obj.get("crc")),
	// 													  JSONParserHelpers.getInt(obj.get("a")),
	// 													  JSONParserHelpers.getInt(obj.get("r")));
	// }

	private DiagramItemDTO parseDiagram(JSONObject obj, boolean diff) {
    if (diff) {
      return new DiagramItemDTO(
        JSONParserHelpers.getStringOrNull(obj.get("text")),
        JSONParserHelpers.getStringOrNull(obj.get("elementType")),
        JSONParserHelpers.getStringOrNull(obj.get("shape")),
        JSONParserHelpers.getExtension(obj.get(DiagramItemField.EXTENSION.getValue())),
        JSONParserHelpers.getStringOrNull(obj.get("backgroundColor")),
        JSONParserHelpers.getStringOrNull(obj.get("textColor")),
        JSONParserHelpers.getInteger(obj.get("fsize")),
        JSONParserHelpers.getInteger(obj.get("props")),
        JSONParserHelpers.getInteger(obj.get("dord")),
        JSONParserHelpers.getInteger(obj.get("version")),
        JSONParserHelpers.getLongOrNull(obj.get("id")),
        JSONParserHelpers.getStringOrNull(obj.get("clientId")),
        JSONParserHelpers.getStringOrNull(obj.get("cd")),
        JSONParserHelpers.getInteger(obj.get(DiagramItemField.ROTATE_DEGREES.getValue())),														  
        JSONParserHelpers.getDoubleOrNull(obj.get("crc")),
        JSONParserHelpers.getStringOrNull(obj.get(DiagramItemField.GROUP.getValue())),
        JSONParserHelpers.getInteger(obj.get("a")),
        JSONParserHelpers.getInteger(obj.get("r")),
        JSONParserHelpers.getListUrl(obj.get("links")),
        JSONParserHelpers.getStringOrNull(obj.get("p")),
        JSONParserHelpers.getObjectOrNull(obj.get("data"))
      );
    }

		return new DiagramItemDTO(JSONParserHelpers.getString(obj.get("text")),
														  JSONParserHelpers.getString(obj.get("elementType")),
														  JSONParserHelpers.getString(obj.get("shape")),
														  JSONParserHelpers.getExtension(obj.get(DiagramItemField.EXTENSION.getValue())),
														  JSONParserHelpers.getString(obj.get("backgroundColor")),
														  JSONParserHelpers.getString(obj.get("textColor")),
														  JSONParserHelpers.getInteger(obj.get("fsize")),
														  JSONParserHelpers.getInt(obj.get("props")),
														  JSONParserHelpers.getInt(obj.get("dord")),
														  JSONParserHelpers.getInt(obj.get("version")),
														  JSONParserHelpers.getLong(obj.get("id")),
														  JSONParserHelpers.getString(obj.get("clientId")),
														  JSONParserHelpers.getString(obj.get("cd")),
												  		JSONParserHelpers.getInt(obj.get(DiagramItemField.ROTATE_DEGREES.getValue())),														  
														  JSONParserHelpers.getDouble(obj.get("crc")),
														  JSONParserHelpers.getString(obj.get(DiagramItemField.GROUP.getValue())),
														  JSONParserHelpers.getInt(obj.get("a")),
														  JSONParserHelpers.getInt(obj.get("r")),
														  JSONParserHelpers.getListUrl(obj.get("links")),
														  JSONParserHelpers.getStringOrNull(obj.get("p")),
														  JSONParserHelpers.getObjectOrNull(obj.get("data")));
	}

	public CommentDTO isComment() {
		return comment;
	}

	public DiagramItemDTO isDiagram() {
		return diagramItem;
	}

	public IDiagramItemRO isItem() {
		if (diagramItem != null && diagramItem.getType() != null && diagramItem.getType().length() > 0) {
      // fix bug where there are ghost items that doesn't have type
			return diagramItem;
		} else if (comment != null) {
			return comment;
		// } else if (commentThread != null) {
		// 	return commentThread;
		}
		return null;
	}

}
