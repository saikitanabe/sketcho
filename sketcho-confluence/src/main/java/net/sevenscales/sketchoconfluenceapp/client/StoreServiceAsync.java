package net.sevenscales.sketchoconfluenceapp.client;

import net.sevenscales.domain.api.IDiagramContent;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface StoreServiceAsync {
  void store(Long pageId, String key, IDiagramContent content, String svg, AsyncCallback<String> callback);
  RequestBuilder load(Long pageId, String key, AsyncCallback<IDiagramContent> callback);
}
