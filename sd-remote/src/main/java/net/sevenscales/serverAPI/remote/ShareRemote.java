package net.sevenscales.serverAPI.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("share.rpc")
public interface ShareRemote extends IShareRemote, RemoteService {

    /**
     * Utility class for simplifing access to the instance of async service.
     */
    public static class Util {
      public static ShareRemoteAsync inst;
      static {
        inst = (ShareRemoteAsync) GWT.create(ShareRemote.class);
      }
    }
    
}
