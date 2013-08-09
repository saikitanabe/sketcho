package net.sf.hibernate4gwt.testApplication.domain;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Server configuration
 * @author bruno.marchesson
 */
public class Configuration implements IsSerializable
{
	//----
	// Attributes
	//----
	/**
	 * Configuration name
	 */
	private String name;
	
	/**
	 * The Spring context file
	 */
	private String springContextFile;
	

	//----
	// Properties
	//----
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the springContextFile
	 */
	public String getSpringContextFile() {
		return springContextFile;
	}

	/**
	 * @param springContextFile the springContextFile to set
	 */
	public void setSpringContextFile(String springContextFile) {
		this.springContextFile = springContextFile;
	}

}
