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
import net.sevenscales.sketcho.client.app.view.DefaultFooterView;

public class DefaultFooterController extends ControllerBase<Context> {
	private DefaultFooterView view;
	
	private DefaultFooterController(Context context) {
	  super(context);
	}
	
	public static ClassInfo info() {
		return new ClassInfo() {
			public Object createInstance(Object data) {
				return new DefaultFooterController((Context) data);
			}

			public Object getId() {
				return DefaultFooterController.class;
			}
		};
	}

	public void activate(Map requests, ActivationObserver observer) {
		if (view == null) {
			view = new DefaultFooterView(this);
		}
    observer.activated(this, null);
	}

	public void activate(DynamicParams params) {
		if (view == null) {
			view = new DefaultFooterView(this);
		}
	}

	public Handler createHandlerById(Action action) {
		return null;
	}

	public View getView() {
		return view;
	}

}
