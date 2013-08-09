package net.sevenscales.server.dao.hibernate;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.api.IUser;
import net.sevenscales.domain.api.Member;
import net.sevenscales.domain.api.MemberSuggestion;
import net.sevenscales.server.acl.Authority;
import net.sevenscales.server.dao.IUserDAO;
import net.sevenscales.server.domain.Project;
import net.sevenscales.server.domain.User;

import org.acegisecurity.acls.AccessControlEntry;
import org.acegisecurity.acls.Acl;
import org.acegisecurity.acls.MutableAcl;
import org.acegisecurity.acls.MutableAclService;
import org.acegisecurity.acls.NotFoundException;
import org.acegisecurity.acls.Permission;
import org.acegisecurity.acls.domain.AclImpl;
import org.acegisecurity.acls.domain.BasePermission;
import org.acegisecurity.acls.objectidentity.ObjectIdentity;
import org.acegisecurity.acls.objectidentity.ObjectIdentityImpl;
import org.acegisecurity.acls.sid.PrincipalSid;
import org.acegisecurity.acls.sid.Sid;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.codec.binary.Hex;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

@Transactional(readOnly=true)
public class UserDAO extends HibernateDaoSupport implements IUserDAO {
  private MutableAclService mutableAclService;

  public void setMutableAclService(MutableAclService mutableAclService) {
    this.mutableAclService = mutableAclService;
  }

//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IUser save(IUser user) {
    getHibernateTemplate().save(user);
    return user;
  }

  @Transactional(propagation=Propagation.SUPPORTS)
  public IUser find(String username) {
    String[] keys = {"username"};
    String[] values = {username};
    List<User> match = getHibernateTemplate().findByNamedParam
      ("from users u where u.username=:username", keys, values);
    
    if (match.size() == 1) {
      return match.get(0);
    }
    
    return null;
  }
  
//  @Override
  @Transactional(propagation=Propagation.SUPPORTS)
  public IUser find(String username, String password) {
    String[] keys = {"username", "password"};
    String[] values = {username, password};
    List<IUser> match = getHibernateTemplate().findByNamedParam
      ("from User u where u.userId=:username and u.password=:password", keys, values);
    
    if (match.size() == 1) {
      return match.get(0);
    }
    return null;
  }
  
  @Transactional(propagation=Propagation.SUPPORTS)
  public boolean isEnabled(String username) {
    String[] keys = {"username"};
    String[] values = {username};
    List<User> match = getHibernateTemplate().findByNamedParam
      ("from users u where u.username=:username", keys, values);
    
    if (match.size() == 1) {
      return match.get(0).getEnabled();
    }
    return false;
  }
  
  @Transactional(propagation=Propagation.REQUIRED)
  public void setEnabled(String email) {
    User user = (User) find(email);
    if (user != null) {
      user.setEnabled(true);
      getHibernateTemplate().update(user);
    }
  }
  
