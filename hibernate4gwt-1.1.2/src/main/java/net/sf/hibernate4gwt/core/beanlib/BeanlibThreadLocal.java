/**
 * 
 */
package net.sf.hibernate4gwt.core.beanlib;

import net.sf.beanlib.utils.ClassUtils;

/**
 * Thread local to store BeanLib additional parameters
 * @author bruno.marchesson
 *
 */
public class BeanlibThreadLocal
{
	//----
	// Attributes
	//----
	/**
	 * Target merge bean (no new instance needed)
	 */
	private static ThreadLocal<Object> destinationBean = new ThreadLocal<Object>();

	//----
	// Properties
	//----
	/**
	 * @return the destinationBean
	 */
	public static Object getDestinationBean() {
		return destinationBean.get();
	}

	/**
	 * @param destinationBean the destinationBean to set
	 */
	public static void setDestinationBean(Object destinationBean)
	{
		if ((destinationBean == null) ||
			(ClassUtils.immutable(destinationBean.getClass()) == true))
		{
		//	Do not store primitive type
		//
			BeanlibThreadLocal.destinationBean.set(null);
		}
		else
		{
			BeanlibThreadLocal.destinationBean.set(destinationBean);
		}
	}
}
