package net.sevenscales.server.event;

import org.springframework.context.ApplicationEvent;

public class ContentUpdateEvent extends ApplicationEvent {
  public ContentUpdateEvent(Object source) {
    super(source);
  }

}
