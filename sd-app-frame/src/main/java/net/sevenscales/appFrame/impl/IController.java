package net.sevenscales.appFrame.impl;

import java.util.Map;

import net.sevenscales.appFrame.api.IContext;

import com.google.gwt.event.dom.client.ClickHandler;

public interface IController<T extends IContext> extends ClickHandler {

	public View getView();
	
	/**
	 * Loads requested parameters.
	 * @param requests
	 */
	public void activate(Map requests, ActivationObserver observer);
	public void activate(DynamicParams params);
	public Handler createHandlerById(Action action);
	
  public T getContext();
  public boolean isVisible();
  public void setVisible(boolean visible);
}
