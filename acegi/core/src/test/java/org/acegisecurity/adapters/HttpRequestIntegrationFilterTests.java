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

package org.acegisecurity.adapters;

import junit.framework.TestCase;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;

import org.acegisecurity.context.SecurityContextHolder;

import org.acegisecurity.util.MockFilterChain;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Tests {@link HttpRequestIntegrationFilter}.
 *
 * @author Ben Alex
 * @version $Id: HttpRequestIntegrationFilterTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class HttpRequestIntegrationFilterTests extends TestCase {
    //~ Constructors ===================================================================================================

    public HttpRequestIntegrationFilterTests() {
        super();
    }

    public HttpRequestIntegrationFilterTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(HttpRequestIntegrationFilterTests.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public void testCorrectOperation() throws Exception {
        HttpRequestIntegrationFilter filter = new HttpRequestIntegrationFilter();
        PrincipalAcegiUserToken principal = new PrincipalAcegiUserToken("key", "someone", "password",
                new GrantedAuthority[] {new GrantedAuthorityImpl("SOME_ROLE")}, null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setUserPrincipal(principal);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain(true);

        filter.doFilter(request, response, chain);

        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof PrincipalAcegiUserToken)) {
            System.out.println(SecurityContextHolder.getContext().getAuthentication());
            fail("Should have returned PrincipalAcegiUserToken");
        }

        PrincipalAcegiUserToken castResult = (PrincipalAcegiUserToken) SecurityContextHolder.getContext()
                                                                                            .getAuthentication();
        assertEquals(principal, castResult);
    }

    public void testHandlesIfHttpRequestIsNullForSomeReason()
        throws Exception {
        HttpRequestIntegrationFilter filter = new HttpRequestIntegrationFilter();

        try {
            filter.doFilter(null, null, null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    public void testHandlesIfThereIsNoPrincipal() throws Exception {
        HttpRequestIntegrationFilter filter = new HttpRequestIntegrationFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain(true);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        filter.doFilter(request, response, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
