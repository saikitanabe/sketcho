package net.sevenscales.sketcho.client.app.controller.impl.project;

import java.util.HashMap;

import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.dto.ProjectDTO;
import net.sevenscales.serverAPI.remote.ProjectRemote;
import net.sevenscales.sketcho.client.app.view.NewProjectView;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class CreateProjectHandler extends HandlerBase {
	private NewProjectController controller;
	private NewProjectView view;
	
	public CreateProjectHandler(NewProjectController controller) {
		this.controller = controller;
		this.view = (NewProjectView) controller.getView();
	}

	public void execute() {
      	IProject p = new ProjectDTO();
      	p.setName(view.getProjectNameField().getText());
      	p.setPublicProject(view.isPublicProject());
      	ProjectRemote.Util.inst.save(p, new AsyncCallback<IProject>() {
      		public void onSuccess(IProject result) {
//      			System.out.println("project stored");
      			RequestUtils.activate(new HashMap());
      		}
      		public void onFailure(Throwable caught) {
      			System.out.println("project store failed");
      		}
      	});
	}

}
