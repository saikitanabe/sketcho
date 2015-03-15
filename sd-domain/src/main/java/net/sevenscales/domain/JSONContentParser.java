package net.sevenscales.domain;

import java.util.List;

import net.sevenscales.domain.js.JsShape;
import net.sevenscales.domain.utils.DiagramItemList;
import net.sevenscales.domain.utils.SLogger;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.core.client.JsArray;


public class JSONContentParser {
	private static final SLogger logger = SLogger.createLogger(JSONContentParser.class);
	static {
		SLogger.addFilter(JSONContentParser.class);
	}

	private DiagramContentDTO content;

	public JSONContentParser(String jsonContentStr) {
		content = new DiagramContentDTO();
		JSONValue jvalue = JSONParser.parseStrict(jsonContentStr);
		if (jvalue.isObject() != null) {
			parse(jvalue.isObject());
		}
	}

	public JSONContentParser(JSONObject jsonContent) {
		content = new DiagramContentDTO();
		parse(jsonContent);
	}

	private void parse(JSONObject jsonContent) {
		logger.debug("jsonContent {}", jsonContent);
		content.setVersion(JSONParserHelpers.getInt(jsonContent.get("version")));
		content.setName(JSONParserHelpers.getString(jsonContent.get("name")));
		content.setCreatedTime(JSONParserHelpers.getLong(jsonContent.get("createdAt")));
		content.setModifiedTime(JSONParserHelpers.getLong(jsonContent.get("updatedAt")));
		content.setWidth(JSONParserHelpers.getInteger(jsonContent.get("width")));
		content.setDiagramProperties(JSONParserHelpers.getLong(jsonContent.get("properties")));

		JSONArray items = jsonContent.get("items").isArray();
		if (items != null) {
			content.setDiagramItems(JSONContentParser.parseItems(items));
		}

		JSONArray library = jsonContent.get("library").isArray();
		if (library != null) {
			content.setLibrary(JSONContentParser.parseLibrary(library));
		}

		// logger.debug("content.getVersion() {} content.getDiagramItems()", content.getVersion(), content.getDiagramItems());
	}

	public static List<IDiagramItemRO> parseItems(JSONArray items) {
		DiagramItemList result = new DiagramItemList();
		for (int i = 0; i < items.size(); ++i) {
			JSONObject obj = items.get(i).isObject();
			if (obj != null) {
				JSONDiagramParser dp = new JSONDiagramParser(obj);
				if (dp.isItem() != null) {
					result.add(dp.isItem());
				}
			}
		}
		return result;
	}

	public static JsArray<JsShape> parseLibrary(JSONArray library) {
		return library.getJavaScriptObject().cast();
	}

	public DiagramContentDTO toDTO() {
		return content;
	}
}