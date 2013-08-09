package net.sf.hibernate4gwt.testApplication.domain;

import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Common interface for all user implementations.
 * Not needed in production, it is just used for unified testing between different configurations
 * (stateful, stateless, proxy and Java5)
 * @author bruno.marchesson
 *
 */
public interface IUser extends IsSerializable{

	public Integer getId();

	public void setId(Integer id);

	public Integer getVersion();

	public void setVersion(Integer version);

	public String getLogin();

	public void setLogin(String surname);

	public String getFirstName();

	public void setFirstName(String firstName);

	public String getLastName();

	public void setLastName(String lastName);

	public String getPassword();

	public void setPassword(String password);
	
	public Set getMessageList();
	
	public void addMessage(IMessage message);
	
	public void removeMessage(IMessage message);

}