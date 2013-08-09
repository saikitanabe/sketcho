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

package org.acegisecurity.ui.webapp;

import junit.framework.TestCase;

import org.acegisecurity.Authentication;
import org.acegisecurity.MockAuthenticationManager;

import org.acegisecurity.ui.WebAuthenticationDetails;

import org.springframework.mock.web.MockHttpServletRequest;


/**
 * Tests {@link AuthenticationProcessingFilter}.
 *
 * @author Ben Alex
 * @version $Id: AuthenticationProcessingFilterTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class AuthenticationProcessingFilterTests extends TestCase {
    //~ Constructors ===================================================================================================

    public AuthenticationProcessingFilterTests() {
        super();
    }

    public AuthenticationProcessingFilterTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AuthenticationProcessingFilterTests.class);
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testGetters() {
        AuthenticationProcessingFilter filter = new AuthenticationProcessingFilter();
        assertEquals("/j_acegi_security_check", filter.getDefaultFilterProcessesUrl());
    }

    public void testNormalOperation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(AuthenticationProcessingFilter.ACEGI_SECURITY_FORM_USERNAME_KEY, "marissa");
        request.addParameter(AuthenticationProcessingFilter.ACEGI_SECURITY_FORM_PASSWORD_KEY, "koala");

        MockAuthenticationManager authMgr = new MockAuthenticationManager(true);

        AuthenticationProcessingFilter filter = new AuthenticationProcessingFilter();
        filter.setAuthenticationManager(authMgr);
        filter.init(null);

        Authentication result = filter.attemptAuthentication(request);
        assertTrue(result != null);
        assertEquals("127.0.0.1", ((WebAuthenticationDetails) result.getDetails()).getRemoteAddress());
    }

    public void testNullPasswordHandledGracefully() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(AuthenticationProcessingFilter.ACEGI_SECURITY_FORM_USERNAME_KEY, "marissa");

        MockAuthenticationManager authMgr = new MockAuthenticationManager(true);

        AuthenticationProcessingFilter filter = new AuthenticationProcessingFilter();
        filter.setAuthenticationManager(authMgr);
        filter.init(null);

        Authentication result = filter.attemptAuthentication(request);
        assertTrue(result != null);
    }

    public void testNullUsernameHandledGracefully() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(AuthenticationProcessingFilter.ACEGI_SECURITY_FORM_PASSWORD_KEY, "koala");

        MockAuthenticationManager authMgr = new MockAuthenticationManager(true);

        AuthenticationProcessingFilter filter = new AuthenticationProcessingFilter();
        filter.setAuthenticationManager(authMgr);
        filter.init(null);

        Authentication result = filter.attemptAuthentication(request);
        assertTrue(result != null);
    }
}
