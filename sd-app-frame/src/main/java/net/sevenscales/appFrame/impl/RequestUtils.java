package net.sevenscales.appFrame.impl;


import java.util.Map;

import com.google.gwt.user.client.History;

public class RequestUtils {
  static Impl impl;
  
  private static class Impl {
    RootController rootController;
  }

//	public static void ActivateProject(Long projectId) {
//		Map requests = new HashMap();
//		requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_CONTROLLER);
//		requests.put(RequestId.PROJECT_ID, projectId);		
//		activateRequests(requests);
//	}
	
	public static void activate(Map requests) {
//  svg.render.forceflash=true
//    requests.put("svg.render.forceflash","true");
		activateRequests(requests);
	}
	
	public static void activateDynamic(Map<String, Object> requests) {
	  impl.rootController.activate(requests);
	}
	
	private static void activateRequests(Map requests) {
		String token = History.getToken();
		Location current = new Location(token);
		Location newLocation = new Location(requests);
		
		String queries = Location.formatRequests(requests);
		if (current.equals(newLocation)) {
			// reactivate same request
//			History.onHistoryChanged(queries);
		  History.fireCurrentHistoryState();
		} else {
			History.newItem(queries);
		}

	}
	
	public static void refresh() {
    String token = History.getToken();
    Location current = new Location(token);
    String queries = Location.formatRequests(current.getRequests());
    // reactivate same request
    History.onHistoryChanged(queries);
  }
	
	private static native void refreshBrowser()/*-{
		$wnd.location.reload(true);
	}-*/;

  public static Long parseLong(Object id, Map requests) {
    if (requests.get(id) != null) {
      return Long.valueOf( (String) requests.get(id) );
    }
    return null;
  }

  public static Integer parseInt(Object id, Map requests) {
    if (requests.get(id) != null) {
      return Integer.valueOf( (String) requests.get(id) );
    }
    return null;
  }

  public static void setRootController(RootController rootController) {
    if (impl == null) {
      impl = new Impl();
      impl.rootController = rootController;
    }
  }
  
  public static RootController getRootController() {
    if (impl == null) {
      return null;
    }
    return impl.rootController;
  }

}
