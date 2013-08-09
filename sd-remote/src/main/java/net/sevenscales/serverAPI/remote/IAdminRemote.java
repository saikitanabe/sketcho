package net.sevenscales.serverAPI.remote;

import java.util.List;

import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.api.IUser;
import net.sevenscales.domain.api.Member;
import net.sevenscales.domain.api.ProjectContextDTO;
import net.sevenscales.domain.dto.SdServerEception;

public interface IAdminRemote {
  public IUser save(IUser user);
  public List<IUser> findAll();
  public List<Member> findAll(Long projectId);
//  public SuggestOracle.Response findAll(SuggestOracle.Request request);
  public void remove(IUser user);
  public void removeAll(List<IUser> users);
  public Integer projectUserPermissions(Long projectId);
  public List<Member> addMember(Long id, IProject project);
  public List<Member> addMember(String username, IProject project);
  public Member addPermission(String username, IProject project, Integer permission);
  public Member deletePermission(String username, IProject project, Integer permission);
  public void register(String userName, String nickName, String password) throws SdServerEception;
  public List<Member> removeMember(String username, IProject project);
  public ProjectContextDTO openProjectContext(Long projectId);
}
