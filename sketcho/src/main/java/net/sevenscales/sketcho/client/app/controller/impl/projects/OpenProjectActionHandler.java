package net.sevenscales.sketcho.client.app.controller.impl.projects;


import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.sketcho.client.app.controller.ProjectsController;

public class OpenProjectActionHandler extends HandlerBase {

	private ProjectsController projectsController;
	private IProject project;

	public OpenProjectActionHandler(ProjectsController projectsController) {
		this.projectsController = projectsController;
	}

	public void execute() {
//		projectsController.getRootController()
//			.activateController(ProjectController.class, params);
	}

}
