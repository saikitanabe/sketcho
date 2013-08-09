/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.acegisecurity.intercept.method.aopalliance;

import junit.framework.TestCase;

import org.acegisecurity.AccessDecisionManager;
import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.AfterInvocationManager;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationCredentialsNotFoundException;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.ITargetObject;
import org.acegisecurity.MockAccessDecisionManager;
import org.acegisecurity.MockAfterInvocationManager;
import org.acegisecurity.MockAuthenticationManager;
import org.acegisecurity.MockRunAsManager;
import org.acegisecurity.RunAsManager;

import org.acegisecurity.context.SecurityContextHolder;

import org.acegisecurity.intercept.method.AbstractMethodDefinitionSource;
import org.acegisecurity.intercept.method.MockMethodDefinitionSource;

import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.acegisecurity.runas.RunAsManagerImpl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Method;

import java.util.Iterator;


/**
 * Tests {@link MethodSecurityInterceptor}.
 *
 * @author Ben Alex
 * @version $Id: MethodSecurityInterceptorTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class MethodSecurityInterceptorTests extends TestCase {
    //~ Constructors ===================================================================================================

    public MethodSecurityInterceptorTests() {
        super();
    }

    public MethodSecurityInterceptorTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MethodSecurityInterceptorTests.class);
    }

    private ITargetObject makeInterceptedTarget() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "org/acegisecurity/intercept/method/aopalliance/applicationContext.xml");

        return (ITargetObject) context.getBean("target");
    }

    private ITargetObject makeInterceptedTargetRejectsAuthentication() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "org/acegisecurity/intercept/method/aopalliance/applicationContext.xml");

        MockAuthenticationManager authenticationManager = new MockAuthenticationManager(false);
        MethodSecurityInterceptor si = (MethodSecurityInterceptor) context.getBean("securityInterceptor");
        si.setAuthenticationManager(authenticationManager);

        return (ITargetObject) context.getBean("target");
    }

    private ITargetObject makeInterceptedTargetWithoutAnAfterInvocationManager() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "org/acegisecurity/intercept/method/aopalliance/applicationContext.xml");

        MethodSecurityInterceptor si = (MethodSecurityInterceptor) context.getBean("securityInterceptor");
        si.setAfterInvocationManager(null);

        return (ITargetObject) context.getBean("target");
    }

    public final void setUp() throws Exception {
        super.setUp();
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public void testCallingAPublicMethodFacadeWillNotRepeatSecurityChecksWhenPassedToTheSecuredMethodItFronts()
        throws Exception {
        ITargetObject target = makeInterceptedTarget();
        String result = target.publicMakeLowerCase("HELLO");
        assertEquals("hello Authentication empty", result);
    }

    public void testCallingAPublicMethodWhenPresentingAnAuthenticationObjectWillNotChangeItsIsAuthenticatedProperty()
        throws Exception {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("Test", "Password");
        assertTrue(!token.isAuthenticated());
        SecurityContextHolder.getContext().setAuthentication(token);

        // The associated MockAuthenticationManager WILL accept the above UsernamePasswordAuthenticationToken
        ITargetObject target = makeInterceptedTarget();
        String result = target.publicMakeLowerCase("HELLO");
        assertEquals("hello org.acegisecurity.providers.UsernamePasswordAuthenticationToken false", result);
    }

    public void testDeniesWhenAppropriate() throws Exception {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("Test", "Password",
                new GrantedAuthority[] {new GrantedAuthorityImpl("MOCK_NO_BENEFIT_TO_THIS_GRANTED_AUTHORITY")});
        SecurityContextHolder.getContext().setAuthentication(token);

        ITargetObject target = makeInterceptedTarget();

        try {
            target.makeUpperCase("HELLO");
            fail("Should have thrown AccessDeniedException");
        } catch (AccessDeniedException expected) {
            assertTrue(true);
        }
    }

    public void testGetters() {
        MockAccessDecisionManager accessDecision = new MockAccessDecisionManager();
        MockRunAsManager runAs = new MockRunAsManager();
        MockAuthenticationManager authManager = new MockAuthenticationManager();
        MockMethodDefinitionSource methodSource = new MockMethodDefinitionSource(false, true);
        MockAfterInvocationManager afterInvocation = new MockAfterInvocationManager();

        MethodSecurityInterceptor si = new MethodSecurityInterceptor();
        si.setAccessDecisionManager(accessDecision);
        si.setRunAsManager(runAs);
        si.setAuthenticationManager(authManager);
        si.setObjectDefinitionSource(methodSource);
        si.setAfterInvocationManager(afterInvocation);

        assertEquals(accessDecision, si.getAccessDecisionManager());
        assertEquals(runAs, si.getRunAsManager());
        assertEquals(authManager, si.getAuthenticationManager());
        assertEquals(methodSource, si.getObjectDefinitionSource());
        assertEquals(afterInvocation, si.getAfterInvocationManager());
    }

    public void testMethodCallWithRunAsReplacement() throws Exception {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("Test", "Password",
                new GrantedAuthority[] {new GrantedAuthorityImpl("MOCK_UPPER")});
        SecurityContextHolder.getContext().setAuthentication(token);

        ITargetObject target = makeInterceptedTarget();
        String result = target.makeUpperCase("hello");
        assertEquals("HELLO org.acegisecurity.MockRunAsAuthenticationToken true", result);
    }

    public void testMethodCallWithoutRunAsReplacement()
        throws Exception {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("Test", "Password",
                new GrantedAuthority[] {new GrantedAuthorityImpl("MOCK_LOWER")});
        assertTrue(token.isAuthenticated());
        SecurityContextHolder.getContext().setAuthentication(token);

        ITargetObject target = makeInterceptedTargetWithoutAnAfterInvocationManager();
        String result = target.makeLowerCase("HELLO");

        // Note we check the isAuthenticated remained true in following line
        assertEquals("hello org.acegisecurity.providers.UsernamePasswordAuthenticationToken true", result);
    }

    public void testRejectionOfEmptySecurityContext() throws Exception {
        ITargetObject target = makeInterceptedTarget();

        try {
            target.makeUpperCase("hello");
            fail("Should have thrown AuthenticationCredentialsNotFoundException");
        } catch (AuthenticationCredentialsNotFoundException expected) {
            assertTrue(true);
        }
    }

    public void testRejectsAccessDecisionManagersThatDoNotSupportMethodInvocation()
        throws Exception {
        MethodSecurityInterceptor si = new MethodSecurityInterceptor();
        si.setAccessDecisionManager(new MockAccessDecisionManagerWhichOnlySupportsStrings());
        si.setAuthenticationManager(new MockAuthenticationManager());
        si.setObjectDefinitionSource(new MockMethodDefinitionSource(false, true));
        si.setRunAsManager(new MockRunAsManager());

        try {
            si.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("AccessDecisionManager does not support secure object class: interface org.aopalliance.intercept.MethodInvocation",
                expected.getMessage());
        }
    }

    public void testRejectsCallsWhenAuthenticationIsIncorrect()
        throws Exception {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("Test", "Password");
        assertTrue(!token.isAuthenticated());
        SecurityContextHolder.getContext().setAuthentication(token);

        // NB: The associated MockAuthenticationManager WILL reject the above UsernamePasswordAuthenticationToken
        ITargetObject target = makeInterceptedTargetRejectsAuthentication();

        try {
            target.makeLowerCase("HELLO");
            fail("Should have thrown AuthenticationException");
        } catch (AuthenticationException expected) {
            assertTrue(true);
        }
    }

    public void testRejectsCallsWhenObjectDefinitionSourceDoesNotSupportObject()
        throws Throwable {
        MethodSecurityInterceptor interceptor = new MethodSecurityInterceptor();
        interceptor.setObjectDefinitionSource(new MockObjectDefinitionSourceWhichOnlySupportsStrings());
        interceptor.setAccessDecisionManager(new MockAccessDecisionManager());
        interceptor.setAuthenticationManager(new MockAuthenticationManager());
        interceptor.setRunAsManager(new MockRunAsManager());

        try {
            interceptor.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("ObjectDefinitionSource does not support secure object class: interface org.aopalliance.intercept.MethodInvocation",
                expected.getMessage());
        }
    }

    public void testRejectsCallsWhenObjectIsNull() throws Throwable {
        MethodSecurityInterceptor interceptor = new MethodSecurityInterceptor();

        try {
            interceptor.invoke(null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("Object was null", expected.getMessage());
        }
    }

    public void testRejectsRunAsManagersThatDoNotSupportMethodInvocation()
        throws Exception {
        MethodSecurityInterceptor si = new MethodSecurityInterceptor();
        si.setAccessDecisionManager(new MockAccessDecisionManager());
        si.setAuthenticationManager(new MockAuthenticationManager());
        si.setObjectDefinitionSource(new MockMethodDefinitionSource(false, true));
        si.setRunAsManager(new MockRunAsManagerWhichOnlySupportsStrings());
        si.setAfterInvocationManager(new MockAfterInvocationManager());

        try {
            si.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("RunAsManager does not support secure object class: interface org.aopalliance.intercept.MethodInvocation",
                expected.getMessage());
        }
    }

    public void testStartupCheckForAccessDecisionManager()
        throws Exception {
        MethodSecurityInterceptor si = new MethodSecurityInterceptor();
        si.setRunAsManager(new MockRunAsManager());
        si.setAuthenticationManager(new MockAuthenticationManager());
        si.setAfterInvocationManager(new MockAfterInvocationManager());

        si.setObjectDefinitionSource(new MockMethodDefinitionSource(false, true));

        try {
            si.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("An AccessDecisionManager is required", expected.getMessage());
        }
    }

    public void testStartupCheckForAuthenticationManager()
        throws Exception {
        MethodSecurityInterceptor si = new MethodSecurityInterceptor();
        si.setAccessDecisionManager(new MockAccessDecisionManager());
        si.setRunAsManager(new MockRunAsManager());
        si.setAfterInvocationManager(new MockAfterInvocationManager());

        si.setObjectDefinitionSource(new MockMethodDefinitionSource(false, true));

        try {
            si.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("An AuthenticationManager is required", expected.getMessage());
        }
    }

    public void testStartupCheckForMethodDefinitionSource()
        throws Exception {
        MethodSecurityInterceptor si = new MethodSecurityInterceptor();
        si.setAccessDecisionManager(new MockAccessDecisionManager());
        si.setAuthenticationManager(new MockAuthenticationManager());

        try {
            si.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("An ObjectDefinitionSource is required", expected.getMessage());
        }
    }

    public void testStartupCheckForRunAsManager() throws Exception {
        MethodSecurityInterceptor si = new MethodSecurityInterceptor();
        si.setAccessDecisionManager(new MockAccessDecisionManager());
        si.setAuthenticationManager(new MockAuthenticationManager());
        si.setRunAsManager(null); // Overriding the default

        si.setObjectDefinitionSource(new MockMethodDefinitionSource(false, true));

        try {
            si.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("A RunAsManager is required", expected.getMessage());
        }
    }

    public void testStartupCheckForValidAfterInvocationManager()
        throws Exception {
        MethodSecurityInterceptor si = new MethodSecurityInterceptor();
        si.setRunAsManager(new MockRunAsManager());
        si.setAuthenticationManager(new MockAuthenticationManager());
        si.setAfterInvocationManager(new MockAfterInvocationManagerWhichOnlySupportsStrings());
        si.setAccessDecisionManager(new MockAccessDecisionManager());
        si.setObjectDefinitionSource(new MockMethodDefinitionSource(false, true));

        try {
            si.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().startsWith("AfterInvocationManager does not support secure object class:"));
        }
    }

    public void testValidationFailsIfInvalidAttributePresented()
        throws Exception {
        MethodSecurityInterceptor si = new MethodSecurityInterceptor();
        si.setAccessDecisionManager(new MockAccessDecisionManager());
        si.setAuthenticationManager(new MockAuthenticationManager());
        si.setRunAsManager(new RunAsManagerImpl());

        assertTrue(si.isValidateConfigAttributes()); // check default
        si.setObjectDefinitionSource(new MockMethodDefinitionSource(true, true));

        try {
            si.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("Unsupported configuration attributes: [ANOTHER_INVALID, INVALID_ATTRIBUTE]",
                expected.getMessage());
        }
    }

    public void testValidationNotAttemptedIfIsValidateConfigAttributesSetToFalse()
        throws Exception {
        MethodSecurityInterceptor si = new MethodSecurityInterceptor();
        si.setAccessDecisionManager(new MockAccessDecisionManager());
        si.setAuthenticationManager(new MockAuthenticationManager());

        assertTrue(si.isValidateConfigAttributes()); // check default
        si.setValidateConfigAttributes(false);
        assertTrue(!si.isValidateConfigAttributes()); // check changed

        si.setObjectDefinitionSource(new MockMethodDefinitionSource(true, true));
        si.afterPropertiesSet();
        assertTrue(true);
    }

    public void testValidationNotAttemptedIfMethodDefinitionSourceCannotReturnIterator()
        throws Exception {
        MethodSecurityInterceptor si = new MethodSecurityInterceptor();
        si.setAccessDecisionManager(new MockAccessDecisionManager());
        si.setRunAsManager(new MockRunAsManager());
        si.setAuthenticationManager(new MockAuthenticationManager());

        assertTrue(si.isValidateConfigAttributes()); // check default
        si.setObjectDefinitionSource(new MockMethodDefinitionSource(true, false));
        si.afterPropertiesSet();
        assertTrue(true);
    }

    //~ Inner Classes ==================================================================================================

    private class MockAccessDecisionManagerWhichOnlySupportsStrings implements AccessDecisionManager {
        public void decide(Authentication authentication, Object object, ConfigAttributeDefinition config)
            throws AccessDeniedException {
            throw new UnsupportedOperationException("mock method not implemented");
        }

        public boolean supports(Class clazz) {
            if (String.class.isAssignableFrom(clazz)) {
                return true;
            } else {
                return false;
            }
        }

        public boolean supports(ConfigAttribute attribute) {
            return true;
        }
    }

    private class MockAfterInvocationManagerWhichOnlySupportsStrings implements AfterInvocationManager {
        public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config,
            Object returnedObject) throws AccessDeniedException {
            throw new UnsupportedOperationException("mock method not implemented");
        }

        public boolean supports(Class clazz) {
            if (String.class.isAssignableFrom(clazz)) {
                return true;
            } else {
                return false;
            }
        }

        public boolean supports(ConfigAttribute attribute) {
            return true;
        }
    }

    private class MockObjectDefinitionSourceWhichOnlySupportsStrings extends AbstractMethodDefinitionSource {
        public Iterator getConfigAttributeDefinitions() {
            return null;
        }

        protected ConfigAttributeDefinition lookupAttributes(Method method) {
            throw new UnsupportedOperationException("mock method not implemented");
        }

        public boolean supports(Class clazz) {
            if (String.class.isAssignableFrom(clazz)) {
                return true;
            } else {
                return false;
            }
        }
    }

    private class MockRunAsManagerWhichOnlySupportsStrings implements RunAsManager {
        public Authentication buildRunAs(Authentication authentication, Object object, ConfigAttributeDefinition config) {
            throw new UnsupportedOperationException("mock method not implemented");
        }

        public boolean supports(Class clazz) {
            if (String.class.isAssignableFrom(clazz)) {
                return true;
            } else {
                return false;
            }
        }

        public boolean supports(ConfigAttribute attribute) {
            return true;
        }
    }
}
