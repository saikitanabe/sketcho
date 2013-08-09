package net.sevenscales.appFrame.impl;


public abstract class HandlerBase implements Handler {
	protected DynamicParams params;
	
	public void addParams(DynamicParams params) {
		this.params = params;
	}

}
