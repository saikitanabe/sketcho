package net.sevenscales.serverAPI.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("admin.rpc")
public interface AdminRemote extends IAdminRemote, RemoteService {

    /**
     * Utility class for simplifing access to the instance of async service.
     */
    public static class Util {
      public static AdminRemoteAsync inst;
      static {
        inst = (AdminRemoteAsync) GWT.create(AdminRemote.class);
      }
    }

}
