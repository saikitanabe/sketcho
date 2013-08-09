package net.sevenscales.appFrame.impl;

public class GlobalState {

	private int registeredActionId;

	public void registerAction(int actionId) {
		this.registeredActionId = actionId;
	}
	
	public boolean isActive(Action action) {
		if (action.getId() == registeredActionId) {
			return true;
		}
		return false;
	}
}
