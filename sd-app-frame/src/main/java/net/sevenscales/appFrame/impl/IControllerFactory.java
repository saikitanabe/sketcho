package net.sevenscales.appFrame.impl;


import java.util.List;
import java.util.Map;

public interface IControllerFactory {
	public IController getController(Map requests);
	public IController getController(ClassInfo ci);
	public List getDefaultControllers();
}
