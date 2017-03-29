package net.sevenscales.appFrame.impl;

import com.google.gwt.event.dom.client.ClickEvent;

import net.sevenscales.appFrame.api.IContext;


public abstract class ControllerBase<T extends IContext> implements IController<T> {
	private HandlerFactory handlerFactory;
	private Object data;
	private T context;
	private boolean visible;

	public ControllerBase(T context) {
	  this.context = context;
		handlerFactory = new HandlerFactory();
	}
	
	public void onClick(ClickEvent event) {
		Action action = (Action) event.getSource();
		
//		rootController.registerAction(action);
		Handler handler = handlerFactory.createHandler(action);
		if (handler != null) {
			handler.execute();
		}
	}

	public void setData(Object data) {
		this.data = data;
	}
	public Object getData() {
		return data;
	}
	
  public T getContext() {
    return (T) context;
  }
  
  // @Override
  public boolean isVisible() {
    return false;
  }

  // @Override
  public void setVisible(boolean visible) {
    this.visible = visible;
  }
}
