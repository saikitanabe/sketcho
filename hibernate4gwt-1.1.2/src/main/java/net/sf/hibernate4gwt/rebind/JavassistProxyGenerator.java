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

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import net.sf.hibernate4gwt.exception.ProxyException;
import net.sf.hibernate4gwt.rebind.xml.AdditionalCode;
import net.sf.hibernate4gwt.rebind.xml.AdditionalCodeReader;
import net.sf.hibernate4gwt.rebind.xml.Attribute;
import net.sf.hibernate4gwt.rebind.xml.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Javassist proxy generator (for server side)
 * @author bruno.marchesson
 *
 */
public class JavassistProxyGenerator implements IServerProxyGenerator
{
	//----
	// Attributes
	//----
	/**
	 * Log channel
	 */
	private static Log log = LogFactory.getLog(JavassistProxyGenerator.class);
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.rebind.IServerProxyGenerator#generateProxyFor(java.lang.Class)
	 */
	public Class generateProxyFor(Class clazz, AdditionalCode additionalCode)
	{
		try
		{
		//	Compute proxy class name
		//
			String sourceClassName = clazz.getName();
			String proxyClassName = sourceClassName + additionalCode.getSuffix();
			log.info("Generating server proxy " + proxyClassName + " for class " + sourceClassName);
		
		//	Create proxy class
		//
			ClassPool pool = ClassPool.getDefault();
			
			// TOMCAT and JBOSS classloader handling
			pool.insertClassPath(new ClassClassPath(clazz));
			
			CtClass proxyClass = pool.makeClass(proxyClassName);
			
		//	Add proxy inheritance
		//
			proxyClass.setSuperclass(pool.get(sourceClassName));
			
		//	Add ILazyPojo inheritance
		//
			if (additionalCode.getImplementedInterface() != null)
			{
				proxyClass.addInterface(pool.get(additionalCode.getImplementedInterface()));
			}
			
		//	TODO add lazy properties
		//
			generateProxy(proxyClass, additionalCode);
			
		//	Generate class
		//
			return proxyClass.toClass(clazz.getClassLoader());
		}
		catch(Exception ex)
		{
			throw new ProxyException("Proxy generation failure for " + clazz.getName(), ex);
		}
	}
	
	//-------------------------------------------------------------------------
	//
	// Internal methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Generates ILazyPojo classes and methods
	 * @throws CannotCompileException 
	 */
	private void generateProxy(CtClass proxyClass, AdditionalCode additionalCode) throws CannotCompileException
	{
	//	Generate attributes if needed
	//
		if (additionalCode.getAttributes() != null)
		{
			for (Attribute attribute : additionalCode.getAttributes())
			{
				generateAttribute(proxyClass, attribute);
			}
		}
				
	//	Generate methods if needed
	//	
		if (additionalCode.getMethods() != null)
		{
			for (Method method : additionalCode.getMethods())
			{
				generateMethod(proxyClass, method);
			}
		}
	}	
	
	/**
	 * Generate an additional attribute 
	 * @param proxyClass
	 * @param attribute
	 * @throws CannotCompileException
	 */
	protected void generateAttribute(CtClass proxyClass, Attribute attribute) throws CannotCompileException
	{
		CtField field = CtField.make(attribute.toJava14String(), proxyClass);
		proxyClass.addField(field);
	}
	
	/**
	 * Generate additional method to the instrumented class
	 * @param proxyClass
	 * @param method
	 * @throws CannotCompileException 
	 */
	private void generateMethod(CtClass proxyClass, Method method) throws CannotCompileException
	{
	//	Source code
	//
		StringBuffer sourceCode = new StringBuffer();
		sourceCode.append(method.computeJava14Signature());
		sourceCode.append(method.getCode());
		
	//	Add method body
	//
		CtMethod ctMethod = CtNewMethod.make(sourceCode.toString(), proxyClass);
		proxyClass.addMethod(ctMethod);
	}
}
