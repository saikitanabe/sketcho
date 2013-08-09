package net.sevenscales.serverAPI.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("project.rpc")
public interface ProjectRemote extends IProjectRemote, RemoteService {

    /**
     * Utility class for simplifing access to the instance of async service.
     */
    public static class Util {
      public static ProjectRemoteAsync inst;
      static {
//        @RemoteServiceRelativePath("ProjectRemote")
        inst = (ProjectRemoteAsync) GWT.create(ProjectRemote.class);
      }
    }
    
//    public IProject save(IProject page);
//    public IProject open(Long id);
//    public List<IProject> findAll();
//    public ProjectPageDTO addPage(IPage page, IProject project);
//    public ProjectPageDTO addPage(IPage page, Long projectId);

}
