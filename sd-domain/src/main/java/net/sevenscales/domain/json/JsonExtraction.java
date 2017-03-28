package net.sevenscales.domain.json;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import net.sevenscales.domain.CommentDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.DiagramItemField;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.IPathRO;
import net.sevenscales.domain.ISvgDataRO;
import net.sevenscales.domain.IUrlLinkRO;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.utils.JsonFormat;


public class JsonExtraction {
	public static JSONValue decompose(IDiagramContent content, JsonFormat jsonFormat) {
		JSONObject result = new JSONObject();
		result.put("name", new JSONString(content.getName()));
		// do not send createAt and updatedAt times
		// slightly misleading, even though server is not saving/using those
		result.put("editorWidth", new JSONNumber(content.getWidth()));
		result.put("editorHeight", new JSONNumber(content.getHeight()));
		result.put("createdAt", new JSONNumber(content.getCreatedTime()));
		result.put("updatedAt", new JSONNumber(content.getModifiedTime()));

		// need to have some special tricks with version...
		String version = String.valueOf(content.getVersion());
		result.put("version", new JSONNumber(Integer.valueOf(content.getVersion())));

		JSONArray items = new JSONArray();
		int index = 0;
		for (IDiagramItemRO item : content.getDiagramItems()) {
		 items.set(index++, decompose(item, jsonFormat));
		}
		result.put("items", items);
		return result;
	}

	public static JSONValue decompose(IDiagramItemRO item, JsonFormat jsonFormat) {
    // NOTE that this needs to be leaf first order!
    // otherwise DiagramItemDTO catches all!
		if (item instanceof CommentDTO) {
      return decompose((CommentDTO) item, jsonFormat);
    } else if (item instanceof DiagramItemDTO) {
			return decompose((DiagramItemDTO) item, jsonFormat);
		}
		return null;
	}

	public static JSONValue decompose(CommentDTO comment, JsonFormat jsonFormat) {
		JSONValue result = decompose((DiagramItemDTO) comment, jsonFormat);
		JSONObject obj = result.isObject();
		if (obj != null) {
    	obj.put("cby", new JSONString(safeJsonString(comment.getUsername())));
    	obj.put("cbyd", new JSONString(safeJsonString(comment.getUserDisplayName())));
    }
		return result;
	}

	public static JSONObject decompose(IDiagramItemRO item) {
    JSONObject result = new JSONObject();
    String text = item.getText();
    result.put("text", new JSONString(text));
    result.put("elementType", new JSONString(safeJsonString(item.getType())));
    result.put("shape", new JSONString(safeJsonString(validateShape(item.getShape()))));
    if (item.getExtension() != null) {
      JSONObject ext = decomposeExtension(item);
      result.put(DiagramItemField.EXTENSION.getValue(), ext);
    }
    result.put("backgroundColor", new JSONString(safeJsonString(item.getBackgroundColor())));
    result.put("textColor", new JSONString(safeJsonString(item.getTextColor())));
    if (item.getFontSize() != null) {
        result.put("fsize", new JSONNumber(item.getFontSize()));
    }
    if (item.getShapeProperties() != null && item.getShapeProperties() > 0) {
      // 0 is default and is omitted
      result.put("props", new JSONNumber(item.getShapeProperties()));
    }
    if (item.getDisplayOrder() != null && item.getDisplayOrder() != 0) {
      // 0 is the default display order and null means the same
      // value can be - as well to be on bottom
      result.put("dord", new JSONNumber(item.getDisplayOrder()));
    }
    result.put("version", new JSONNumber(item.getVersion()));
    result.put("id", new JSONNumber(item.getId()));
    result.put("clientId", new JSONString(safeJsonString(item.getClientId())));
    result.put("cd", new JSONString(safeJsonString(item.getCustomData())));
    result.put("crc", new JSONNumber(item.getCrc32()));

    if (item.getCreatedAt() != null) {
      result.put(DiagramItemField.CREATED_AT.getValue(), new JSONNumber(item.getCreatedAt()));
    }
    if (item.getUpdatedAt() != null) {
      result.put(DiagramItemField.UPDATED_AT.getValue(), new JSONNumber(item.getUpdatedAt()));
    }

    if (item.isGroup()) {
      // 0 is default and is omitted
      result.put(DiagramItemField.GROUP.getValue(), new JSONString(item.getGroup()));
    }

    if (item.getData() != null) {
      result.put(DiagramItemField.DATA.getValue(), new JSONObject(item.getData()));
    }

    if (item.isAnnotation()) {
	    result.put("a", new JSONNumber(item.getAnnotation()));
    }
    if (item.isResolved()) {
	    result.put("r", new JSONNumber(item.getResolved()));
    }
    if (item.getLinks() != null && item.getLinks().size() > 0) {
    	JSONArray jlinks = new JSONArray();
    	List<? extends IUrlLinkRO> links = item.getLinks();
    	for (int i = 0; i < links.size(); ++i) {
    		JSONObject jlink = new JSONObject();
    		jlink.put("url", new JSONString(links.get(i).getUrl()));
    		if (links.get(i).getName() != null) {
	    		jlink.put("name", new JSONString(links.get(i).getName()));
    		}
    		jlinks.set(i, jlink);
    	}
    	result.put("links", jlinks);
    }

    if (item.getParentId() != null && !"".equals(item.getParentId())) {
      result.put("p", new JSONString(item.getParentId()));
    }

    return result;
	}

