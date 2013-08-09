package net.sevenscales.login.client.controller;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.ControllerBase;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.Handler;
import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.dto.AuthenticationDTO;
import net.sevenscales.login.client.view.LoginView;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.SdRegistryEvents;
import net.sevenscales.serverAPI.remote.LoginRemote;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.KeyboardListener;

public class LoginController extends ControllerBase<Context> {

	private LoginView loginview;
	private View dummyView;
	
	private LoginController(Context context) {
	  super(context);
    loginview = new LoginView(this);
    dummyView = new View(this) {
      public void activate(ITilesEngine tilesEngine, DynamicParams params,
          IContributor contributor) {
      }
    };
//		getContext().getEventRegistry().registerService(SdRegistryEvents.EVENT_RELOGIN, this);
	}

	public static ClassInfo info() {
	  return new ClassInfo() {
      public Object createInstance(Object data) {
        return new LoginController((Context) data);
      }
  
      public Object getId() {
        return LoginController.class;
      }
	  };
	}
	
  private class SignInCallback implements AsyncCallback<AuthenticationDTO> {
    public void onSuccess(AuthenticationDTO result) {
      getContext().setUserId(result.userId);
      
      Map requests = RequestUtils.getRootController().getPrevRequest();
      if (requests == null || 
          (requests != null && requests.get(RequestId.CONTROLLER) != null && 
              requests.get(RequestId.CONTROLLER).equals(RequestValue.LOGIN_CONTROLLER))) {
        requests = new HashMap();
        requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
      }
      RequestUtils.activate(requests);
      loginview.clear();
    }
    public void onFailure(Throwable caught) {
        UiNotifier.instance().showError("login failed: " + caught);
      System.out.println("Invalid login");
//      RequestUtils.refresh();
    }
  }
  
  private class SignOutCallback implements AsyncCallback {
    // @Override
    public void onSuccess(Object result) {
//      System.out.println("Signed out");
      RequestUtils.activate(RequestUtils.getRootController().getPrevRequest());
      
      // cannot edit if not logged in
      getContext().setEditMode(false);
    }
    // @Override
    public void onFailure(Throwable caught) {
      System.out.println("Sign out failure");
    }
  }

	public void activate(Map requests, ActivationObserver observer) {
	  Integer actionId = RequestUtils.parseInt(RequestId.ACTION_ID, requests);
	  if (actionId != null && actionId == ActionId.LOGOUT) {
      getContext().setUserId(null);
      getContext().getEventRegistry().handleEvent(SdRegistryEvents.EVENT_LOGOUT, null);
      
      DeferredCommand.addCommand(new Command() {
        public void execute() {
          // hack, for some reason doesn't have otherwise enough time to 
          // logout from cometd and this is still not enough :)
          // FIX: need to wait untill unsubscribe call back comes
          LoginRemote.Util.inst.logout(new SignOutCallback());
        }
      });
	  } else {
//	    if (actionId != null && actionId == ActionId.REGISTER) {
//	      loginview.setState(true);
//	    }
	    observer.activated(this, null);
	  }
	}

	public void activate(DynamicParams params) {
	}

	public Handler createHandlerById(Action action) {
		return createHandler(action.getId());
	}
	
	public Handler createHandler(int id) {
		  switch (id) {
		    case ActionId.LOGIN: {
		      return new HandlerBase() {
//		        // use basic authentication
//		        public void execute() {
//		          RequestBuilder rb = 
//		        }
		        public void execute() {
		          LoginRemote.Util.inst.authenticate(loginview.getUserName(), loginview.getPassword(), 
		              loginview.getStaySignedIn(),
		              getContext().getEventRegistry().getHandler(SdRegistryEvents.EVENT_LOGIN, 
		                  new SignInCallback()));
		        }
		      };
		    }
		    case ActionId.REGISTER: {
		      return new HandlerBase() {
		        public void execute() {
              Map requests = new HashMap();
              requests.put(RequestId.CONTROLLER, RequestValue.REGISTER_CONTROLLER);
              RequestUtils.activate(requests);
		        }
		      };
		    }
//		          boolean register = true;
//		          if (!loginview.getPassword().equals(loginview.getPasswordVerifyField())) {
//		            register = false;
//		            UiNotifier.instance().showError("Check Password");
//		          }
//		          if (register) {
//		            loginview.enableSubmit(false);
//		            UiNotifier.instance().showInfo("Registering...");
//	              AdminRemote.Util.inst.register(loginview.getUserName(), loginview.getPassword(), new AsyncCallback() {
//	                public void onSuccess(Object result) {
//	                  loginview.setState(false);
//	                  Map requests = new HashMap();
//	                  requests.put(RequestId.CONTROLLER, RequestValue.LOGIN_CONTROLLER);
//	                  RequestUtils.activate(requests);
//	                  loginview.enableSubmit(true);
//	                  UiNotifier.instance().showInfo("Email has been sent to activate account");
//	                }
//	                public void onFailure(Throwable caught) {
//                    loginview.enableSubmit(true);
//	                  UiNotifier.instance().showError("Registration failure: "+caught);
//	                  System.out.println("FAILURE: register");
//	                }
//	              });
//		          }
//		        }
//		      };
//		    }
		    case ActionId.CANCEL: {
		      return new HandlerBase() {
		        public void execute() {
		          Map<Object, Object> requests = new HashMap<Object, Object>();
		          requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
		          RequestUtils.activate(requests);
		        }
		      };
		    }
		  }
			return null;
	}

	public View getView() {
	  if (getContext().getUserId() != null) {
	    return dummyView;
	  }
		return loginview;
	}
	

  // @Override
  public void signOut() {
//    LoginRemote.Util.inst.logout(new SignOutCallback());
  }

	public void processKeyEvent(char keyCode) {
		if (keyCode == KeyboardListener.KEY_ENTER) {
//		  int action = ActionId.LOGIN;
//		  if (loginview.isEnableRegister()) {
//		    action = ActionId.REGISTER;
//		  }
 			createHandler(ActionId.LOGIN).execute();
 		}
	}
}
