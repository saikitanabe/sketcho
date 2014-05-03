package net.sevenscales.domain;

import java.util.List;

import net.sevenscales.domain.utils.DiagramItemList;
import net.sevenscales.domain.utils.SLogger;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;


public class JSONContentParser {
	private static final SLogger logger = SLogger.createLogger(JSONContentParser.class);

	private DiagramContentDTO content;

	public JSONContentParser(String jsonContentStr) {
		content = new DiagramContentDTO();
		JSONValue jvalue = JSONParser.parseStrict(jsonContentStr);
		if (jvalue.isObject() != null) {
			parse(jvalue.isObject());
		}
	}

	private void parse(JSONObject jsonContent) {
		content.setVersion(JSONParserHelpers.getInt(jsonContent.get("version")));
		content.setName(JSONParserHelpers.getString(jsonContent.get("name")));
		content.setCreatedTime(JSONParserHelpers.getLong(jsonContent.get("createdAt")));
		content.setModifiedTime(JSONParserHelpers.getLong(jsonContent.get("updatedAt")));
		
		JSONArray items = jsonContent.get("items").isArray();
		if (items != null) {
			content.setDiagramItems(JSONContentParser.parseItems(items));
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

	public DiagramContentDTO toDTO() {
		return content;
	}
}