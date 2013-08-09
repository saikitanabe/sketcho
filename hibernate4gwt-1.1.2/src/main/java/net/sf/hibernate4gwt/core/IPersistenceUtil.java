package net.sf.hibernate4gwt.core;

import java.io.Serializable;

public interface IPersistenceUtil {

	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * @return the ID of the argument Hibernate Pojo
	 */
	public abstract Serializable getId(Object pojo);

	/**
	 * @return the ID of the argument DTO with the same name than the persistent class
	 */
	public abstract Serializable getId(Object pojo, Class<?> persistentClass);

	/**
	 * Indicates if the pojo is persistent or not
	 */
	public abstract boolean isPersistentPojo(Object pojo);

	/**
	 * Indicates if the class is managed by the persistence container of not
	 */
	public abstract boolean isPersistentClass(Class<?> clazz);
	
	/**
	 * Indicates if the collection class is managed by the persistence container of not
	 */
	public abstract boolean isPersistentCollection(Class<?> clazz);

	/**
	 * @return the underlying persistent class
	 */
	public abstract Class<?> getPersistentClass(Class<?> clazz);

	/**
	 * Indicated if the argument class is enhanced or not
	 * @param clazz the persistent class
	 * @return true is the class is enhanced, false otherwise
	 */
	public abstract boolean isEnhanced(Class<?> clazz);

	/**
	 * Open a new session
	 * @return the opened session
	 */
	public abstract void openSession();

	/**
	 * Open a new session
	 * @return the opened session
	 */
	public abstract void closeCurrentSession();
	
	/**
	 * Load a fresh instance of the persistent Pojo
	 * @param clonePojo the clone pojo (needed for ID)
	 * @param persistentClass the persistent class
	 * @return the loaded instance
	 */
	public abstract Object load(Serializable id, Class<?> persistentClass);
	
	/**
	 * Indicates it the argument is a proxy or not
	 * @param proxy
	 * @return
	 */
	public abstract boolean isProxy(Object proxy);
	
	/**
	 * Initialize the argument proxy
	 * @param proxy
	 * @return
	 */
	public abstract void initializeProxy(Object proxy);

}