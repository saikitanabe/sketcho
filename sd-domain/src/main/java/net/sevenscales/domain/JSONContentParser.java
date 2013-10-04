package net.sevenscales.domain;

import com.google.gwt.json.client.*;

public class JSONContentParser {
	private DiagramContentDTO content;

	public JSONContentParser(String jsonContentStr) {
		content = new DiagramContentDTO();
		JSONValue jvalue = JSONParser.parseStrict(jsonContentStr);
		if (jvalue.isObject() != null) {
			parse(jvalue.isObject());
		}
	}

	private void parse(JSONObject jsonContent) {
		JSONArray items = jsonContent.get("items").isArray();
		if (items != null) {
			for (int i = 0; i < items.size(); ++i) {
				if (items.get(i).isObject() != null) {
					JSONDiagramParser dp = new JSONDiagramParser(items.get(i).isObject());
					if (dp.isItem() != null) {
						content.addItem(dp.isItem());
					}
				}
			}
		}
	}

	public DiagramContentDTO toDTO() {
		return content;
	}
}