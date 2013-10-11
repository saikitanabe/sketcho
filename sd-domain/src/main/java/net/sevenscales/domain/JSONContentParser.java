package net.sevenscales.domain;

import com.google.gwt.json.client.*;

import net.sevenscales.domain.utils.SLogger;

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
			for (int i = 0; i < items.size(); ++i) {
				JSONObject obj = items.get(i).isObject();
				if (obj != null) {
					JSONDiagramParser dp = new JSONDiagramParser(obj);
					if (dp.isItem() != null) {
						content.addItem(dp.isItem());
					}
				}
			}
		}
		// logger.debug("content.getVersion() {} content.getDiagramItems()", content.getVersion(), content.getDiagramItems());
	}

	public DiagramContentDTO toDTO() {
		return content;
	}
}