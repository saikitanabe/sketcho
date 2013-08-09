package net.sevenscales.serverAPI.remote;

import java.util.List;

import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.api.IUser;
import net.sevenscales.domain.api.Member;
import net.sevenscales.domain.api.ProjectContextDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AdminRemoteAsync {
  public void save(IUser user, AsyncCallback<IUser> async);
  public void findAll(AsyncCallback< List<IUser> > async);
  public void findAll(Long projectId, AsyncCallback< List<Member> > async);
//  public void findAll(SuggestOracle.Request request, AsyncCallback<SuggestOracle.Response> asyncCallback);
  public void remove(IUser user, AsyncCallback async);
  public void removeAll(List<IUser> users, AsyncCallback async);
  public void projectUserPermissions(Long projectId, AsyncCallback<Integer> callback);
  public void addMember(Long id, IProject project, AsyncCallback<List<Member>> callback);
  public void addMember(String username, IProject project, AsyncCallback<List<Member>> callback);
  public void addPermission(String username, IProject project, Integer permission, AsyncCallback<Member> callback);
  public void deletePermission(String username, IProject project, Integer permission, AsyncCallback<Member> callback);
  public void register(String userName, String nickName, String password, AsyncCallback callback);
  public void removeMember(String username, IProject project,
      AsyncCallback<List<Member>> asyncCallback);
  public void openProjectContext(Long projectId, AsyncCallback<ProjectContextDTO> callback);
}
