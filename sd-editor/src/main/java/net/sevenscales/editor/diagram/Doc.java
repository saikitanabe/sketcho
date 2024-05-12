package net.sevenscales.editor.diagram;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.core.client.JavaScriptObject;

class Doc {

  public static native void fireGlobalEvent(
    String event
  )/*-{
    $doc.dispatchEvent(new CustomEvent("doc:" + event));
  }-*/;

  public static void fireGlobalEventWithData(
    String event,
    JSONObject data
  ) {
    Doc._fireGlobalEventWithData(
      event,
      data.getJavaScriptObject()
    );
  }
  
  public static native void _fireGlobalEventWithData(
    String event,
    JavaScriptObject data
  )/*-{
    $doc.dispatchEvent(new CustomEvent("doc:" + event, {
      detail: data
    }));
  }-*/;
  
}