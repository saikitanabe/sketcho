package net.sevenscales.webAdmin.client.controller;

import com.google.gwt.user.client.rpc.AsyncCallback;

import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.domain.api.IUser;
import net.sevenscales.domain.dto.UserDTO;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.serverAPI.remote.AdminRemote;
import net.sevenscales.webAdmin.client.view.NewUserView;

import java.util.HashMap;
import java.util.Map;

public class CreateUserHandler extends HandlerBase {
	private NewUserController controller;
	private NewUserView view;
	
	public CreateUserHandler(NewUserController controller) {
		this.controller = controller;
		this.view = (NewUserView) controller.getView();
		view.getPassword().setText("");
	}

	public void execute() {
	  IUser user = new UserDTO();
	  user.setUsername(view.getUserName().getText());
    user.setPassword(view.getPassword().getText());
    
    AdminRemote.Util.inst.save(user, new AsyncCallback<IUser>() {
      public void onSuccess(IUser result) {
//        System.out.println("User created");
        Map<Object, Object> requests = new HashMap<Object, Object>();
        requests.put(RequestId.CONTROLLER, RequestValue.ADMIN_CONTROLLER);
        RequestUtils.activate(requests);
      }
      public void onFailure(Throwable caught) {
        System.out.println("User creation failed");        
      }
    });
	}

}
