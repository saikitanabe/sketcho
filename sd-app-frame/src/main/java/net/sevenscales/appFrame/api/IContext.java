package net.sevenscales.appFrame.api;

import net.sevenscales.appFrame.impl.EventRegistry;

public interface IContext {
  public EventRegistry getEventRegistry();
  public void setEventRegistry(EventRegistry eventRegistry);
}
