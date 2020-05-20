package net.sevenscales.editor.api.ot;

import com.google.gwt.core.client.JavaScriptObject;

public class JsOperationQueueStorageItem extends JavaScriptObject {

  protected JsOperationQueueStorageItem() {
  }

  public final native String getTabId()/*-{
    return this.tab_id
  }-*/;

  public final native JavaScriptObject getOperations()/*-{
    return this.operations
  }-*/;
}