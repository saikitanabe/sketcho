package net.sevenscales.appFrame.impl;


import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.Widget;


public abstract class ActionClick extends SimplePanel implements SourcesClickEvents {
	private int id;
	private IController controller;
	private Object data;
	private ClickListenerCollection listeners = new ClickListenerCollection();
	private Widget widget;

	public abstract void setName(String name);
	public abstract String getName();
	
	public ActionClick(ClickListener listener) {
		sinkEvents(Event.ONCLICK);
		addClickListener(listener);
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

	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
			case Event.ONCLICK: {
				listeners.fireClick(this);
				break;
			}
		}
	}
	
	public void addClickListener(ClickListener listener) {
		listeners.add(listener);
	}
	
	public void removeClickListener(ClickListener listener) {
		listeners.remove(listener);
	}
	
}
