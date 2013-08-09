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
import net.sevenscales.login.client.view.RegisterView;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.serverAPI.remote.AdminRemote;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.KeyboardListener;

public class RegisterController extends ControllerBase<Context> {

	private RegisterView loginview;
	private View dummyView;
	
	private RegisterController(Context context) {
	  super(context);
    loginview = new RegisterView(this);
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
        return new RegisterController((Context) data);
      }
  
      public Object getId() {
        return RegisterController.class;
      }
	  };
	}
	
	public void activate(Map requests, ActivationObserver observer) {
    observer.activated(this, null);
	}

	public void activate(DynamicParams params) {
	}

	public Handler createHandlerById(Action action) {
		return createHandler(action.getId());
	}
	
	public Handler createHandler(int id) {
		  switch (id) {
		    case ActionId.REGISTER: {
		      return new HandlerBase() {
		        public void execute() {
		          boolean register = true;
              if (loginview.getUserName().length()==0) {
                register = false;
                UiNotifier.instance().showError("Check Email");
              }

		          if (!loginview.getPassword().equals(loginview.getPasswordVerifyField()) || 
		              loginview.getPasswordVerifyField().length()==0) {
		            register = false;
		            UiNotifier.instance().showError("Check Password");
		          }
		          if (register) {
		            loginview.enableSubmit(false);
		            UiNotifier.instance().showInfo("Registering...");
	              AdminRemote.Util.inst.register(
	                  loginview.getUserName(), loginview.getNickName(), loginview.getPassword(), new AsyncCallback() {
	                public void onSuccess(Object result) {
	                  Map requests = new HashMap();
	                  requests.put(RequestId.CONTROLLER, RequestValue.LOGIN_CONTROLLER);
	                  RequestUtils.activate(requests);
	                  loginview.enableSubmit(true);
	                  UiNotifier.instance().showInfo("Email has been sent to activate account");
	                }
	                public void onFailure(Throwable caught) {
                    loginview.enableSubmit(true);
	                  UiNotifier.instance().showError("Registration failure: "+caught);
	                  System.out.println("FAILURE: register");
	                }
	              });
		          }
		        }
		      };
		    }
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
 			createHandler(ActionId.REGISTER).execute();
 		}
	}
}
