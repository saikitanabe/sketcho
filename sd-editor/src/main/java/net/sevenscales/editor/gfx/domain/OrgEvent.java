package net.sevenscales.editor.gfx.domain;

import com.google.gwt.dom.client.NativeEvent;

public class OrgEvent {
  private NativeEvent nativeEvent;

public OrgEvent(NativeEvent nativeEvent) {
    this.nativeEvent = nativeEvent;
  }

  public double getTimeStamp() {
    return _getTimeStamp(nativeEvent);
  }

  private native double _getTimeStamp(NativeEvent event)/*-{
    return event.timeStamp ? event.timeStamp : 0
  }-*/;
}