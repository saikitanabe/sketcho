package net.sevenscales.domain;

import net.sevenscales.domain.utils.JsonFormat;
import net.sevenscales.domain.utils.SLogger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class DiagramContentJS extends JavaScriptObject {
  private static SLogger logger = SLogger.createLogger(DiagramContentJS.class);
  
		protected DiagramContentJS() {
		}
	  public final native String getName() /*-{ return this.name; }-*/;
	  public final native String getCreatedAt() /*-{ return this.createdAt; }-*/;
	  public final native String getUpdatedAt() /*-{ return this.updatedAt; }-*/;
	  public final native Integer getVersion() /*-{ if (this.version) return this.version; else return -1; }-*/;
	  public final native int getEditorWidth() /*-{ if (this.editorWidth) return this.editorWidth; else return -1; }-*/;
	  public final native int getEditorHeight() /*-{ if (this.editorHeight) return this.editorHeight; else return -1;}-*/;
	  // public final native JsArray<DiagramItemJS> getItems() /*-{ return this.items; }-*/;
	  public final native String getSvg() /*-{ return this.svg; }-*/;
	  
		// public final DiagramContentDTO asDTO() {
		// 	DiagramContentDTO result = new DiagramContentDTO();
		// 	result.setName(getName());
		// 	result.setCreatedTime(Long.valueOf(getCreatedAt()));
		// 	result.setModifiedTime(Long.valueOf(getUpdatedAt()));
		// 	result.setVersion(getVersion());
		// 	result.setWidth(getEditorWidth());
		// 	result.setHeight(getEditorHeight());

		// 	JsArray<DiagramItemJS> items = getItems();
		// 	for (int i = 0; i < items.length(); ++i) {
		// 		DiagramItemDTO item = items.get(i).asDTO();
		// 		result.addItem(item);
		// 	}

		// 	return result;
		// }
			  
   // public static JSONValue asJson2(DiagramContentDTO content, JsonFormat jsonFormat) {
   //   JSONObject result = new JSONObject();
   //   result.put("name", new JSONString(content.getName()));
   //   // do not send createAt and updatedAt times
   //   // slightly misleading, even though server is not saving/using those
   //   result.put("editorWidth", new JSONNumber(content.getWidth()));
   //   result.put("editorHeight", new JSONNumber(content.getHeight()));
   //   result.put("createdAt", new JSONNumber(content.getCreatedTime()));
   //   result.put("updatedAt", new JSONNumber(content.getModifiedTime()));
      
   //   // need to have some special tricks with version...
   //   String version = String.valueOf(content.getVersion());
   //   result.put("version", new JSONNumber(Integer.valueOf(version)));
      
   //   JSONArray items = new JSONArray();
   //   int index = 0;
   //   for (IDiagramItemRO item : content.getDiagramItems()) {
   //     DiagramItemDTO dto = (DiagramItemDTO) item;
   //     items.set(index++, DiagramItemJS.asJson2(dto, jsonFormat));
   //   }
   //   result.put("items", items);
   //   return result;
   //  }
	}