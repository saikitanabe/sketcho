package net.sevenscales.editor.api.ot;

import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.json.JsonExtraction;

public class JSONPack {

  public static JSONArray diff(
    List<? extends IDiagramItemRO> newItems,
    List<? extends IDiagramItemRO> oldItems
  ) {
    JSONArray newArray = JsonExtraction.decompose(newItems);
    JSONArray oldArray = JsonExtraction.decompose(oldItems);
    JavaScriptObject diffed = diffDiagramItems(newArray.getJavaScriptObject(), oldArray.getJavaScriptObject());
    return new JSONArray(diffed);
  }

  private static native JavaScriptObject diffDiagramItems(
    JavaScriptObject newItems,
    JavaScriptObject oldItems
  )/*-{
    return $wnd.diffDiagramItems(newItems, oldItems)
  }-*/;

  public static JSONArray packOperation(OTOperation operation, JSONArray operationJson) {
    JavaScriptObject result = packOperation(operation.getValue(), operationJson.getJavaScriptObject());
    return new JSONArray(result);
  }

  private static native JavaScriptObject packOperation(String operation, JavaScriptObject operationJson)/*-{
    return $wnd.packOTOperation(operation, operationJson)
  }-*/;

}