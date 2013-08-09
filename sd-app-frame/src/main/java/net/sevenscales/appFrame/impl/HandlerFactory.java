package net.sevenscales.appFrame.impl;


import java.util.HashMap;
import java.util.Map;


public class HandlerFactory {
	private Map controllerMap = new HashMap();
	
	public HandlerFactory() {
	}

	public Handler createHandler(Action action) {
//		Handler result = (Handler) controllerMap.get(new Integer(action.getId()));
//		if ( result == null) {
	  Handler result = action.getController().createHandlerById(action);
//			if (result == null) {
//				result = action.getController().getRootController().
//					createNavigateHandler(action);
//			}
//			if (result != null) {
//				controllerMap.put(new Integer(action.getId()), result);
//			} else {
			if (result == null) {
			  Debug.print(action.getController() + ": Action handler not implemented");			  
			}
		return result;
	}
}
