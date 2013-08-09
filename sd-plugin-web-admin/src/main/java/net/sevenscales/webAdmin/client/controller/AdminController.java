package net.sevenscales.webAdmin.client.controller;

import java.util.HashMap;
import java.util.List;
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
import net.sevenscales.domain.api.Member;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.serverAPI.remote.AdminRemote;
import net.sevenscales.webAdmin.client.view.AdminView;
import net.sevenscales.webAdmin.client.view.MemberSuggestOracle;
import net.sevenscales.webAdmin.client.view.AdminView.MemberConfigurationObserver;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminController extends ControllerBase<Context> implements MemberConfigurationObserver {

	private AdminView view;
	// currently disables, due to privacy policy
//  private MemberSuggestOracle oracle;
	
	private AdminController(Context context) {
	  super(context);
//	  this.oracle = new MemberSuggestOracle();
//		view = new AdminView(this, this, oracle);
    view = new AdminView(this, this);
	}
	
	public static ClassInfo info() {
		return new ClassInfo() {
			public Object createInstance(Object data) {
				return new AdminController((Context) data);
			}

			public Object getId() {
				return AdminController.class;
			}
		};
	}

	public void activate(Map requests, ActivationObserver observer) {
	    AdminRemote.Util.inst.findAll(getContext().getProjectId(), new AsyncCallback<List<Member>>() {
	      public void onSuccess(List<Member> result) {
//	        oracle.setMembers(result);
          view.setUsers(result);
	      }
	      public void onFailure(Throwable caught) {
          System.out.println("FAILURE: users failed");
	      }
	    });
	    observer.activated(this, null);	    
	}

	public void activate(DynamicParams params) {
//      	ServiceUtils.service.users(new AsyncCallback() {
//      		public void onSuccess(Object result) {
//      			System.out.println("users succeeded");
//      			IUser[] users = (IUser[]) result;
//      			view.setUsers(users);
//      		}
//      		public void onFailure(Throwable caught) {
//      			System.out.println("users failed");
//      		}          		
//      	});
	}

	public Handler createHandlerById(Action action) {
		switch (action.getId()) {
		  case ActionId.ADD_USER: {
		    return new HandlerBase() {
		      public void execute() {
		        Map<Object, Object> requests = new HashMap<Object, Object>();
		        requests.put(RequestId.CONTROLLER, RequestValue.NEW_USER_CONTROLLER);

		        String queries = Location.formatRequests(requests);
		        History.newItem(queries);
		      }
		    };
		  }
			case ActionId.DELETE_USER: {
				return new DeleteUserActionHandler(this);
			}
			
		}
		return null;
	}

	public View getView() {
		return view;
	}

  public void addMember(String username) {
	  // TODO: map username to index if username is encoded...
//    int i = oracle.getMembers().indexOf(username);
//    if (i >= 0) {
//      Long id = oracle.getMembers().get(i).getId();
      AdminRemote.Util.inst.addMember(username, getContext().getProject(), new AsyncCallback<List<Member>>() {
        public void onSuccess(List<Member> result) {
//          oracle.setMembers(result);
          view.setUsers(result);
          UiNotifier.instance().clear();
        }
        public void onFailure(Throwable arg0) {
          UiNotifier.instance().showError("Member add failed, is it correct email address of the user?");
        }
      });
//    }
  }
  
  public void remove(Member member) {
    AdminRemote.Util.inst.removeMember(member.getUsername(), getContext().getProject(), new AsyncCallback<List<Member>>(){
      public void onSuccess(List<Member> result) {
//        oracle.setMembers(result);
        view.setUsers(result);
      }
      public void onFailure(Throwable caught) {
        System.out.println("FAILURE: remove member");
      }
    });
  }
  
  public void setPermission(Member member, int permission, boolean enabled) {
    final Member oldvalue = member;
    if (enabled) {
      AdminRemote.Util.inst.addPermission(member.getUsername(), getContext().getProject(), permission, new AsyncCallback<Member>() {
        public void onSuccess(Member result) {
          view.updateMember(result);
        }
        public void onFailure(Throwable caught) {
          System.out.println("FAILURE: add permission");
          view.updateMember(oldvalue);
        }
      });
    } else {
      AdminRemote.Util.inst.deletePermission(member.getUsername(), getContext().getProject(), permission, new AsyncCallback<Member>() {
        public void onSuccess(Member result) {
          view.updateMember(result);
        }
        public void onFailure(Throwable caught) {
          System.out.println("FAILURE: delete permission");
          view.updateMember(oldvalue);
        }
      });
    }
  }

}
