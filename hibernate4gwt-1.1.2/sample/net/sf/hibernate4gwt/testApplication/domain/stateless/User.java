package net.sf.hibernate4gwt.testApplication.domain.stateless;

import java.util.HashSet;
import java.util.Set;

import net.sf.hibernate4gwt.pojo.java14.LazyPojo;
import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.domain.IUser;

/**
 * User Domain class for stateless server
 */
public class User extends LazyPojo implements IUser
{
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 1058354709157710766L;
	
	// Fields
	private Integer id;
	private Integer version;
	
	private String login;
	private String firstName;
	private String lastName;
	private String password;
	
	/**
	 * @gwt.typeArgs <net.sf.hibernate4gwt.testApplication.domain.stateless.Message>
	 */
	private Set messageList;

	// Properties
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String surname) {
		this.login = surname;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

	/**
	 * @gwt.typeArgs <net.sf.hibernate4gwt.testApplication.domain.stateless.Message>
	 * @return the message List
	 */
	public Set getMessageList() {
		return messageList;
	}

	/**
	 * @gwt.typeArgs messageList <net.sf.hibernate4gwt.testApplication.domain.stateless.Message>
	 * @param messageList the message List to set
	 */
	public void setMessageList(Set messageList) {
		this.messageList = messageList;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.IUser#addMessage(net.sf.hibernate4gwt.testApplication.domain.IMessage)
	 */
	public void addMessage(IMessage message)
	{
		((Message)message).setAuthor(this);
		if (messageList == null)
		{
			messageList = new HashSet();
		}
		messageList.add(message);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.IUser#removeMessage(net.sf.hibernate4gwt.testApplication.domain.IMessage)
	 */
	public void removeMessage(IMessage message)
	{
		messageList.remove(message);
//		((Message)message).setAuthor(null);
	}
}
