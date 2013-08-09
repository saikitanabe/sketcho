package net.sevenscales.appFrame.impl;


import java.util.HashMap;
import java.util.Map;


public class ActionFactory {
	private static class ActionFactoryImpl {
		private Map actions = new HashMap();
		public void addAction(Action action) {
			actions.put(new Integer(action.getId()), action);
		}
		
		public Action getAction(int actionId) {
			return (Action) actions.get(new Integer(actionId));
		}
	}

	private static ActionFactoryImpl impl;
	static {
		impl = new ActionFactoryImpl();
	}
	
//	static public Action createLinkAction(String name, int id, Controller controller) {
		// TODO: return already created item if exists
		// but perhaps some refactoring is needed and actions should be stored at all
		// and all views can create which ever kind of actions they want
		// and different views could have different kind of actions with same
		// action id
		// NOTE! cannot create singleton instances, because multiple actions
		// can have same action id e.g. open project
//		LinkAction result = new LinkAction();
//		setAction(result, name, id, controller);
//		return result;
//	}

	public static Action createLinkAction(String name, Map requests) {
		LinkAction result = new LinkAction(requests);
		result.setName(name);
		return result;
	}

	public static Action createLinkAction(String name) {
		LinkAction result = new LinkAction();
		result.setName(name);
		return result;
	}

	static public ButtonAction createButtonAction(String name, int id,
			IController controller) {
		ButtonAction result = new ButtonAction(controller);
		setAction(result, name, id, controller);
		return result;
	}
/*
	static public Action createListBoxAction(String name, int id,
			Controller controller) {
		ListBoxAction result = new ListBoxAction(controller);
		result.setName(name);
		result.setId(id);
		result.addChangeObserver(controller);
		return result;
	}*/

	public static Action getAction(int actionId) {
		return impl.getAction(actionId);
	}

	static private void setAction(Action action, String name, int id, IController controller) {
		action.setName(name);
		action.setId(id);
		action.setController(controller);
		impl.addAction(action);
	}
}
