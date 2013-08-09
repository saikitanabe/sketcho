package net.sevenscales.webAdmin.client.controller;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.domain.api.IUser;
import net.sevenscales.serverAPI.remote.AdminRemote;
import net.sevenscales.webAdmin.client.view.AdminView;

import java.util.List;

public class DeleteUserActionHandler extends HandlerBase {
	
	private AdminController controller;
	private AdminView view;

	public DeleteUserActionHandler(AdminController controller) {
		this.controller = controller;
		this.view = (AdminView) controller.getView();
	}

	public void execute() {
		final DialogBox d = new DialogBox();
		d.setText("Delete Selected User(s)?");
		HorizontalPanel buttons = new HorizontalPanel();
		Button ok = new Button("Ok");
		ok.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				// get marked projects
				List<IUser> users = (List<IUser>) view.getSelectedUsers();
				
				// delete marked projects
				AdminRemote.Util.inst.removeAll(users, new AsyncCallback() {
				  public void onSuccess(Object result) {
//				    System.out.println("users deleted");
				    RequestUtils.refresh();
				  }
				  public void onFailure(Throwable caught) {
            System.out.println("delete failed");				    
				  }
				});
				
				d.hide();
			}
		});
		
		Button cancel = new Button("Cancel");
		cancel.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				d.hide();
			}
		});
		
		buttons.add(ok);
		buttons.add(cancel);
		d.setWidget(buttons);
		d.center();
		d.show();
	}
}
