/**
 * 
 */
package net.sf.hibernate4gwt.rebind.xml;

import java.util.List;

/**
 * @author bruno.marchesson
 *
 */
public class AdditionalCode
{
	//----
	// Attributes
	//----
	/**
	 * Suffix for generated classes
	 */
	private String suffix;
	
	/**
	 * Implemented interface
	 */
	private String implementedInterface;
	
	/**
	 * List of additional attributes
	 */
	private List<Attribute> attributes;
	
	/**
	 * List of additional methods
	 */
	private List<Method> methods;

	//----
	// Properties
	//----
	/**
	 * @return the suffix
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	/**
	 * @return the implementedInterfaces
	 */
	public String getImplementedInterface() {
		return implementedInterface;
	}

	/**
	 * @param implementedInterfaces the implementedInterfaces to set
	 */
	public void setImplementedInterfaces(String implementedInterface) {
		this.implementedInterface = implementedInterface;
	}

	/**
	 * @return the attributes
	 */
	public List<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the methods
	 */
	public List<Method> getMethods() {
		return methods;
	}

	/**
	 * @param methods the methods to set
	 */
	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}
}
