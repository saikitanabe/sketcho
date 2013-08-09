package net.sevenscales.serverAPI.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("label.rpc")
public interface LabelRemote extends ILabelRemote, RemoteService {

    /**
     * Utility class for simplifing access to the instance of async service.
     */
    public static class Util {
      public static LabelRemoteAsync inst;
      static {
        inst = (LabelRemoteAsync) GWT.create(LabelRemote.class);
      }
    }

}
