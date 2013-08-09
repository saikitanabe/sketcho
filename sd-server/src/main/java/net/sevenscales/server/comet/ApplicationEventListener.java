package net.sevenscales.server.comet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationEventListener implements ApplicationListener {
  private List<ApplicationListener> listeners = new ArrayList<ApplicationListener>();

  public void onApplicationEvent(ApplicationEvent event) {
    for (ApplicationListener l : listeners) {
      l.onApplicationEvent(event);
    }
  }

  public void addListener(ApplicationListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

}
