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

package org.acegisecurity.providers.anonymous;

import junit.framework.TestCase;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;


/**
 * Tests {@link AnonymousAuthenticationToken}.
 *
 * @author Ben Alex
 * @version $Id: AnonymousAuthenticationTokenTests.java 1877 2007-05-25 05:33:06Z benalex $
 */
public class AnonymousAuthenticationTokenTests extends TestCase {
    //~ Constructors ===================================================================================================

    public AnonymousAuthenticationTokenTests() {
        super();
    }

    public AnonymousAuthenticationTokenTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AnonymousAuthenticationTokenTests.class);
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testConstructorRejectsNulls() {
        try {
            new AnonymousAuthenticationToken(null, "Test",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            new AnonymousAuthenticationToken("key", null,
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            new AnonymousAuthenticationToken("key", "Test", null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            new AnonymousAuthenticationToken("key", "Test", new GrantedAuthority[] {null});
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            new AnonymousAuthenticationToken("key", "Test", new GrantedAuthority[] {});
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    public void testEqualsWhenEqual() {
        AnonymousAuthenticationToken token1 = new AnonymousAuthenticationToken("key", "Test",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        AnonymousAuthenticationToken token2 = new AnonymousAuthenticationToken("key", "Test",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        assertEquals(token1, token2);
    }

    public void testGetters() {
        AnonymousAuthenticationToken token = new AnonymousAuthenticationToken("key", "Test",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        assertEquals("key".hashCode(), token.getKeyHash());
        assertEquals("Test", token.getPrincipal());
        assertEquals("", token.getCredentials());
        assertEquals("ROLE_ONE", token.getAuthorities()[0].getAuthority());
        assertEquals("ROLE_TWO", token.getAuthorities()[1].getAuthority());
        assertTrue(token.isAuthenticated());
    }

    public void testNoArgConstructorDoesntExist() {
        Class clazz = AnonymousAuthenticationToken.class;

        try {
            clazz.getDeclaredConstructor((Class[]) null);
            fail("Should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
            assertTrue(true);
        }
    }

    public void testNotEqualsDueToAbstractParentEqualsCheck() {
        AnonymousAuthenticationToken token1 = new AnonymousAuthenticationToken("key", "Test",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        AnonymousAuthenticationToken token2 = new AnonymousAuthenticationToken("key", "DIFFERENT_PRINCIPAL",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        assertFalse(token1.equals(token2));
    }

    public void testNotEqualsDueToDifferentAuthenticationClass() {
        AnonymousAuthenticationToken token1 = new AnonymousAuthenticationToken("key", "Test",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        UsernamePasswordAuthenticationToken token2 = new UsernamePasswordAuthenticationToken("Test", "Password",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        assertFalse(token1.equals(token2));
    }

    public void testNotEqualsDueToKey() {
        AnonymousAuthenticationToken token1 = new AnonymousAuthenticationToken("key", "Test",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        AnonymousAuthenticationToken token2 = new AnonymousAuthenticationToken("DIFFERENT_KEY", "Test",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        assertFalse(token1.equals(token2));
    }

    public void testSetAuthenticatedIgnored() {
        AnonymousAuthenticationToken token = new AnonymousAuthenticationToken("key", "Test",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
        assertTrue(token.isAuthenticated());
        token.setAuthenticated(false);
        assertTrue(!token.isAuthenticated());
    }
}
