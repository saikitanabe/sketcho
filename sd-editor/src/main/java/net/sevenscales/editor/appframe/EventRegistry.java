package net.sevenscales.editor.appframe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;


public class EventRegistry {
  private Map<Integer, Set<IRegistryEventObserver>> entries;
  private Map<Integer, IService> services;
  private Panel dropPanel;
  private DragControllerRegistration dragControllerRegistration;
  
  private class GenericCallback implements AsyncCallback {
    private AsyncCallback callback;
    private Integer event;

    public GenericCallback(Integer event, AsyncCallback asyncCallback) {
      this.callback = asyncCallback;
      this.event = event;
    }
    // @Override
    public void onSuccess(Object result) {
      callback.onSuccess(result);
      for (IRegistryEventObserver o : entries.get(event)) {
        o.handleEvent(event, result);
      }
      
      // need to run deferred so that onSuccess
      // can set e.g. cookies and those will be
      // then effective in o.handleEvent
//      DeferredCommand.addCommand(new Command() {
//        // @Override
//        public void execute() {
//          for (IRegistryEventObserver o : entries.get(event)) {
//            o.handleEvent(event);
//          }
//        }
//      });
    }
    //@Override
    public void onFailure(Throwable caught) {
      callback.onFailure(caught);
    }
  }
  
  public interface IService {
    public void execute();
  }

  public EventRegistry() {
    entries = new HashMap<Integer, Set<IRegistryEventObserver>>();
    services = new HashMap<Integer, IService>();
  }
  
  public void register(Integer event, IRegistryEventObserver eventObserver) {
    Set<IRegistryEventObserver> observers = entries.get(event);
    if (observers == null) {
      observers = new HashSet<IRegistryEventObserver>();
      entries.put(event, observers);
    }
    
    if (!observers.contains(eventObserver)) {
      observers.add(eventObserver);
    }
  }

  @SuppressWarnings("unchecked")
  public AsyncCallback getHandler(Integer event,
      AsyncCallback asyncCallback) {
    return new GenericCallback(event, asyncCallback);
  }

  public void handleEvent(Integer eventId, Object data) {
    if (entries.get(eventId) != null) {
      for (IRegistryEventObserver obs : entries.get(eventId)) {
        obs.handleEvent(eventId, data);
      }
    }
  }

  public void registerService(Integer event, IService service) {
    services.put(event, service);
  }

  public void callService(Integer event, Object data) {
    IService s = services.get(event);
    if (s != null) {
      s.execute();
    }
  }

  public void registerLabelsDropTarget(Panel dropPanel) {
    // lazy for not implementing id for labels and having this generic...
    this.dropPanel = dropPanel;
    if (dragControllerRegistration != null) {
      dragControllerRegistration.register(dropPanel);
    }
  }

  public void registerLabelsDragController(DragControllerRegistration dragControllerRegistration) {
    this.dragControllerRegistration = dragControllerRegistration;
    if (dropPanel != null) {
      dragControllerRegistration.register(dropPanel);
    }
  }

}
