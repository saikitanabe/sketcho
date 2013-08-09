package net.sevenscales.appFrame.impl;

import net.sevenscales.appFrame.api.IContext;
import net.sevenscales.appFrame.api.IContributor;


public abstract class View<T extends IContext> {
	
	protected IController<T> controller;

	public View(IController<T> controller) {
		this.controller = controller;
	}

	public abstract void activate(ITilesEngine tilesEngine, 
								  DynamicParams params, IContributor contributor);
}
