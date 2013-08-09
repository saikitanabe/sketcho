package net.sevenscales.sketcho.client.app.controller.impl.projects;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.Location;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.sketcho.client.app.controller.ProjectsController;

import com.google.gwt.user.client.History;

public class NewProjectActionHandler extends HandlerBase {
	private IController controller;

	public NewProjectActionHandler(ProjectsController controller) {
		this.controller = controller;
	}

	public void execute() {
		Map requests = new HashMap();
		requests.put(RequestId.CONTROLLER, RequestValue.NEW_PROJECT_CONTROLLER);			

		String queries = Location.formatRequests(requests);
		History.newItem(queries);
	}
}
