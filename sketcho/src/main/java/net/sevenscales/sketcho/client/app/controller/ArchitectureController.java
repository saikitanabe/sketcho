package net.sevenscales.sketcho.client.app.controller;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.api.IRegistryEventObserver;
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
import net.sevenscales.domain.dto.ContentUpdateEventDTO;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.SdRegistryEvents;
import net.sevenscales.serverAPI.remote.ProjectRemote;
import net.sevenscales.sketcho.client.app.view.ArchitectureView;
import net.sevenscales.sketcho.client.app.view.constants.ParamId;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ArchitectureController extends ControllerBase<Context> implements IRegistryEventObserver {
	private ArchitectureView view;
	private IProject project;
	private ActivationObserver activationObserver;
	
	private ArchitectureController(Context context) {
	  super(context);
		view = new ArchitectureView(this);
    getContext().getEventRegistry().register(
        SdRegistryEvents.EVENT_CONTENT_UPDATE, this);
	}

	public static ClassInfo info() {
		return new ClassInfo() {
			public Object createInstance(Object data) {
				return new ArchitectureController((Context) data);
			}

			public Object getId() {
				return ArchitectureController.class;
			}
		};
	}
	
	public void activate(Map requests, ActivationObserver observer) {
		this.activationObserver = observer;
		final ArchitectureController self = this;
//		Long id = Long.valueOf( (String) requests.get(RequestId.PROJECT_ID));
		
  	ProjectRemote.Util.inst.open(getContext().getProjectId(), new AsyncCallback<IProject>() {
  		public void onSuccess(IProject result) {
  			project = result;
//  			System.out.println("IProject opened: " + project.getName());
  			DynamicParams params = new DynamicParams();
  			params.addParam(ParamId.PROJECT_PARAM, result);
  			activationObserver.activated(self, params);
  		}
  		public void onFailure(Throwable caught) {
  			System.out.println("FAILURE: IProject open failed");
  		}
  	});
	}
	
	public void activate(DynamicParams params) {
//		projectPage = (IProject) params.getParam(ParamId.PROJECT_PARAM);
	}

	public Handler createHandlerById(Action action) {
		switch (action.getId()) {
		  case ActionId.ORGANIZE: {
		    return new HandlerBase() {
		      public void execute() {
		        Map requests = new HashMap<Object, Object>();
		        requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_CONTROLLER);
		        requests.put(RequestId.PROJECT_ID, getContext().getProjectId());
		        String queries = Location.formatRequests(requests);
		        History.newItem(queries);
		      }     
		    };
		  }
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
		}
		return null;
	}

	public View getView() {
		return view;
	}
	
  // @Override
  public void handleEvent(Integer eventId, Object data) {
    if (eventId.equals(SdRegistryEvents.EVENT_CONTENT_UPDATE)) {
      ContentUpdateEventDTO update = (ContentUpdateEventDTO) data;
      view.redraw(update.content);
    }
  }
	

}
