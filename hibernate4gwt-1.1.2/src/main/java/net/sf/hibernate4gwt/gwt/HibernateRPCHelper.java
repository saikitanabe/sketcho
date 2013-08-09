/**
 * 
 */
package net.sf.hibernate4gwt.gwt;

import javax.servlet.http.HttpSession;

import net.sf.hibernate4gwt.core.HibernateBeanManager;
import net.sf.hibernate4gwt.core.beanlib.mapper.ProxyClassMapper;
import net.sf.hibernate4gwt.core.store.stateful.HttpSessionPojoStore;
import net.sf.hibernate4gwt.exception.NotAssignableException;
import net.sf.hibernate4gwt.exception.TransientHibernateObjectException;
import net.sf.hibernate4gwt.rebind.ProxyClassLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RPCRequest;

/**
 * Static helper class for HibernateRemoteService and HibernateRPCServiceExporter (GWT-SL)
 * @author bruno.marchesson
 *
 */
public class HibernateRPCHelper
{
	//----
	// Attributes
	//----
	/**
	 * The log channel
	 */
	private static Log log = LogFactory.getLog(HibernateRPCHelper.class);
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Proxy class loader initialisation
	 */
	public static void initClassLoader()
	{
		if (log.isDebugEnabled())
		{
			log.debug("initClassLoader");
		}
		
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if (contextClassLoader instanceof ProxyClassLoader == false)
		{
		// 	Set Proxy class loader
		//
			if (log.isDebugEnabled())
			{
				log.debug("Replaced context class loader with ProxyClassLoader");
			}
			
			Thread.currentThread().setContextClassLoader(
							new ProxyClassLoader(contextClassLoader));
		}
	}
	
	/**
	 * Parse RPC input parameters.
	 * Must be called before GWT service invocation.
	 * @param rpcRequest the input GWT RPC request
	 * @param beanManager the Hibernate bean manager
	 * @param session the HTTP session (for HTTP Pojo store)
	 */
	public static void parseInputParameters(RPCRequest rpcRequest, 
											HibernateBeanManager beanManager,
											HttpSession session)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Parse input parameters for request " + rpcRequest.toString());
		}
		
	//	init class loader if in proxy mode
	//
		if ((beanManager.getClassMapper() != null) &&
			(beanManager.getClassMapper() instanceof ProxyClassMapper))
		{
			initClassLoader();
		}
				
	//	Set HTTP session of Pojo store in thread local
	//
		HttpSessionPojoStore.setHttpSession(session);
		
	//	Merge parameters if needed
	//
		if ((rpcRequest != null) &&
			(rpcRequest.getParameters() != null))
		{
			Object[] parameters = rpcRequest.getParameters();
			for (int index = 0 ; index < parameters.length; index ++)
			{
				if (parameters[index] != null)
				{
					try
					{
						parameters[index] = beanManager.merge(parameters[index], true);
					}
					catch (NotAssignableException ex)
					{
						log.debug(parameters[index] + " not assignable");
					}
					catch (TransientHibernateObjectException ex)
					{
						log.info(parameters[index] + " is transient : cannot merge...");
					}
				}
			}
		}
	}
	
	/**
	 * Clone the service result.
	 * Must be called after successful service invocation
	 * @param returnValue the service return value
	 * @param beanManager the Hibernate bean manager
	 * @return the cloned service value
	 */
	public static final Object parseReturnValue(Object returnValue,
											    HibernateBeanManager beanManager)
	{
	//	Clone if needed
	//
		if (returnValue != null)
		{
			try
			{
				returnValue = beanManager.clone(returnValue, true);
			}
			catch (NotAssignableException ex)
			{
				log.debug(returnValue + " not assignable");
			}
			catch (TransientHibernateObjectException ex)
			{
				log.info(returnValue + " is transient : cannot clone...");
			}
		}
		
	//	Remove HTTP session of Pojo store thread local
	//
		HttpSessionPojoStore.setHttpSession(null);
		
		return returnValue;
	}
}
