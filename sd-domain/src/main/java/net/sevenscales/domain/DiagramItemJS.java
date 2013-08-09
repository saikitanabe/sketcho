package net.sevenscales.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.JsonFormat;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class DiagramItemJS extends JavaScriptObject implements IDiagramItemRO {                              // [1]
	  // Overlay types always have protected, zero argument constructors.
	  protected DiagramItemJS() {}                                              // [2]

	  // JSNI methods to get data
	  public final native String getText() /*-{ return this.text; }-*/; // [3]
	  public final native String getElementType() /*-{ return this.elementType; }-*/;
	  public final native String getShape() /*-{ return this.shape; }-*/;
	  public final native String getBackgroundColor() /*-{ return this.backgroundColor; }-*/;
	  public final native String getTextColor() /*-{ return this.textColor; }-*/;
	  public final native int _getVersion() /*-{ return this.version; }-*/;
	  public final native int _getId() /*-{ return this.id; }-*/;
	  public final native String getClientId() /*-{ return this.clientId; }-*/;
	  public final native String getCustomData() /*-{ return this.cd; }-*/;
	  
	  public final DiagramItemDTO asDTO() {
	  	return new DiagramItemDTO(getText(), 
	  														getElementType(), 
	  														getShape(), 
	  														getBackgroundColor(), 
	  														getTextColor(), 
	  														getVersion(), 
	  														new Long(getId()), 
	  														getClientId(),
	  														getCustomData());
	  }

	  private static class JSONObjectFormatter extends JSONObject {
//	  	@Override
//	    public String toString() {
//	      return StringUtil.trimSpaces(super.toString());
//	    }
	    @Override
	    public String toString() {
	      StringBuffer sb = new StringBuffer();
	      sb.append("{");
	      boolean first = true;
	      String[] keys = computeKeys();
	      for (String key : keys) {
	        if (first) {
	          first = false;
	        } else {
	          sb.append(",");
	        }
	        sb.append(JsonUtils.escapeValue(key));
	        sb.append(":");
	        sb.append(get(key));
	      }
	      sb.append("}");
	      return sb.toString();
	    }
	    
	    private native void addAllKeys(Collection<String> s) /*-{
  	    var jsObject = this.@com.google.gwt.json.client.JSONObject::jsObject;
  	    for (var key in jsObject) {
  	      if (jsObject.hasOwnProperty(key)) {
  	        s.@java.util.Collection::add(Ljava/lang/Object;)(key);
  	      }
  	    }
  	  }-*/;

	    private String[] computeKeys() {
	      if (GWT.isScript()) {
	        return computeKeys0(new String[0]);
	      } else {
	        List<String> result = new ArrayList<String>();
	        addAllKeys(result);
	        return result.toArray(new String[result.size()]);
	      }
	    }

	    private native String[] computeKeys0(String[] result) /*-{
	      var jsObject = this.@com.google.gwt.json.client.JSONObject::jsObject;
	      var i = 0;
	      for (var key in jsObject) {
	        if (jsObject.hasOwnProperty(key)) {
	          result[i++] = key;
	        }
	      }
	      return result;
	    }-*/;

	  }
	  
		private static class StringImpl extends JSONString {

      public StringImpl(String value) {
        super(value);
      }
		  
      @Override
      public String toString() {
        return stringValue();
      }
		}
		
    public static JSONValue asJson2(DiagramItemDTO item, JsonFormat jsonFormat) {
      JSONObject result = new JSONObjectFormatter();
      String text = safeJsonString(itemText(item, jsonFormat));
      result.put("text", new StringImpl(text));
      result.put("elementType", new JSONString(safeJsonString(item.getType())));
      result.put("shape", new JSONString(safeJsonString(item.getShape())));
      result.put("backgroundColor", new JSONString(safeJsonString(item.getBackgroundColor())));
      result.put("textColor", new JSONString(safeJsonString(item.getTextColor())));
      result.put("version", new JSONNumber(item.getVersion()));
      result.put("id", new JSONNumber(item.getId()));
      result.put("clientId", new JSONString(safeJsonString(item.getClientId())));
      result.put("cd", new JSONString(safeJsonString(item.getCustomData())));
      return result;
    }


    private static String itemText(DiagramItemDTO item, JsonFormat jsonFormat) {
      String result = item.getText();
      result = result != null ? result : "";
      switch (jsonFormat) {
      case SEND_FORMAT:
        result = escapeForSending(result);
        break;
      case PRESENTATION_FORMAT:
        break;
      case SERVER_FORMAT:
        result = escapeForServerFormat(result);
        break;
      }
      return result;
		}
    
    protected static String escapeForSending(String value) {
      return value
                  .replaceAll("\\\\", "\\\\\\\\")
                  .replaceAll("\\n", "\\\\\\\n")
                  .replaceAll("\"", "\\\\\\\"");
    }
    
    protected static String escapeForServerFormat(String value) {
      return value;
//                  .replaceAll("\\\\", "\\\\\\")
//                  .replaceAll("\\n", "\\\\n");
//                  .replaceAll("\"", "\\\"");
    }
		
		private static String safeJsonString(String value) {
			if (value == null) {
				return "";
			}
			return value;
		}

		@Override
		public final Long getId() {
			return new Long(_getId());
		}

		@Override
		public final String getType() {
			return getElementType();
		}

		@Override
		public final Integer getVersion() {
			return new Integer(_getVersion());
		}

		@Override
		public final IDiagramItem copy() {
			return null;
		}
		
		@Override
		public final void copyFrom(IDiagramItemRO item) {
		}
	  
	}