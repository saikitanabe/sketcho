package net.sevenscales.serverAPI.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("page.rpc")
public interface PageRemote extends IPageRemote, RemoteService {

    /**
     * Utility class for simplifing access to the instance of async service.
     */
    public static class Util {
      public static PageRemoteAsync inst;
      static {
        inst = (PageRemoteAsync) GWT.create(PageRemote.class);
      }
    }
    
//    public IPage save(IPage page);
//    public IPage open(Long id);
//    public List<IPage> findAll();
//    public PageContentDTO addContent(IContent content, IPage page);

}
