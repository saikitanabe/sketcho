package net.sevenscales.sketcho.client;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.api.IRegistryEventObserver;
import net.sevenscales.appFrame.impl.EventRegistry;
import net.sevenscales.appFrame.impl.IControllerFactory;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.Location;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.RootController;
import net.sevenscales.appFrame.impl.TilesEngineComponent;
import net.sevenscales.domain.api.ProjectContextDTO;
import net.sevenscales.domain.dto.AuthenticationDTO;
import net.sevenscales.editor.ui.SilverlightInstallView;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.SdPluginManager;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.SdRegistryEvents;
import net.sevenscales.plugin.constants.TileId;
import net.sevenscales.pluginManager.api.IPluginFactory;
import net.sevenscales.serverAPI.remote.AdminRemote;
import net.sevenscales.serverAPI.remote.LoginRemote;
import net.sevenscales.sketcho.client.app.view.layout.ViewTemplate4;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sketcho implements EntryPoint, HistoryListener, IRegistryEventObserver {

	private RootController rootController;
	private Context context;
	private EventRegistry eventRegistry;
  private ITilesEngine tilesEngine;
	private static Sketcho instance;

  public static native void observOnLoaded()/*-{
//    $wnd.dojo.require("dojox.gfx.silverlight");	
  	$wnd.dojo.addOnLoad(@net.sevenscales.sketcho.client.Sketcho::loaded());
  }-*/; 

	/**
	* This is the entry point method.
	*/
	public void onModuleLoad() {
		instance = this;
		observOnLoaded();
	}
	
	public static void loaded() {
		instance.load();
	}
	
	public void load() {
		// activate main controller
		// default controllers will be activated at the same time and main controller
		// can override those
	  context = new Context();
	  // TODO: this is a user setting
	  context.setEditMode(false);
	  eventRegistry = new EventRegistry();
	  context.setEventRegistry(eventRegistry);
	  context.getClipboard().setEventRegistry(eventRegistry);

    LoginRemote.Util.inst.relogin(new AsyncCallback<AuthenticationDTO>() {
      public void onSuccess(AuthenticationDTO result) {
//        System.out.println("Starting with logged in state: " + result.getUserId());
        // start with logged in or logged out state (result can be null as well)
        context.setUserId(result.userId);
        start();
        eventRegistry.handleEvent(SdRegistryEvents.EVENT_LOGIN, null);
      }
      public void onFailure(Throwable caught) {
        System.out.println("Startup failure");
        // start with logged out state
        start();
      }
    });
	}
	
	private void start() {
    this.tilesEngine = new TilesEngineComponent();
    History.addHistoryListener(this);

    IPluginFactory factory = (IPluginFactory) GWT.create(IPluginFactory.class);
    SdPluginManager pluginManager = new SdPluginManager(factory, context);
    
    // TODO: not so nice way to share these
    // - an other alternative could be that root controller sets to controller
    // so it is possible to access these at any time
    context.setTilesEngine(tilesEngine);
    context.setContributor(pluginManager);

    IControllerFactory controllerFactory = new SharedDesignControllerFactory
      (pluginManager.getControllerMap(), pluginManager.getDefaultControllers(), context);
    this.rootController = new RootController(controllerFactory, tilesEngine, 
        pluginManager, eventRegistry);
    rootController.setRoot(RootPanel.get("content"));
    rootController.installTemplate(new ViewTemplate4
        ((TilesEngineComponent) rootController.getTilesEngine()));

    RequestUtils.setRootController(rootController);
    
    UiNotifier.instance().setTilesEngine(tilesEngine);
    eventRegistry.register(1000, this);
    String initToken = History.getToken();

    onHistoryChanged(initToken);
	}

  public void onHistoryChanged(String historyToken) {
//		System.out.println("onHistoryChanged: " + historyToken);

		Location location = new Location(historyToken);
		final Map requests = location.getRequests();
		
    SilverlightInstallView install = new SilverlightInstallView();
    
    boolean register = requests.get(RequestId.CONTROLLER) != null ? requests.get(RequestId.CONTROLLER).equals(RequestValue.REGISTER_CONTROLLER) : false;
    if (!install.silverlightInstalled() && !register) {
      // registration and login controllers are not coming here
      tilesEngine.setTile(TileId.CONTENT, install);
      // guard do not continue without proper renderer
      return;
    }
		
		boolean synchronous = true;

    context.setPageId(RequestUtils.parseLong(RequestId.PAGE_ID, requests));

		Long projectId = RequestUtils.parseLong(RequestId.PROJECT_ID, requests);
    context.setProjectId(projectId);
		if ( projectId != null) {
		  if (context.getProject() == null || !context.getProject().getId().equals(projectId)) {
		    synchronous = false;
  		  AdminRemote.Util.inst.openProjectContext(projectId, new AsyncCallback<ProjectContextDTO>() {
  		    public void onSuccess(ProjectContextDTO result) {
  		      context.setProject(result.getProject());
            context.setMemberPermissions(result.getMemberPermissions());
  		      rootController.activate(requests);
  		    }
  		    public void onFailure(Throwable arg0) {
  		      Map request = new HashMap();
  		      request.put(RequestId.CONTROLLER, RequestValue.LOGIN_CONTROLLER);
  		      RequestUtils.activate(request);
  		      System.out.println("FAILURE: no rights for project => login");
  		    }
  		  });
		  }
		}
		
		if (projectId == null) {
      context.setProject(null);
      context.setMemberPermissions(0);
		}
		
		if (synchronous) {
      rootController.activate(requests);
    }
	}

  public void handleEvent(Integer eventId, Object data) {
    UiNotifier.instance().clear();
  }
}
