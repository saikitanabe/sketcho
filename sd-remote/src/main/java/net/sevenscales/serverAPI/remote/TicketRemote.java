package net.sevenscales.serverAPI.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ticket.rpc")
public interface TicketRemote extends ITicketRemote, RemoteService {

    /**
     * Utility class for simplifing access to the instance of async service.
     */
    public static class Util {
      public static TicketRemoteAsync inst;
      static {
        inst = (TicketRemoteAsync) GWT.create(TicketRemote.class);
      }
    }
    
}
