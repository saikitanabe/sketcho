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
 * Tests {@link TestingAuthenticationToken}.
 *
 * @author Ben Alex
 * @version $Id: TestingAuthenticationTokenTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class TestingAuthenticationTokenTests extends TestCase {
    //~ Constructors ===================================================================================================

    public TestingAuthenticationTokenTests() {
        super();
    }

    public TestingAuthenticationTokenTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestingAuthenticationTokenTests.class);
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testAuthenticated() {
        TestingAuthenticationToken token = new TestingAuthenticationToken("Test", "Password", null);
        assertTrue(!token.isAuthenticated());
        token.setAuthenticated(true);
        assertTrue(token.isAuthenticated());
    }

    public void testGetters() {
        TestingAuthenticationToken token = new TestingAuthenticationToken("Test", "Password",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
        assertEquals("Test", token.getPrincipal());
        assertEquals("Password", token.getCredentials());
        assertEquals("ROLE_ONE", token.getAuthorities()[0].getAuthority());
        assertEquals("ROLE_TWO", token.getAuthorities()[1].getAuthority());
    }

    public void testNoArgConstructorDoesntExist() {
        Class clazz = TestingAuthenticationToken.class;

        try {
            clazz.getDeclaredConstructor((Class[]) null);
            fail("Should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
            assertTrue(true);
        }
    }
}
