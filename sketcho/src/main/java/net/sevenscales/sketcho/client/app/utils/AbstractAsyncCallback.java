package net.sevenscales.sketcho.client.app.utils;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractAsyncCallback<T> implements AsyncCallback<T> {
	public Object data;

	public AbstractAsyncCallback(Object data) {
		this.data = data;
	}
}
