package net.sevenscales.sketcho.client.app.controller;

import java.util.HashMap;
import java.util.List;
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
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.serverAPI.remote.ProjectRemote;
import net.sevenscales.sketcho.client.app.controller.impl.projects.DeleteProjectActionHandler;
import net.sevenscales.sketcho.client.app.controller.impl.projects.NewProjectActionHandler;
import net.sevenscales.sketcho.client.app.controller.impl.projects.OpenProjectActionHandler;
import net.sevenscales.sketcho.client.app.view.ProjectsView;
import net.sevenscales.sketcho.client.app.view.constants.ParamId;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ProjectsController extends ControllerBase<Context> {
	private ProjectsView view;
	
	private ProjectsController(Context context) {
	  super(context);
		view = new ProjectsView(this);
		view.addClickHandler(this);
	}
	
	public static ClassInfo info() {
		return new ClassInfo() {
			public Object createInstance(Object data) {
				return new ProjectsController((Context) data);
			}

			public Object getId() {
				return ProjectsController.class;
			}
		};
	}

	public void activate(Map requests, ActivationObserver observer) {
  	ProjectRemote.Util.inst.findAll(new AsyncCallback< List<IProject> >() {
  		public void onSuccess(List<IProject> result) {
//  			System.out.println("projects succeeded");
  			view.setProjects(result);
  			
//  		   ProjectRemote.Util.inst.findAllPublicProjects(new AsyncCallback< List<IProject> >() {
//  		      public void onSuccess(List<IProject> result) {
////  		        System.out.println("projects succeeded");
//  		        view.addProjects(result);
//  		      }
//  		      public void onFailure(Throwable caught) {
//  		        UiNotifier.instance().showError("projects failed:" + caught);
//  		        System.out.println("projects failed");
//  		      }
//  		    });

  		}
  		public void onFailure(Throwable caught) {
  		  UiNotifier.instance().showError("projects failed:" + caught);
  			System.out.println("projects failed");
  		}
  	});
  	observer.activated(this, null);
	}

	public void activate(DynamicParams params) {
		if (view == null) {
			view = new ProjectsView(this);
		}

//    ProjectRemote.Util.inst.findAll(new AsyncCallback<List<IProject>>() {
//      public void onSuccess(List<IProject> result) {
//        System.out.println("projects succeeded");
//        view.setProjects(result);
//      }
//      public void onFailure(Throwable caught) {
//        System.out.println("projects failed");
//      }
//    });
	}

	public View getView() {
		return view;
	}

	public Handler createHandlerById(Action action) {
		switch (action.getId()) {
			case ActionId.OPEN_PROJECT: {
				DynamicParams params = new DynamicParams();
				params.addParam(new DynamicParams
						.Param(ParamId.PROJECT_PARAM, action.getData()));
				Handler result = new OpenProjectActionHandler(this);
				result.addParams(params);
				return result;
			}
			case ActionId.NEW_PROJECT: {
				return new NewProjectActionHandler(this);
			}
			case ActionId.DELETE_PROJECT: {
				return new DeleteProjectActionHandler(this);
			}
		}
		return null;
	}
	
  public void onClick(ClickEvent event) {
    IProject project = view.getDomainObject(event);
    if (project != null) {
      Map<Object, Object> requests = new HashMap<Object, Object>();
      requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
      requests.put(RequestId.PROJECT_ID, project.getId());
      requests.put(RequestId.PAGE_ID, project.getDashboard().getId());
      
      RequestUtils.activate(requests);
    } else {
      super.onClick(event);
    }
  }

}
