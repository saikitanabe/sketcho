package net.sevenscales.sketcho.client.app.controller;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.ControllerBase;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.Handler;
import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.appFrame.impl.Location;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.serverAPI.remote.ProjectRemote;
import net.sevenscales.sketcho.client.app.view.HierarchyView;
import net.sevenscales.sketcho.client.app.view.constants.ParamId;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class HierarchyController extends ControllerBase<Context> {

	private HierarchyView view;
	private IProject project;
	
  public static ClassInfo info() {
    return new ClassInfo() {
      public Object createInstance(Object data) {
        return new HierarchyController((Context) data);
      }

      public Object getId() {
        return HierarchyController.class;
      }
      
//      public boolean isGlobal() {
//        return true;
//      }
    };
  }
	
	private HierarchyController(Context context) {
	  super(context);
	}

	public void activate(Map requests, ActivationObserver observer) {
	  Context context = getContext();
//    observer.activated(this, null);
	  final ActivationObserver activationObserver = observer;
	  
	  if (context.getProjectId() != null) {
	    ProjectRemote.Util.inst.open(context.getProjectId(), new AsyncCallback<IProject>() {
	      public void onSuccess(IProject result) {
	        project = result;
//	        System.out.println("IProject opened: " + project.getName());
	        DynamicParams params = new DynamicParams();
	        params.addParam(ParamId.PROJECT_PARAM, project);
	        activationObserver.activated(HierarchyController.this, params);
	      }
	      public void onFailure(Throwable caught) {
	        UiNotifier.instance().showError("project open fail: " + caught);
	        System.out.println("IProject open failed");
	      }
	    });
	  } else {
      DynamicParams params = new DynamicParams();
      params.addParam(ParamId.PROJECT_PARAM, null);
      activationObserver.activated(HierarchyController.this, params);
	  }

	}

	public void activate(DynamicParams params) {
	}

	public Handler createHandlerById(Action action) {
    return null;
	}

	public View getView() {
	  if (view == null) {
	    view = new HierarchyView(this, new HierarchyView.IHierarchyViewListener() {
        public void newPage() {
          Map requests = new HashMap();
          requests.put(RequestId.CONTROLLER, RequestValue.NEW_PAGE_CONTROLLER);     
          requests.put(RequestId.PROJECT_ID, project.getId());
          
          String queries = Location.formatRequests(requests);
          History.newItem(queries);
        }

        public void organize() {
          Map requests = new HashMap<Object, Object>();
          requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_CONTROLLER);
          requests.put(RequestId.PROJECT_ID, getContext().getProjectId());
          String queries = Location.formatRequests(requests);
          History.newItem(queries);
        }
      });
	  }

	  return view;
	}
  
}
