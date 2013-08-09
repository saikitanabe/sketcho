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

package org.acegisecurity.providers;

import junit.framework.TestCase;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;


/**
 * Tests {@link AbstractAuthenticationToken}.
 *
 * @author Ben Alex
 * @version $Id: AbstractAuthenticationTokenTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class AbstractAuthenticationTokenTests extends TestCase {
    //~ Instance fields ================================================================================================

    private GrantedAuthority[] authorities = null;

    //~ Constructors ===================================================================================================

    public AbstractAuthenticationTokenTests() {
        super();
    }

    public AbstractAuthenticationTokenTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AbstractAuthenticationTokenTests.class);
    }

    public final void setUp() throws Exception {
        super.setUp();

        authorities = new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")};
    }

    public void testAuthoritiesAreImmutable() {
        MockAuthenticationImpl token = new MockAuthenticationImpl("Test", "Password", authorities);
        GrantedAuthority[] gotAuthorities = token.getAuthorities();
        assertNotSame(authorities, gotAuthorities);

        gotAuthorities[0] = new GrantedAuthorityImpl("ROLE_SUPER_USER");

        // reget them and check nothing has changed
        gotAuthorities = token.getAuthorities();
        assertEquals(2, gotAuthorities.length);
        assertEquals(gotAuthorities[0], authorities[0]);
        assertEquals(gotAuthorities[1], authorities[1]);
        assertFalse(gotAuthorities[0].equals("ROLE_SUPER_USER"));
        assertFalse(gotAuthorities[1].equals("ROLE_SUPER_USER"));
    }

    public void testGetters() throws Exception {
        MockAuthenticationImpl token = new MockAuthenticationImpl("Test", "Password", authorities);
        assertEquals("Test", token.getPrincipal());
        assertEquals("Password", token.getCredentials());
        assertEquals("Test", token.getName());
    }

    public void testHashCode() throws Exception {
        MockAuthenticationImpl token1 = new MockAuthenticationImpl("Test", "Password", authorities);
        MockAuthenticationImpl token2 = new MockAuthenticationImpl("Test", "Password", authorities);
        MockAuthenticationImpl token3 = new MockAuthenticationImpl(null, null, new GrantedAuthority[] {});
        assertEquals(token1.hashCode(), token2.hashCode());
        assertTrue(token1.hashCode() != token3.hashCode());

        token2.setAuthenticated(true);

        assertTrue(token1.hashCode() != token2.hashCode());
    }

    public void testObjectsEquals() throws Exception {
        MockAuthenticationImpl token1 = new MockAuthenticationImpl("Test", "Password", authorities);
        MockAuthenticationImpl token2 = new MockAuthenticationImpl("Test", "Password", authorities);
        assertEquals(token1, token2);

        MockAuthenticationImpl token3 = new MockAuthenticationImpl("Test", "Password_Changed", authorities);
        assertTrue(!token1.equals(token3));

        MockAuthenticationImpl token4 = new MockAuthenticationImpl("Test_Changed", "Password", authorities);
        assertTrue(!token1.equals(token4));

        MockAuthenticationImpl token5 = new MockAuthenticationImpl("Test", "Password",
                new GrantedAuthority[] {
                    new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO_CHANGED")
                });
        assertTrue(!token1.equals(token5));

        MockAuthenticationImpl token6 = new MockAuthenticationImpl("Test", "Password",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE")});
        assertTrue(!token1.equals(token6));

        MockAuthenticationImpl token7 = new MockAuthenticationImpl("Test", "Password", null);
        assertTrue(!token1.equals(token7));
        assertTrue(!token7.equals(token1));

        assertTrue(!token1.equals(new Integer(100)));
    }

    public void testSetAuthenticated() throws Exception {
        MockAuthenticationImpl token = new MockAuthenticationImpl("Test", "Password", authorities);
        assertTrue(!token.isAuthenticated());
        token.setAuthenticated(true);
        assertTrue(token.isAuthenticated());
    }

    public void testToStringWithAuthorities() {
        MockAuthenticationImpl token = new MockAuthenticationImpl("Test", "Password", authorities);
        assertTrue(token.toString().lastIndexOf("ROLE_TWO") != -1);
    }

    public void testToStringWithNullAuthorities() {
        MockAuthenticationImpl token = new MockAuthenticationImpl("Test", "Password", null);
        assertTrue(token.toString().lastIndexOf("Not granted any authorities") != -1);
    }

    //~ Inner Classes ==================================================================================================

    private class MockAuthenticationImpl extends AbstractAuthenticationToken {
        private Object credentials;
        private Object principal;

        public MockAuthenticationImpl(Object principal, Object credentials, GrantedAuthority[] authorities) {
            super(authorities);
            this.principal = principal;
            this.credentials = credentials;
        }

        private MockAuthenticationImpl() {
            super(null);
        }

        public Object getCredentials() {
            return this.credentials;
        }

        public Object getPrincipal() {
            return this.principal;
        }
    }
}
