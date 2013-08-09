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

package net.sf.hibernate4gwt.pojo.java14;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.hibernate4gwt.pojo.base.ILazyPojo;

/**
 * Abstract POJO for GWT with lazy property handling
 * @author bruno.marchesson
 *
 */
public abstract class LazyPojo implements ILazyPojo, Serializable
{
	//-----
	// Attributes
	//-----
	/**
	 * List of lazy but not loaded properties
	 * @gwt.typeArgs <java.lang.String>
	 */
	protected List _lazyProperties;
	
	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public LazyPojo()
	{
		super();
		_lazyProperties = new ArrayList();
	}

	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * @return the lazy properties
	 * @gwt.typeArgs <java.lang.String>
	 */
	public List getLazyProperties()
	{
		return _lazyProperties;
	}

	/**
	 * @param properties the lazy properties to set
	 * @gwt.typeArgs properties <java.lang.String>
	 */
	public void setLazyProperties(List properties)
	{
		_lazyProperties = properties;
	}

	/* (non-Javadoc)
	 * @see org.dotnetguru.lazykiller.pojo.ILazyPojo#addLazyProperty(java.lang.String)
	 */
	public void addLazyProperty(String property)
	{
		_lazyProperties.add(property);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.pojo.ILazyPojo#removeLazyProperty(java.lang.String)
	 */
	public void removeLazyProperty(String property)
	{
		_lazyProperties.remove(property);
	}
	
	/* (non-Javadoc)
	 * @see org.dotnetguru.lazykiller.pojo.ILazyPojo#isLazyProperty(java.lang.String)
	 */
	public boolean isLazyProperty(String property)
	{
		return _lazyProperties.contains(property);
	}
	
	/* (non-Javadoc)
	 * @see org.dotnetguru.lazykiller.pojo.ILazyPojo#getLazyString()
	 */
	public String getLazyString()
	{
		StringBuffer result = new StringBuffer();
		
		for (int index = 0 ; index < _lazyProperties.size() ; index ++)
		{
			result.append(_lazyProperties.get(index));
			result.append(' ');
		}
		
		return result.toString();
	}
}
