package net.sf.hibernate4gwt.testApplication.domain.stateful;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.domain.IUser;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Message Java 1.4 domain class for stateful pojo store
 * This class has no inheritance on hibernate4gwt, but must be Serializable for GWT RPC serialization
 * @author bruno.marchesson
 *
 */
public class Message implements Serializable, IsSerializable, IMessage
{
	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 3421537443957416948L;
	
	//	Fields    
    private int id;
    private Integer version;
    private String message;
    private Date date;
    
    private User author;
    
    /**
     * @gwt.typeArgs <java.lang.String, java.lang.Integer>
     */
    private Map keywords;
    
    // Properties
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.IMessage#getId()
	 */
	public final int getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.IMessage#setId(java.lang.Integer)
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
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.IMessage#getMessage()
	 */
	public String getMessage() {
		return message;
	}
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.IMessage#setMessage(java.lang.String)
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.IMessage#getDate()
	 */
	public Date getDate() {
		return date;
	}
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.domain.IMessage#setDate(java.util.Date)
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
	public void setAuthor(User author) {
		this.author = author;
	}
	
	/**
	 * @see net.sf.hibernate4gwt.testApplication.domain.IMessage#getKeywords()
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
		Message other = (Message) obj;
		return (id == other.getId());
	}
}
