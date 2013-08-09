package net.sevenscales.sketcho.client.app.controller;

import java.util.Map;

import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.ControllerBase;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.Handler;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.sketcho.client.app.view.TopNavigationView;


public class TopNavigationController extends ControllerBase<Context> {
	private TopNavigationView view;

	private TopNavigationController(Context context) {
    super(context);
	}
	
	public static ClassInfo info() {
		return new ClassInfo() {
			public Object createInstance(Object data) {
				return new TopNavigationController((Context) data);
			}

			public Object getId() {
				return TopNavigationController.class;
			}
		};
	}
	
	public void activate(Map requests, ActivationObserver observer) {
		if (view == null) {
			view = new TopNavigationView(this);
		}
		observer.activated(this, null);
	}
	
	public void activate(DynamicParams params) {
		if (view == null) {
			view = new TopNavigationView(this);
		}
	}

	public Handler createHandlerById(Action action) {
		return null;
	}

	public View getView() {
		return view;
	}

}
