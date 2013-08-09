package net.sevenscales.sketcho.client.app.controller;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.ControllerBase;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.Handler;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.sketcho.client.app.view.MenuView;
import net.sevenscales.sketcho.client.app.view.MenuView.IMenuViewListener;

public class MenuController extends ControllerBase<Context> {

	private MenuView view;
	private IProject project;
	
  public static ClassInfo info() {
    return new ClassInfo() {
      public Object createInstance(Object data) {
        return new MenuController((Context) data);
      }

      public Object getId() {
        return MenuController.class;
      }
      
//      public boolean isGlobal() {
//        return true;
//      }
    };
  }
	
	private MenuController(Context context) {
	  super(context);
	}

	public void activate(Map requests, ActivationObserver observer) {
    observer.activated(this, null);
	}

	public void activate(DynamicParams params) {
	}

	public Handler createHandlerById(Action action) {
    return null;
	}

	public View getView() {
	  if (view == null) {
	    view = new MenuView(this, new IMenuViewListener() {
	      public void newSketch() {
          Map<Object, String> requests = new HashMap<Object, String>();
          requests.put(RequestId.CONTROLLER, RequestValue.SKETCH_CONTROLLER);
          requests.put(RequestId.PROJECT_ID, String.valueOf(getContext().getProjectId()));
          requests.put(RequestId.ACTION_ID, String.valueOf(ActionId.NEW_PAGE));
          RequestUtils.activate(requests);
	      }
      });
	  }

	  return view;
	}
  
}
