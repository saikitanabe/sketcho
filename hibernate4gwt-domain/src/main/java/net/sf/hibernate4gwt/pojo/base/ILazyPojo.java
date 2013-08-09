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

package net.sf.hibernate4gwt.pojo.base;

import java.util.List;

/**
 * Lazy property handler interface.
 * All the Hibernate POJO must implements this interface.
 * @author bruno.marchesson
 */
public interface ILazyPojo
{
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Add a lazy property
	 */
	public abstract void addLazyProperty(String property);
	
	/**
	 * Remove a lazy property
	 */
	public void removeLazyProperty(String property);

	/**
	 * Indicates if the property is lazy or not
	 * @param property
	 * @return
	 */
	public abstract boolean isLazyProperty(String property);

	/**
	 * Debug method : write the declared lazy properties
	 * @return
	 */
	public abstract String getLazyString();

	/**
	 * @return the lazy properties
	 */
	public List getLazyProperties();

	/**
	 * @param properties the lazy properties to set
	 */
	public void setLazyProperties(List properties);

}