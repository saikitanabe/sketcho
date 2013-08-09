package net.sf.hibernate4gwt.testApplication.client.configuration;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConfigurationRemoteAsync
{
	/**
	 * Returns the server configuration
	 */
	public void getServerConfiguration(AsyncCallback callback);
}
