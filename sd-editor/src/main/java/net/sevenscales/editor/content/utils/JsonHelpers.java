package net.sevenscales.editor.content.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.JsonConversion;
import net.sevenscales.domain.utils.JsonFormat;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.json.JsonExtraction;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;

public class JsonHelpers {
	private static final SLogger logger = SLogger.createLogger(JsonHelpers.class);
	
	public static class MisMatchException extends Exception {
    private String logicalName;
		private double checksum;
		private String json;

		public MisMatchException(String logicalName, double checksum, String json) {
		  this.logicalName = logicalName;
			this.checksum = checksum;
			this.json = json;
		}
		
		public String getLogicalName() {
      return logicalName;
    }

		public double checksum() {
			return checksum;
		}
		
		public String getJson() {
			return json;
		}
	}
	
	public JsonHelpers(ISurfaceHandler surface) {
	}

	public void verify(String logicalName, List<? extends IDiagramItemRO> list, double checksum, JsonFormat jsonFormat) throws MisMatchException {
		String json = JsonExtraction.jsonStringify(list);
		double boardChecksum = ChecksumHelpers.crc32(json);
		if (boardChecksum != checksum) {
			logger.error("Logical name {}, CLIENT JSON: {}", logicalName, json);
			throw new MisMatchException(logicalName, boardChecksum, json);
		}
	}
	
	public static String removeLastComma(String json) {
		if (json.length() > 0) {
			// send doesn't allow last comma in the end => remove 
			json = json.substring(0, json.length() - 1);
		}
		return json;
	}

}
