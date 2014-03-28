package net.sevenscales.domain.json;

import java.util.List;

import com.google.gwt.json.client.*;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.IUrlLinkRO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.CommentDTO;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.utils.JsonFormat;
import net.sevenscales.domain.DiagramItemField;


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
    	obj.put("p", new JSONString(safeJsonString(comment.getParentThreadId())));
    	obj.put("cby", new JSONString(safeJsonString(comment.getUsername())));
    	obj.put("cbyd", new JSONString(safeJsonString(comment.getUserDisplayName())));
    	obj.put("cat", new JSONNumber(comment.getCreatedAt()));
    	obj.put("uat", new JSONNumber(comment.getUpdatedAt()));
    }
		return result;
	}

	public static JSONValue decompose(DiagramItemDTO item, JsonFormat jsonFormat) {
    JSONObject result = new JSONObject();
    String text = safeJsonString(itemText(item, jsonFormat));
    result.put("text", new JSONString(text));
    result.put("elementType", new JSONString(safeJsonString(item.getType())));
    result.put("shape", new JSONString(safeJsonString(item.getShape())));
    if (item.getSvg() != null) {
      result.put(DiagramItemField.SVG.getValue(), new JSONString(item.getSvg()));
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
	
	protected static String safeJsonString(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}

}