  public static JSONArray decompose(List<? extends IDiagramItemRO> items) {
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

  public static String jsonStringify(List<? extends IDiagramItemRO> items) {
    return stringify(JsonExtraction.decompose(items).getJavaScriptObject());
  }

  public static String stringify(JSONObject jobject) {
    return JsonExtraction.stringify(jobject.getJavaScriptObject());
  }

  public static native String stringify(JavaScriptObject json)/*-{
    return JSON.stringify(json)
  }-*/;

  private static String validateShape(String shape) {
    if (shape == null || (shape != null && "".equals(shape))) {
      // let's not check empty shape, that is on purpose, like for undo
      return shape;
    }
    // sequence shape has bug of having space as separator
    String[] numbers = shape.replaceAll(" ", ",").split(",");
    for (String number : numbers) {
      if (Double.valueOf(number).isNaN()) {
        throw new RuntimeException("Bad number " + number);
      }
    }
    return shape;
  }

  /**
  * Handle each extension value in here.
  */
  private static JSONObject decomposeExtension(IDiagramItemRO item) {
    JSONObject result = new JSONObject();
    if (item.getExtension().getSvgData() != null) {
      JSONObject jsvgdata = new JSONObject();
      ISvgDataRO svgdataro = item.getExtension().getSvgData();
      JSONArray paths = getJsonPaths(svgdataro);
      jsvgdata.put(DiagramItemField.PATHS.getValue(), paths);
      jsvgdata.put(DiagramItemField.SVG_WIDTH.getValue(), new JSONNumber(svgdataro.getWidth()));
      jsvgdata.put(DiagramItemField.SVG_HEIGHT.getValue(), new JSONNumber(svgdataro.getHeight()));
      result.put(DiagramItemField.SVG_DATA.getValue(), jsvgdata);
    }

    if (item.getExtension().getLineWeight() != null) {
      result.put(DiagramItemField.LINE_WEIGHT.getValue(), new JSONNumber(item.getExtension().getLineWeight()));
    }

    return result;
  }

  private static JSONArray getJsonPaths(ISvgDataRO svgdataro) {
    JSONArray result = new JSONArray();
    for (int i = 0; i < svgdataro.getPaths().size(); ++i) {
      IPathRO path = svgdataro.getPaths().get(i);
      JSONObject jpath = new JSONObject();
      jpath.put(DiagramItemField.PATH.getValue(), new JSONString(path.getPath()));
      if (path.getStyle() != null && !"".equals(path.getStyle())) {
        jpath.put(DiagramItemField.STYLE.getValue(), new JSONString(path.getStyle()));
      }
      result.set(i, jpath);
    }
    return result;
  }

	public static  JSONValue toJson(IDiagramContent content, JsonFormat jsonFormat) {
		return decompose(content, jsonFormat);
	}

  private static String itemText(IDiagramItemRO item, JsonFormat jsonFormat) {
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
  
  public static String escapeForSending(String value) {
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
	
	protected static String safeJsonString(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}

}