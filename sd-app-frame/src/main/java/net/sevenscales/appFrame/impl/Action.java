package net.sevenscales.appFrame.impl;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


public abstract class Action extends SimplePanel 
    implements HasMouseDownHandlers, HasMouseUpHandlers, HasMouseMoveHandlers, HasMouseOutHandlers {
	private int id;
	private IController controller;
	private Object data;
//	private ClickListenerCollection listeners = new ClickListenerCollection();
	private Widget widget;
  private boolean hoover;

	public abstract void setName(String name);
	public abstract String getName();
	
	public Action() {
//		sinkEvents(Event.ONCLICK);
//		addClickListener(listener);
	}

	public final void initWidget(Widget widget) {
		this.widget = widget;
		setWidget(widget);
	}

	public final Widget getWidget() {
		return this;
	}

	public final void setId(int id) {
		this.id = id;
	}
	public final int getId() {
		return this.id;
	}
	
	public void setController(IController controller) {
		this.controller = controller;
	}
	public IController getController() {
		return this.controller;
	}
	
	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
		return this.data;
	}
  public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
    return addDomHandler(clickHandler, ClickEvent.getType());
  }
  
  public HandlerRegistration addMouseOverHandler(MouseOverHandler overHandler) {
    return addDomHandler(overHandler, MouseOverEvent.getType());
  }

  public HandlerRegistration addMouseOutHandler(MouseOutHandler overHandler) {
    return addDomHandler(overHandler, MouseOutEvent.getType());
  }

  public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
    return addDomHandler(handler, KeyDownEvent.getType());
  }
  
  public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
    return addDomHandler(handler, MouseDownEvent.getType());
  }
  public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
    return addDomHandler(handler, MouseUpEvent.getType());
  }
  public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
    return addDomHandler(handler, MouseMoveEvent.getType());
  }


  public void setHover(boolean hoover) {
    this.hoover = hoover;
  }
  public boolean isHover() {
    return hoover;
  }

//	public void onBrowserEvent(Event event) {
//		switch (DOM.eventGetType(event)) {
//			case Event.ONCLICK: {
//			  fireEvent(event)
//				listeners.fireClick(this);
//				break;
//			}
//		}
//	}
	
//	public void addClickListener(ClickListener listener) {
//		listeners.add(listener);
//	}
//	
//	public void removeClickListener(ClickListener listener) {
//		listeners.remove(listener);
//	}
	
}
