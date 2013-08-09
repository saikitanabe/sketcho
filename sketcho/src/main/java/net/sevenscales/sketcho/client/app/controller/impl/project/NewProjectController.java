package net.sevenscales.sketcho.client.app.controller.impl.project;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.ControllerBase;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.Handler;
import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.sketcho.client.app.view.NewProjectView;

public class NewProjectController extends ControllerBase<Context> {
	private NewProjectView view;

	private NewProjectController(Context context) {
    super(context);	  
	}
	
	public static ClassInfo info() {
		return new ClassInfo() {
			public Object createInstance(Object data) {
				return new NewProjectController((Context) data);
			}

			public Object getId() {
				return NewProjectController.class;
			}
		};
	}

	public void activate(Map requests, ActivationObserver observer) {
		if (view == null) {
			view = new NewProjectView(this);
		}
		observer.activated(this, null);
	}
	
	public void activate(DynamicParams params) {
		if (view == null) {
			view = new NewProjectView(this);
		}
	}

	public View getView() {
		return view;
	}

	public Handler createHandlerById(Action action) {
		switch (action.getId()) {
			case ActionId.CREATE_PROJECT: {
				return new CreateProjectHandler(this);
			}
			case ActionId.CANCEL: {
				return new HandlerBase() {
					public void execute() {
						Map requests = new HashMap();
						requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
						RequestUtils.activate(requests);
					}
				};
			}
		}
		return null;
	}
}
