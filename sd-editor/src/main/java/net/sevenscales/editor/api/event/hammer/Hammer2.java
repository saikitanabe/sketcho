package net.sevenscales.editor.api.event.hammer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

public class Hammer2 {

  private Element elem;
  private Hammer2TapEventHandler handler;

  public Hammer2(Element elem) {
    this.elem = elem;
  }

  public void on(String event, Hammer2TapEventHandler handler) {
    this.handler = handler;
    initHammer(this, this.elem, event);
  }

  private native void initHammer(Hammer2 me, Element elem, String event)/*-{
    $wnd.Hammer2(elem).on(event, function(evt) {
      me.@net.sevenscales.editor.api.event.hammer.Hammer2::onEvent(Lcom/google/gwt/user/client/Event;)(evt)
    })
  }-*/;

  private void onEvent(Event event) {
    handler.onHammerTap(event);
  }
}
