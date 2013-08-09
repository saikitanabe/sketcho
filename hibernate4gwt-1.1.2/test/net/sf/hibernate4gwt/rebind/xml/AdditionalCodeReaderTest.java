/**
 * 
 */
package net.sf.hibernate4gwt.rebind.xml;

import java.io.FileNotFoundException;

import junit.framework.TestCase;

/**
 * Test of reading of Additional code xml file
 * @author bruno.marchesson
 *
 */
public class AdditionalCodeReaderTest extends TestCase {

	/**
	 * Test method for {@link net.sf.hibernate4gwt.rebind.xml.AdditionalCodeReader#readFromFile(java.lang.String)}.
	 * @throws FileNotFoundException 
	 */
	public final void testReadFromFileGWT14() throws FileNotFoundException 
	{
		AdditionalCode additionalCode = AdditionalCodeReader.readFromFile("net/sf/hibernate4gwt/rebind/xml/LazyPojo.java14.xml");
		assertNotNull(additionalCode);
		assertNotNull(additionalCode.getImplementedInterface());
		
		assertNotNull(additionalCode.getMethods());
		assertFalse(additionalCode.getMethods().isEmpty());
	}

	/**
	 * Test method for {@link net.sf.hibernate4gwt.rebind.xml.AdditionalCodeReader#readFromFile(java.lang.String)}.
	 * @throws FileNotFoundException 
	 */
	public final void testReadFromFileGWT15() throws FileNotFoundException 
	{
		AdditionalCode additionalCode = AdditionalCodeReader.readFromFile("net/sf/hibernate4gwt/rebind/xml/LazyPojo.java5.xml");
		assertNotNull(additionalCode);
		assertNotNull(additionalCode.getImplementedInterface());
		
		assertNotNull(additionalCode.getMethods());
		assertFalse(additionalCode.getMethods().isEmpty());
		
		assertNotNull(additionalCode.getAttributes());
		assertFalse(additionalCode.getAttributes().isEmpty());
		
		Attribute attribute = additionalCode.getAttributes().get(0);
		assertNotNull(attribute.getCollectionType());
		
	}
}
