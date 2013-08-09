package net.sevenscales.appFrame.impl;

import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;


public class RootController implements ActivationObserver {
	private RootPanel rootPanel;
	private ITilesEngine tilesEngine;
	private IControllerFactory controllerFactory;
  private IContributor contributor;
  private Map prevRequest;
  private Map currentRequest;
  private EventRegistry eventRegistry;

	public RootController(IControllerFactory controllerFactory,
			 			  ITilesEngine tilesEngine, IContributor contributor, EventRegistry eventRegistry) {
		this.controllerFactory = controllerFactory;
		this.tilesEngine = tilesEngine;
		this.contributor = contributor;
		this.eventRegistry = eventRegistry;
	}

	public void setRoot(RootPanel rootPanel) {
		this.rootPanel = rootPanel;
	}

	/*
	public void activateController(Class controllerClass,
			int actionId) {
		// register action so that control can start in correct state
		globalState.registerAction(actionId);

//		activateController(controllerClass);
	}*/

	public void activate(Map requests) {
		// activate default controllers
		activateDefaultControllers(requests);
		
//		activateGlobalControllers(requests);

		// controller can override default controllers
		// provide empty params
		doActivateController(requests);
	}
	
//	private void activateGlobalControllers(Map requests) {	  
//	  if (globalControllers == null) {
//	    globalControllers = new ArrayList();
//  	  for (ControllerInfo ci : controllerFactory.getControllers()) {
//  	    if (ci.isGlobal()) {
//  	      globalControllers.add( ci.getInstance() );
//  	    }
//  	  }
//	  }
//	  
//	  for (IController c : globalControllers) {
//	    c.activate(requests, this);
//	  }
//  }

  private void activateDefaultControllers(Map requests) {
		int size = controllerFactory.getDefaultControllers().size();
		for (int i = 0; i < size; ++i) {
			ClassInfo ci = (ClassInfo) controllerFactory.getDefaultControllers().get(i);
			IController c = controllerFactory.getController(ci);
			c.activate(requests, this);
//			activateView(c, null, contributor);
		}
	}

	private void doActivateController(Map requests) {
    eventRegistry.handleEvent(1000, null);

	  prevRequest = currentRequest;
	  currentRequest = requests;
		IController c = controllerFactory.getController(requests);
		
//		System.out.println("Activating " + c + " controller");
		c.activate(requests, this);
	}
	
	public void activated(IController controller, DynamicParams params) {
	  tilesEngine.clearDynamicAreas();
		activateView(controller, params, contributor);
	}

	private void activateView(
	    IController controller, DynamicParams params, IContributor contributor) {
		View view = controller.getView();
		if (view != null) {
			view.activate(tilesEngine, params, contributor);
		} else {
			Debug.print(controller + ": controller needs to implement a view");
		}
	}

	public void installTemplate(Composite template) {
		rootPanel.clear();
		rootPanel.add(template);
	}

	public ITilesEngine getTilesEngine() {
		return tilesEngine;
	}
	
	public Map getPrevRequest() {
	  return prevRequest;
	}
	
//	public Handler createNavigateHandler(Action action) {
//		Handler result = (Handler) controllerGraph.graph.get
//			(new ControllerGraph.Key(action.getId(), action.getController().getClass()));
//		
//		return result;
//	}
}
