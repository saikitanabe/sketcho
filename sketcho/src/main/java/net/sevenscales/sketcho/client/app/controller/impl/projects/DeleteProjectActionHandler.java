package net.sevenscales.sketcho.client.app.controller.impl.projects;

import java.util.ArrayList;

import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.sketcho.client.app.controller.ProjectsController;
import net.sevenscales.sketcho.client.app.view.ProjectsView;


public class DeleteProjectActionHandler extends HandlerBase {

	private ProjectsController controller;
	private ProjectsView view;

	public DeleteProjectActionHandler(ProjectsController controller) {
		this.controller = controller;
		this.view = (ProjectsView) controller.getView();
	}

	public void execute() {
		// get marked projects
//		ArrayList projects = view.getSelectedProjects();

		// delete marked projects
//		for (int i = 0; i < projects.size(); ++i) {
//			System.out.println(projects.get(i));
//			ServiceUtils.service.removeProject((IProject)projects.get(i), new AsyncCallback() {
//				public void onSuccess(Object result) {
////					System.out.println("project removed");
//					Map requests = new HashMap();
//					requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
//					RequestUtils.activate(requests);
//				}
//				public void onFailure(Throwable caught) {
//					System.out.println("project remove failed: " + caught);
//					DialogUtils.showInfoNote("IProject has pages, cannot delete");
//				}
//			});
//		}
	}
}
