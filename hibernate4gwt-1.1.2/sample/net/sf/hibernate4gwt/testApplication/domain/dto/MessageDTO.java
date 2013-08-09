package net.sf.hibernate4gwt.testApplication.domain.dto;

import java.util.Date;
import java.util.Map;

import net.sf.hibernate4gwt.pojo.java14.LazyPojo;
import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.domain.IUser;

/**
 * DTO Message class for Java5 support
 * This class just has to inherit from LazyGwtPojo
 * It is also used as DTO for the Java5 Message POJO
 * @author bruno.marchesson
 *
 */
public class MessageDTO extends LazyPojo implements IMessage
{
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 3445339493203407152L;
	
	//	Fields    
    private int id;
    private Integer version;
    private String message;
    private Date date;
    
    private UserDTO author;
    
    /**
     * @gwt.typeArgs <java.lang.String, java.lang.Integer>
     */
    private Map keywords;
    
    // Properties
	/**
	 * @return the id
	 */
	public final int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public final void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the timeStamp
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setDate(Date timeStamp) {
		this.date = timeStamp;
	}
	/**
	 * @return the author
	 */
	public IUser getAuthor() {
		return author;
	}
	/**
	 * @param author the author to set
	 */
	public void setAuthor(UserDTO author) {
		this.author = (UserDTO) author;
	}
	
	/**
	 * @see net.sf.hibernate4gwt.testApplication.domain.IMessage#getKeywords(
	 * @gwt.typeArgs <java.lang.String, java.lang.Integer>
	 */
	public Map getKeywords() {
		return keywords;
	}
	
	/**
	 * @see net.sf.hibernate4gwt.testApplication.domain.IMessage#setKeywords(java.util.Map)
	 * @gwt.typeArgs keywords <java.lang.String, java.lang.Integer>
	 */
	public void setKeywords(Map keywords)
	{
		this.keywords = keywords;
	}
	
	/**
	 * Equality function
	 */
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		else if (this == obj)
		{
			return true;
		}
		
		// ID comparison
		MessageDTO other = (MessageDTO) obj;
		return (id == other.getId());
	}
}