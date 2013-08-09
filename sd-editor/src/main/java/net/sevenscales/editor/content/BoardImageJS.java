package net.sevenscales.editor.content;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class BoardImageJS extends JavaScriptObject {
		protected BoardImageJS() {
		}
	  public final native String getName() /*-{ return this.name; }-*/;
	  public final native String getImageBase64() /*-{ return this.imageBase64; }-*/;
	  
	  public static JSONValue asJson(String boardName, String imageBase64) {
	  	JSONObject result = new JSONObject();
	  	result.put("name", new JSONString(boardName));
	  	result.put("imageBase64", new JSONString(imageBase64));
	  	return result;
	  }
	}