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
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.serverAPI.remote.PageRemote;
import net.sevenscales.serverAPI.remote.ProjectRemote;
import net.sevenscales.sketcho.client.app.utils.NoInsertAtEndIndexedDropController;
import net.sevenscales.sketcho.client.app.utils.PageOrganizeHandle;
import net.sevenscales.sketcho.client.app.view.ProjectView;
import net.sevenscales.sketcho.client.app.view.constants.ParamId;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ProjectController extends ControllerBase<Context> implements DragHandler {
	private ProjectView view;
	private IProject project;
	private ActivationObserver activationObserver;
	
	protected ProjectController(Context context) {
	  super(context);
		view = new ProjectView(this, this);
	}

	public static ClassInfo info() {
		return new ClassInfo() {
			public Object createInstance(Object data) {
				return new ProjectController((Context) data);
			}

			public Object getId() {
				return ProjectController.class;
			}
		};
	}

	public void activate(Map requests, ActivationObserver observer) {
		this.activationObserver = observer;
		final ProjectController self = this;
		Long id = Long.valueOf( (String) requests.get(RequestId.PROJECT_ID));
		
  	ProjectRemote.Util.inst.open(id, new AsyncCallback<IProject>() {
  		public void onSuccess(IProject result) {
  			project = result;
//  			System.out.println("IProject opened: " + project.getName());
  			DynamicParams params = new DynamicParams();
  			params.addParam(ParamId.PROJECT_PARAM, project);
  			activationObserver.activated(self, params);
  		}
  		public void onFailure(Throwable caught) {
  		  UiNotifier.instance().showError("project open fail: " + caught);
  			System.out.println("IProject open failed");
  		}
  	});
	}
	
	public void activate(DynamicParams params) {
//		projectPage = (IProject) params.getParam(ParamId.PROJECT_PARAM);
	}

	public Handler createHandlerById(Action action) {
		switch (action.getId()) {
			case ActionId.NEW_PAGE: {
				return new HandlerBase() {
					public void execute() {
						Map requests = new HashMap();
						requests.put(RequestId.CONTROLLER, RequestValue.NEW_PAGE_CONTROLLER);			
						requests.put(RequestId.PROJECT_ID, project.getId());
						
						String queries = Location.formatRequests(requests);
						History.newItem(queries);
					}
				};
			}
			case ActionId.VIEW_DOCUMENTATION: {
			  Map requests = new HashMap<Object, Object>();
	      requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_ARCHITECTURE_CONTROLLER);
	      requests.put(RequestId.PROJECT_ID, getContext().getProjectId());    
        String queries = Location.formatRequests(requests);
        History.newItem(queries);
			}
			case ActionId.OPEN_PAGE: {
				DynamicParams params = new DynamicParams();
				params.addParam(new DynamicParams
						.Param(ParamId.CURRENT_PAGE, action.getData()));
				params.addParam(new DynamicParams
						.Param(ParamId.PROJECT_PARAM, project));
				Handler handler = new HandlerBase() {
					public void execute() {
//						getRootController().activateController
//							(PageController.class, params);
					}
				};

				handler.addParams(params);
				return handler;
			}
			case ActionId.SAVE_PROJECT: {
				return new HandlerBase() {
					public void execute() {
					  ProjectRemote.Util.inst.update(project, new AsyncCallback<IProject>() {
		      		public void onSuccess(IProject result) {
		      			project = (IProject) result;
//		      			System.out.println("IProject saved");
		      		}
		      		public void onFailure(Throwable caught) {
		      			System.out.println("IProject save failed");
		      		}
		      	});
					}
				};
			}
			case ActionId.BACK: {
				return new HandlerBase() {
					public void execute() {
//						getRootController().activateController(ProjectsController.class);
					}
				};
			}
		}
		return null;
	}

	public View getView() {
		return view;
	}

  public void onDragEnd(DragEndEvent event) {
    PageOrganizeHandle poh = (PageOrganizeHandle) event.getSource();
    NoInsertAtEndIndexedDropController c = (NoInsertAtEndIndexedDropController) 
      event.getContext().finalDropController;
    
    if (c != null) {
      int index = c.getTarget().getWidgetIndex(poh);
      PageRemote.Util.inst.move(poh.getPage().getId(), index, c.getPage().getId(), new AsyncCallback<IPage>() {
        public void onSuccess(IPage result) {
          project = result.getProject();
          // HACK! need to update context as well
          getContext().setProject(project);
          
          DynamicParams params = new DynamicParams();
          params.addParam(ParamId.PROJECT_PARAM, project);
          activationObserver.activated(ProjectController.this, params);
        }
        public void onFailure(Throwable caught) {
          System.out.println("FAIL: reorder failed");
          DynamicParams params = new DynamicParams();
          params.addParam(ParamId.PROJECT_PARAM, project);
          activationObserver.activated(ProjectController.this, params);
        }
      });
    }

//    // remove from old parent
//    poh.getPage().getParent().getSubpages().remove(poh.getPage());
//    
//    // reorder
//    for (int i = 0; i < c.getTarget().getWidgetCount(); ++i) {
//      Widget w = (Widget) c.getTarget().getWidget(i);
//      if (w instanceof PageOrganizeHandle) {
//        PageOrganizeHandle h = (PageOrganizeHandle) w;
//        h.getPage().setOrderValue(i+1);
//      }
//    }
//    // add to new parent
//    IPage newParent = c.getPage();
//    newParent.getSubpages().add(poh.getPage());
    
    // update project
//    ProjectRemote.Util.inst.update(project, new AsyncCallback<IProject>(){
//      public void onSuccess(IProject result) {
//        project = result;
//        DynamicParams params = new DynamicParams();
//        params.addParam(ParamId.PROJECT_PARAM, project);
//        activationObserver.activated(ProjectController.this, params);
//      }
//      public void onFailure(Throwable caught) {
//        System.out.println("FAIL: reorder failed");
//        DynamicParams params = new DynamicParams();
//        params.addParam(ParamId.PROJECT_PARAM, project);
//        activationObserver.activated(ProjectController.this, params);
//      }
//    });
    
//    PageRemote.Util.inst.moveAndUpdate
//      (poh.getPage(), c.getPage().getId(), new AsyncCallback<IPage>() {
//        public void onSuccess(IPage result) {
//      //    System.out.println("subpage saved");
//          project = result.getProject();
//          DynamicParams params = new DynamicParams();
//          params.addParam(ParamId.PROJECT_PARAM, project);
//          activationObserver.activated(ProjectController.this, params);
////          Map requests = new HashMap();
////          requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_CONTROLLER);
////          requests.put(RequestId.PROJECT_ID, projectId);
////          RequestUtils.activate(requests);
//        }
//        public void onFailure(Throwable caught) {
//          System.out.println("subpage save failed");
//        }
//      });

  }

  public void onDragStart(DragStartEvent event) {
  }

  public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
  }

  public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
  }

}
