package net.sevenscales.sketcho.client.app.utils;

import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class ControllerAsyncCallback implements AsyncCallback {

	public IController controller;
	public ActivationObserver observer;

	public ControllerAsyncCallback(IController controller, ActivationObserver observer) {
		this.controller = controller;
		this.observer = observer;
	}

	public void activateController(DynamicParams params) {
		observer.activated(controller, params);
	}
}
