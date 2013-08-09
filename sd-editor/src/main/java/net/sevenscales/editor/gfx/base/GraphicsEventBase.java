package net.sevenscales.editor.gfx.base;
public abstract class GraphicsEventBase {
  protected GraphicsEventBase() {}
  public static class Type <H extends GraphicsEventHandler> {
    private String eventName;
    private GraphicsEventType<H> event;

    public String getEventName() {
      return eventName;
    }

    public Type(String eventName, GraphicsEventType<H> event) {
      this.eventName = eventName;
      this.event = event;
    }
  }
}
