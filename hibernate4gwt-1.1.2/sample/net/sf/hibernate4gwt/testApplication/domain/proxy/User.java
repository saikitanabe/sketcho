package net.sf.hibernate4gwt.testApplication.domain.proxy;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.domain.IUser;

/**
 * User Domain class for statefull server
 */
public class User implements Serializable, IUser
{
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 7239533396817246837L;
	
	// Fields
	private Integer id;
	private Integer version;
	
	private String login;
	private String firstName;
	private String lastName;
	private String password;
	
	/**
	 * @gwt.typeArgs <net.sf.hibernate4gwt.testApplication.domain.proxy.Message>
	 */
	private Set messageList;


	// Properties
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#getId()
	 */
	public Integer getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#getVersion()
	 */
	public Integer getVersion() {
		return version;
	}

	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#setVersion(java.lang.Integer)
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#getLogin()
	 */
	public String getLogin() {
		return this.login;
	}

	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#setLogin(java.lang.String)
	 */
	public void setLogin(String surname) {
		this.login = surname;
	}

	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#getFirstName()
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#setFirstName(java.lang.String)
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#getLastName()
	 */
	public String getLastName() {
		return this.lastName;
	}

	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#setLastName(java.lang.String)
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#getPassword()
	 */
	public String getPassword() {
		return this.password;
	}

	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.stateful.IUser#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @gwt.typeArgs <net.sf.hibernate4gwt.testApplication.domain.proxy.Message>
	 * @return the message List
	 */
	public Set getMessageList() {
		return messageList;
	}

	/**
	 * @gwt.typeArgs messageList <net.sf.hibernate4gwt.testApplication.domain.proxy.Message>
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
		// Bi-directional association
		((Message)message).setAuthor(this);
		
		// Create message list if needed
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
	}
}
