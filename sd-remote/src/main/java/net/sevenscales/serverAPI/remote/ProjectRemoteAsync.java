package net.sevenscales.serverAPI.remote;

import java.util.List;

import net.sevenscales.domain.api.IProject;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProjectRemoteAsync {
  public void save(IProject page, AsyncCallback<IProject> async);
  public void update(IProject page, AsyncCallback<IProject> async);
  public void open(Long id, AsyncCallback<IProject> async);
  public void findAll(AsyncCallback< List<IProject> > async);
  public void findAllPublicProjects(AsyncCallback< List<IProject> > async);
}
