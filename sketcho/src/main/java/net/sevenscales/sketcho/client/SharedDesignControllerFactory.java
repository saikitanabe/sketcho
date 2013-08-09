package net.sevenscales.sketcho.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.Debug;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.IControllerFactory;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.sketcho.client.app.controller.ArchitectureController;
import net.sevenscales.sketcho.client.app.controller.DefaultFooterController;
import net.sevenscales.sketcho.client.app.controller.HierarchyController;
import net.sevenscales.sketcho.client.app.controller.LabelController;
import net.sevenscales.sketcho.client.app.controller.ManageLabelsController;
import net.sevenscales.sketcho.client.app.controller.MenuController;
import net.sevenscales.sketcho.client.app.controller.NewPageController;
import net.sevenscales.sketcho.client.app.controller.PageController;
import net.sevenscales.sketcho.client.app.controller.ProjectController;
import net.sevenscales.sketcho.client.app.controller.ProjectsController;
import net.sevenscales.sketcho.client.app.controller.SketchController;
import net.sevenscales.sketcho.client.app.controller.SketchesController;
import net.sevenscales.sketcho.client.app.controller.TitleController;
import net.sevenscales.sketcho.client.app.controller.TopNavigationController;
import net.sevenscales.sketcho.client.app.controller.impl.project.NewProjectController;


public class SharedDesignControllerFactory implements IControllerFactory {
	private static Map controllers;
	private static List defaultControllerList;
  private Context context;;

	private static Map<String, ClassInfo> controllerMap;
	static {
		controllerMap = new HashMap<String, ClassInfo>();
		controllerMap.put(RequestValue.PROJECT_CONTROLLER, ProjectController.info());
		controllerMap.put(RequestValue.PROJECTS_CONTROLLER, ProjectsController.info());
		controllerMap.put(RequestValue.NEW_PROJECT_CONTROLLER, NewProjectController.info());
		controllerMap.put(RequestValue.TOP_NAVIGATION_CONTROLLER, TopNavigationController.info());
		controllerMap.put(RequestValue.NEW_PAGE_CONTROLLER, NewPageController.info());
		controllerMap.put(RequestValue.PAGE_CONTROLLER, PageController.info());
    controllerMap.put(RequestValue.SKETCH_CONTROLLER, SketchController.info());		
    controllerMap.put(RequestValue.SKETCHES_CONTROLLER, SketchesController.info());   
    controllerMap.put(RequestValue.PROJECT_ARCHITECTURE_CONTROLLER, ArchitectureController.info());   
    controllerMap.put(RequestValue.MANAGE_LABELS_CONTROLLER, ManageLabelsController.info());   
    
		// main controller
		controllerMap.put(null, ProjectsController.info());

		// page area default controller infos
		defaultControllerList = new ArrayList();
		defaultControllerList.add(DefaultFooterController.info());
		defaultControllerList.add(TopNavigationController.info());
    defaultControllerList.add(TitleController.info());
//    defaultControllerList.add(PrimaryNavigationController.info());
    defaultControllerList.add(HierarchyController.info());
    defaultControllerList.add(MenuController.info());   
    defaultControllerList.add(LabelController.info());
	}

	public SharedDesignControllerFactory
	    (Map<String, ClassInfo> controllerMap, List<ClassInfo> defaultControllers, Context context) {
		controllers = new HashMap();
		this.context = context;
		this.controllerMap.putAll(controllerMap);
		this.defaultControllerList.addAll(defaultControllers);
	}

	public IController getController(Map requests) {
		Object controllerId = requests.get(RequestId.CONTROLLER);
		ClassInfo ci = (ClassInfo) controllerMap.get(controllerId);
		if (ci == null) {
			Debug.print(controllerId + " has not been installed");
			return null;
		}
		return getController(ci);
	}
	
	public List getDefaultControllers() {
		return defaultControllerList;
	}

	public IController getController(ClassInfo ci) {
		IController result = (IController) controllers.get(ci.getId());
		if (result == null) {
			result = (IController) ci.createInstance(context);
			controllers.put(ci.getId(), result);
		}		
		return result;
	}

}
