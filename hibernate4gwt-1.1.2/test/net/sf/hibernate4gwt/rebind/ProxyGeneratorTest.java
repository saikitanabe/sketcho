package net.sf.hibernate4gwt.rebind;

import java.io.FileNotFoundException;

import junit.framework.TestCase;
import net.sf.hibernate4gwt.pojo.base.ILazyPojo;
import net.sf.hibernate4gwt.rebind.xml.AdditionalCode;
import net.sf.hibernate4gwt.rebind.xml.AdditionalCodeReader;
import net.sf.hibernate4gwt.testApplication.domain.stateful.Message;

import com.google.gwt.dev.shell.ShellGWT;

/**
 * Proxy generation test
 * @author bruno.marchesson
 *
 */
public class ProxyGeneratorTest extends TestCase
{
	//-------------------------------------------------------------------------
	//
	// Required method
	//
	// -------------------------------------------------------------------------
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName()
	{
		return "net.sf.hibernate4gwt.testApplication.TestApplication";
	}
	
	//-------------------------------------------------------------------------
	//
	// Test methods
	//
	//------------------------------------------------------------------------
	/**
	 * Test GWT proxy generation for message
	 */
//	public void testGenerateGwtProxyForMessage()
//	{
//	//	Direct call to Shell GWT (used in hosted mode)
//	//
//		Message messageProxy = (Message) ShellGWT.create(Message.class);
//		assertNotNull(messageProxy);
//		assertTrue(messageProxy instanceof ILazyPojo);
//	}
	
	/**
	 * Test GWT 1.4 proxy generation for Message
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws FileNotFoundException 
	 */
	public void testGenerateJavassistProxyForGWT14() throws InstantiationException, IllegalAccessException, FileNotFoundException
	{
	//	Read additional code
	//
		AdditionalCode additionalCode = AdditionalCodeReader.readFromFile(Gwt14ProxyGenerator.ADDITIONAL_CODE);
	//	Create proxy class
	//
		IServerProxyGenerator generator = new JavassistProxyGenerator();
		Class proxyClass = generator.generateProxyFor(Message.class, additionalCode);
		assertNotNull(proxyClass);
		
	//	Instantiate a new proxy
	//
		Message messageProxy = (Message) proxyClass.newInstance();
		assertNotNull(messageProxy);
		assertTrue(messageProxy instanceof ILazyPojo);
	}
	
	/**
	 * Test GWT 1.4 proxy generation for Message
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws FileNotFoundException 
	 */
	public void testGenerateJavassistProxyForGWT15() throws InstantiationException, IllegalAccessException, FileNotFoundException
	{
	//	Read additional code
	//
		AdditionalCode additionalCode = AdditionalCodeReader.readFromFile(Gwt15ProxyGenerator.ADDITIONAL_CODE);
	//	Create proxy class
	//
		IServerProxyGenerator generator = new JavassistProxyGenerator();
		Class proxyClass = generator.generateProxyFor(Message.class, additionalCode);
		assertNotNull(proxyClass);
		
	//	Instantiate a new proxy
	//
		Message messageProxy = (Message) proxyClass.newInstance();
		assertNotNull(messageProxy);
		assertTrue(messageProxy instanceof ILazyPojo);
	}
}
