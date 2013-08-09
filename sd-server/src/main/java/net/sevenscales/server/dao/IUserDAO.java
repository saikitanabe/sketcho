package net.sevenscales.server.dao;

import java.util.List;

import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.api.IUser;
import net.sevenscales.domain.api.Member;

import com.google.gwt.user.client.ui.SuggestOracle;


public interface IUserDAO {
  public IUser save(IUser user);
  public IUser find(String username);
  public IUser find(String username, String password);
  public List<Member> findAll(Long projectId);
  public SuggestOracle.Response findAll(SuggestOracle.Request request);
  public List<IUser> findAll();
  public void remove(IUser user);
  public void remove(List<IUser> users);
  public Integer projectUserPermissions(Long projectId, String userName);
  public List<Member> addMember(Long id, IProject project);
  public List<Member> addMember(String username, IProject project);
  public List<Member> removeMember(String username, IProject project);
  public Member addPermission(String username, IProject project, Integer permission);
  public Member deletePermission(String username, IProject project, Integer permission);
  public void register(String userName, String nickName, String password);
  public boolean isEnabled(String username);
  public void setEnabled(String email);
}
