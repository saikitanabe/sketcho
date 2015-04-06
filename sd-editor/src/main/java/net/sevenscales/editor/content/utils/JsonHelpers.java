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
// import net.sevenscales.editor.api.ot.BoardDocumentGraphicalViewHelpers;
// import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.editor.diagram.Diagram;

public class JsonHelpers {
	private static final SLogger logger = SLogger.createLogger(JsonHelpers.class);
	// private BoardDocumentGraphicalViewHelpers boardDocument;
	
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
		// boardDocument = new BoardDocumentGraphicalViewHelpers(surface);
	}

	public void verify(String logicalName, List<? extends IDiagramItemRO> list, double checksum, JsonFormat jsonFormat) throws MisMatchException {
		// String json = toJson(list, jsonFormat);
		String json = jsonStringify(list);
		double boardChecksum = ChecksumHelpers.crc32(json);
		if (boardChecksum != checksum) {
			logger.error("Logical name {}, CLIENT JSON: {}", logicalName, json);
			throw new MisMatchException(logicalName, boardChecksum, json);
		}
	}
	
	// public static String toJson(List<? extends IDiagramItemRO> items, JsonFormat jsonFormat) {
	// 	String json = "";
		
	// 	for (IDiagramItemRO di : items) {
	// 		// server has in saved format only one \n and also " without 
	// 		// e.g. \\n \"
	// 		json += JsonExtraction.decompose(di, jsonFormat).toString() + ",";
	// 	}
		
	// 	json = removeLastComma(json);
	// 	return "[" + json + "]";
	// }

	// public JsonConversion json(List<Diagram> diagrams, boolean updateDiagramItem, JsonFormat jsonFormat) {
	// 	List<IDiagramItem> items = BoardDocumentHelpers.getDiagramsAsDTO(diagrams, updateDiagramItem);
	// 	String json = jsonStr(items, jsonFormat);
	// 	JsonConversion result = new JsonConversion(json, jsonFormat, items);
	// 	return result;
	// }

	// public String jsonStr(List<? extends IDiagramItemRO> items, JsonFormat jsonFormat) {
	// 	String json = "";
	// 	boolean first = true;
	// 	for (IDiagramItemRO di : items) {
	// 	  if (first) {
	// 	    first = false;
	// 	  } else {
	// 	    json += ",";
	// 	  }
	// 	  if (di.getClientId() != null && !"".equals(di.getClientId())) {
	// 	  	// prevent bugs in the ui to send server ghost elements!!!
	// 			json += JsonExtraction.decompose(di, jsonFormat).toString();
	// 	  }
	// 	}
	// 	return "[" + json + "]";
	// }

	public static String jsonStringify(List<? extends IDiagramItemRO> items) {
		return stringify(json(items).getJavaScriptObject());
	}

	private static native String stringify(JavaScriptObject json)/*-{
		return JSON.stringify(json)
	}-*/;

	public static JSONArray json(List<? extends IDiagramItemRO> items) {
		JSONArray result = new JSONArray();
		JsArray<JavaScriptObject> array = result.getJavaScriptObject().cast();
		for (IDiagramItemRO di : items) {
		  if (di.getClientId() != null && !"".equals(di.getClientId())) {
		  	// prevent bugs in the ui to send server ghost elements!!!
				JSONObject json = JsonExtraction.decompose(di);
				array.push(json.getJavaScriptObject());
		  }
		}
		return result;
	}

	// public JsonConversion json(Diagram[] diagrams, JsonFormat jsonFormat) {
	// public JsonConversion json(Diagram[] diagrams) {
	// 	List<Diagram> list = new ArrayList<Diagram>();
	// 	for (Diagram d : diagrams) {
	// 		list.add(d);
	// 	}
	// 	// return json(list, false, jsonFormat);
	// 	return json(list);
	// }
	
	// public static String json(List<IDiagramItemRO> items, JsonFormat jsonFormat) {
	// 	String result = "";
	// 	for (IDiagramItemRO di : items) {
	// 		if (di instanceof DiagramItemDTO) {
	// 		}
	// 	}
	// 	result = removeLastComma(result);
	// 	return "[" + result + "]";
	// }
	
	public static String removeLastComma(String json) {
		if (json.length() > 0) {
			// send doesn't allow last comma in the end => remove 
			json = json.substring(0, json.length() - 1);
		}
		return json;
	}

}