//  @Override
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IUser> findAll() {
    return getHibernateTemplate().find("from users u order by u.username asc");
  }
  
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<Member> findAll(Long projectId) {
    Project project = (Project) getHibernateTemplate().get(Project.class, projectId);
    return findAll(project);
  }

  public List<Member> findAll(IProject project) {
    List<String> usernames = getHibernateTemplate().findByNamedParam(
        "select e.sid.sid from acl_entry e where e.objectIdentity.objectIdentity=:id order by e.sid.sid", 
        "id", project.getId());
    
//    List<String> owner = (List<String>) getHibernateTemplate().findByNamedParam(
//        "select i.ownerSid.sid from acl_object_identity i where i.objectIdentity=:projectId", 
//        "projectId", project.getId() );

    List<Member> members = new ArrayList<Member>();
    for (String u : usernames) {
      ObjectIdentity oid = new ObjectIdentityImpl(project);
      Member member = createMember(u, oid);
      // do not filter owner away
      if (!members.contains(member)) {
        members.add(member);
      }
//      if (!owner.get(0).equals(u) && !members.contains(member)) {
//        // filters owner away
//        members.add(member);
//      }
    }
    
    return members;
  }

  @Transactional(propagation=Propagation.SUPPORTS)
  public SuggestOracle.Response findAll(SuggestOracle.Request request) {
    String text = request.getQuery();
    String query = "from users u where u.username like :text";
    List<User> users = (List<User>) getHibernateTemplate().findByNamedParam(query, "text", '%' + text + '%');
    
    List<Suggestion> suggestions = new ArrayList<Suggestion>();
    for (User u : users) {
      if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(u.getUsername())) {
        // doesn't find my self (not really according to method semantics)!!
        suggestions.add(new MemberSuggestion(u.getId(), encodeMemberName(u.getUsername())));
      }
    }
    
    SuggestOracle.Response result = new SuggestOracle.Response();
    result.setSuggestions(suggestions);
    return result;
  }
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public void remove(IUser user) {
    getHibernateTemplate().delete(user);
  }
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public void remove(List<IUser> users) {
    getHibernateTemplate().deleteAll(users);
  }

  @Transactional(propagation=Propagation.SUPPORTS)
  public Integer projectUserPermissions(Long projectId, String userName) {
//    String[] keys = {"userName"};
//    String[] values = {userName};

//    List<User> users = (List<User>) getHibernateTemplate().findByNamedParam("from users u where u.username=:userName", keys, values);
//    
//    String[] k2 = {"userId", "projectId"};
//    Long[] v2 = {users.get(0).getId(), projectId};
//    List<AclEntry> entry = (List<AclEntry>) getHibernateTemplate()
//      .findByNamedParam("from acl_entry e where e.sid.id=:userId and e.objectIdentity.objectIdentity=:projectId", 
//          k2, v2);
    
    ObjectIdentity oid = new ObjectIdentityImpl(Project.class, projectId);
    PrincipalSid principal = new PrincipalSid(userName);
    
    Sid[] sids = new Sid[]{principal};
    MutableAcl acl = (MutableAcl) mutableAclService.readAclById(oid, sids);
    
    int result = 0;
    for (AccessControlEntry entry : acl.getEntries()) {
        if (entry.getSid().equals(principal)) {
          result |= entry.getPermission().getMask();
        }
    }

    return result;
  }
  
  @Transactional(propagation=Propagation.REQUIRED)
  public List<Member> addMember(Long id, IProject project) {
    List<String> usernames = getHibernateTemplate().findByNamedParam(
        "select u.username from users u where u.id=:id", 
        "id", id);

    if (usernames.size() != 1) {
      throw new NotFoundException("Unable to find "+id);
    }
    
    AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(project));
    acl.insertAce(null, BasePermission.READ, new PrincipalSid(usernames.get(0)), true);
    mutableAclService.updateAcl(acl);
    return findAll(project);
  }
  
  @Transactional(propagation=Propagation.REQUIRED)
  public List<Member> addMember(String username, IProject project) {
    List<String> usernames = getHibernateTemplate().findByNamedParam(
        "select u.username from users u where u.username=:username", 
        "username", username);
    if (usernames.size() == 0) {
      throw new NotFoundException("Unable to find "+username);
    }
    
    AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(project));
    acl.insertAce(null, BasePermission.READ, new PrincipalSid(username), true);
    mutableAclService.updateAcl(acl);
    return findAll(project);
  }
  
  @Transactional(propagation=Propagation.REQUIRED)
  public List<Member> removeMember(String username, IProject project) {
    ObjectIdentity oid = new ObjectIdentityImpl(project);
    PrincipalSid principal = new PrincipalSid(username);
    
    Sid[] sids = new Sid[]{principal};
    MutableAcl acl = (MutableAcl) mutableAclService.readAclById(oid, sids);
    
    for (AccessControlEntry entry : acl.getEntries()) {
        if (entry.getSid().equals(principal)) {
            acl.deleteAce(entry.getId());
        }
    }

    mutableAclService.updateAcl(acl);
    return findAll(project);
  }

  @Transactional(propagation=Propagation.REQUIRED)
  public Member addPermission(String username, IProject project,
      Integer permission) {
    ObjectIdentity oid = new ObjectIdentityImpl(project);
    AclImpl acl = (AclImpl) mutableAclService.readAclById(oid);
    acl.insertAce(null, map(permission), new PrincipalSid(username), true);
    mutableAclService.updateAcl(acl);

    Member result = createMember(username, oid);
    return result;
  }
  
  @Transactional(propagation=Propagation.REQUIRED)
  public Member deletePermission(String username, IProject project, Integer permission) {
    ObjectIdentity oid = new ObjectIdentityImpl(project);
    MutableAcl acl = (MutableAcl) mutableAclService.readAclById(oid);
    
    AccessControlEntry[] entries = acl.getEntries();
    PrincipalSid principal = new PrincipalSid(username);
    for (int i = 0; i < entries.length; i++) {
        if (entries[i].getSid().equals(principal) && entries[i].getPermission().equals(map(permission))) {
            acl.deleteAce(entries[i].getId());
        }
    }

    mutableAclService.updateAcl(acl);
    Member result = createMember(username, oid);
    return result;
  }
  
  @Transactional(propagation=Propagation.REQUIRED)
  public void register(String userName, String nickName, String password) {
    User user = new User();
    user.setUsername(userName);
    user.setPassword(toMd5(password));
    user.setEnabled(false);
    getHibernateTemplate().save(user);
    
    Authority a = new Authority();
    a.setAuthority("ROLE_USER");
    a.setUser(user);
    getHibernateTemplate().save(a);
  }
  
  private String toMd5(String password) {
    MessageDigest algorithm;
    try {
      algorithm = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException();
    }
    algorithm.reset();
    byte[] digest = algorithm.digest(password.getBytes());
    return new String(Hex.encodeHex(digest));
  }

  private Permission map(Integer permission) {
    if (permission.equals(BasePermission.WRITE.getMask())) {
      return BasePermission.WRITE;
    } else if (permission.equals(BasePermission.DELETE.getMask())) {
      return BasePermission.DELETE;
    } 
   return null;
  }
  
  private Member createMember(String username, ObjectIdentity oid) {
    Sid[] sids = new Sid[]{ new PrincipalSid(username) };
    Acl a = mutableAclService.readAclById(oid, sids);

    String memberName = encodeMemberName(username);
    List<Long> ids = getHibernateTemplate().findByNamedParam(
        "select u.id from users u where u.username=:username", 
        "username", username);

    if (ids.size() != 1) {
      throw new NotFoundException("Unable to find unique "+username);
    }
    Member result = new Member(ids.get(0), memberName);
    PrincipalSid principal = new PrincipalSid(username);

    for (AccessControlEntry entry : a.getEntries()) {
      if (entry.getSid().equals(principal)) {
        result.setPermissions( result.getPermissions() | entry.getPermission().getMask() );
      }
    }
    return result;
  }

  private String encodeMemberName(String username) {
	  if (true) {
		  return username;
	  }
	  // TODO: encode member name at some other time
    String result = username;
    // encrypt domain and three last chars if email
    Pattern p = Pattern.compile("(.+)@(.+\\.[a-z]+)");
    Matcher m = p.matcher(username);
    if (m.matches()) {
      String domain = toMd5("rand0145"+m.group(2));
      result = m.group(1)+"@"+domain;
    }
    return result;
  }

}
