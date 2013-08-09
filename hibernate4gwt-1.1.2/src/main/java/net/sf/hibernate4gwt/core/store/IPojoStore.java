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

package net.sf.hibernate4gwt.core.store;

import net.sf.hibernate4gwt.core.IPersistenceUtil;

/**
 * Handler for POJO storage and recovery
 * @author bruno.marchesson
 *
 */
public interface IPojoStore
{
	//----
	// Properties
	//----
	/**
	 * @return the associated persistence util class
	 */
	public IPersistenceUtil getPersistenceUtil();

	/**
	 * @param persistenceUtil the persistence Util instance to set
	 */
	public void setPersistenceUtil(IPersistenceUtil persistenceUtil);

	//-------------------------------------------------------------------------
	//
	// Store and restore interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Indicates if the store is stateless or stateful
	 * @return true if the store is stateless, false otherwise
	 */
	public boolean isStateless();
	
	/**
	 * Store the argument object
	 */
	public void store(Object object);
	
	/**
	 * Remove the argument object
	 */
	public void remove(Object object);
	
	/**
	 * Restore an object from its clone and class
	 * @param clone the clone or DTO from GWT
	 * @param hibernateClass the searched hibernate class
	 * @return the Hibernate POJO
	 */
	public Object restore(Object clone, Class<?> hibernateClass);
	
	//---------------------------------------------------------------------------
	//
	// Listeners
	//
	//----------------------------------------------------------------------------
	/**
	 * Extension point called before restore (and merge)
	 */
	public void beforeRestore();
	
	/**
	 * Extension point called after restore (and merge)
	 */
	public void afterRestore();
}
