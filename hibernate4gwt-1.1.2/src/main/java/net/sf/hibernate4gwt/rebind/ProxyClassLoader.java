/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.hibernate4gwt.rebind;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import net.sf.hibernate4gwt.rebind.xml.AdditionalCode;
import net.sf.hibernate4gwt.rebind.xml.AdditionalCodeReader;


/**
 * This wrapping class loader is used to generate proxy every time
 * that a IProxy assignable class is loaded
 * @author bruno.marchesson
 *
 */
public class ProxyClassLoader extends URLClassLoader
{
	//----
	// Attributes
	//----
	/**
	 * The wrapped class loader
	 */
	private ClassLoader _wrappedClassLoader;
	
	/**
	 * Indicates if the wrapped classloader is an URL one or not
	 */
	private boolean _isUrlClassLoader;
	
	/**
	 * Map of additional code
	 */
	private Map<String, AdditionalCode> _additionalCodeMap;
	
	//----
	// Properties
	//-----
	/**
	 * Returns the underlying class loader
	 */
	public ClassLoader getWrappedClassLoader()
	{
		return _wrappedClassLoader;
	}

	/**
	 * Changes the underlying class loader
	 * @param classLoader the new class loader to use
	 */
	public void setWrappedClassLoader(ClassLoader classLoader)
	{
		_wrappedClassLoader = classLoader;
		_isUrlClassLoader = (classLoader instanceof URLClassLoader);
	}

	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public ProxyClassLoader(ClassLoader wrappedClassLoader)
	{
		super(new URL[] {});
		setWrappedClassLoader(wrappedClassLoader);
		_additionalCodeMap = new HashMap<String, AdditionalCode>();
		
		// additional code
		try
		{
			// GWT 1.4
			AdditionalCode additionalCode = AdditionalCodeReader.readFromFile(Gwt14ProxyGenerator.ADDITIONAL_CODE);
			_additionalCodeMap.put(additionalCode.getSuffix(), additionalCode);
			
			// GWT 1.5
			additionalCode = AdditionalCodeReader.readFromFile(Gwt15ProxyGenerator.ADDITIONAL_CODE);
			_additionalCodeMap.put(additionalCode.getSuffix(), additionalCode);
		}
		catch (IOException ex)
		{
		//	Should not happen
		//
			throw new RuntimeException("Error reading proxy file", ex);
		}
	}
	
	//-------------------------------------------------------------------------
	//
	// URL Class loader overrides
	//
	//-------------------------------------------------------------------------
	/**
	 * Find Resource simple override
	 */
	public URL findResource(String name)
	{
		if (_isUrlClassLoader)
		{
			return ((URLClassLoader)_wrappedClassLoader).findResource(name);
		}
		else
		{
			return super.findResource(name);
		}
	}
	
	/**
	 * Find Resources simple override
	 */
	public Enumeration<URL> findResources(String name) throws IOException {
		if (_isUrlClassLoader)
		{
			return ((URLClassLoader)_wrappedClassLoader).findResources(name);
		}
		else
		{
			return super.findResources(name);
		}
	}
	
	//-------------------------------------------------------------------------
	//
	// Class loader overrides
	//
	//-------------------------------------------------------------------------
	/**
	 * @param name
	 * @return
	 * @see java.lang.ClassLoader#getResource(java.lang.String)
	 */
	public URL getResource(String name)
	{
		return _wrappedClassLoader.getResource(name);
	}

	/**
	 * @param name
	 * @return
	 * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String name)
	{
		return _wrappedClassLoader.getResourceAsStream(name);
	}

	/**
	 * @param name
	 * @return
	 * @throws IOException
	 * @see java.lang.ClassLoader#getResources(java.lang.String)
	 */
	public Enumeration<URL> getResources(String name) throws IOException
	{
		return _wrappedClassLoader.getResources(name);
	}

	
	/**
	 * Load class wrapping
	 */
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		AdditionalCode additionalCode = getAdditionalCodeFor(name);
		if (additionalCode != null)
		{
		//	Get source class name
		//
			String sourceClassName = getSourceClass(name, additionalCode);
			Class<?> sourceClass = _wrappedClassLoader.loadClass(sourceClassName);
			
		//	Generate proxy 
		//
			return ProxyManager.getInstance().generateProxyClass(sourceClass, additionalCode);
		}
		else
		{
		//	Load class
		//
			return _wrappedClassLoader.loadClass(name);
		}
	}

	//-------------------------------------------------------------------------
	//
	// Internal methods
	//
	//-------------------------------------------------------------------------
	/**
	 * @return the additional code associated with the argument className, or null if any
	 */
	private AdditionalCode getAdditionalCodeFor(String className)
	{
	//	Search for suffix
	//
		for (String suffix : _additionalCodeMap.keySet())
		{
			if (className.endsWith(suffix))
			{
				return _additionalCodeMap.get(suffix);
			}
		}
		
	//	Suffix not found, so no additional code is associated to this class
	//
		return null;
	}
	
	/**
	 * Compute the source class name from the proxy class name and the additional code
	 * @param proxyName
	 * @param additionalCode
	 * @return
	 */
	private String getSourceClass(String proxyName, AdditionalCode additionalCode)
	{
		return proxyName.substring(0, proxyName.length() - additionalCode.getSuffix().length());
	}
}
