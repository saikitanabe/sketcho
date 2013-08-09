package net.sevenscales.serverAPI.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("content.rpc")
public interface ContentRemote extends IContentRemote, RemoteService {

    /**
     * Utility class for simplifing access to the instance of async service.
     */
    public static class Util {
      public static ContentRemoteAsync inst;
      static {
        inst = (ContentRemoteAsync) GWT.create(ContentRemote.class);
      }
    }
    
//    public IPage save(IPage page);
//    public IPage open(Long id);
//    public List<IPage> findAll();
//    public PageContentDTO addContent(IContent content, IPage page);

}
