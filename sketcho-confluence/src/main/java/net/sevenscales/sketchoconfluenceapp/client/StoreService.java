package net.sevenscales.sketchoconfluenceapp.client;

import net.sevenscales.domain.api.IDiagramContent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("store")
public interface StoreService extends RemoteService {
  public static class Util {
    public static final StoreServiceAsync service = GWT.create(StoreService.class);
//    public static String service_url = GWT.getHostPageBaseURL()+"sketcho_confluence_app/storeService"; 
  }
  
  String store(Long pageId, String key, IDiagramContent content, String svg);
  IDiagramContent load(Long pageId, String key);
}
