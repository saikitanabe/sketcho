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

package org.acegisecurity.userdetails;

import junit.framework.TestCase;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;

import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;


/**
 * Tests {@link User}.
 *
 * @author Ben Alex
 * @version $Id: UserTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class UserTests extends TestCase {
    //~ Constructors ===================================================================================================

    public UserTests() {
        super();
    }

    public UserTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UserTests.class);
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testEquals() {
        User user1 = new User("marissa", "koala", true, true, true, true,
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        assertFalse(user1.equals(null));
        assertFalse(user1.equals("A STRING"));

        assertTrue(user1.equals(user1));

        assertTrue(user1.equals(
                new User("marissa", "koala", true, true, true, true,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")})));

        assertFalse(user1.equals(
                new User("DIFFERENT_USERNAME", "koala", true, true, true, true,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")})));

        assertFalse(user1.equals(
                new User("marissa", "DIFFERENT_PASSWORD", true, true, true, true,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")})));

        assertFalse(user1.equals(
                new User("marissa", "koala", false, true, true, true,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")})));

        assertFalse(user1.equals(
                new User("marissa", "koala", true, false, true, true,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")})));

        assertFalse(user1.equals(
                new User("marissa", "koala", true, true, false, true,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")})));

        assertFalse(user1.equals(
                new User("marissa", "koala", true, true, true, false,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")})));

        assertFalse(user1.equals(
                new User("marissa", "koala", true, true, true, true,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE")})));
    }

    public void testNoArgConstructorDoesntExist() {
        Class clazz = User.class;

        try {
            clazz.getDeclaredConstructor((Class[]) null);
            fail("Should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
            assertTrue(true);
        }
    }

    public void testNullValuesRejected() throws Exception {
        try {
            UserDetails user = new User(null, "koala", true, true, true, true,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            UserDetails user = new User("marissa", null, true, true, true, true,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            UserDetails user = new User("marissa", "koala", true, true, true, true, null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            UserDetails user = new User("marissa", "koala", true, true, true, true,
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), null});
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    public void testNullWithinGrantedAuthorityElementIsRejected()
        throws Exception {
        try {
            UserDetails user = new User(null, "koala", true, true, true, true,
                    new GrantedAuthority[] {
                        new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO"), null,
                        new GrantedAuthorityImpl("ROLE_THREE")
                    });
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    public void testUserGettersSetter() throws Exception {
        UserDetails user = new User("marissa", "koala", true, true, true, true,
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
        assertEquals("marissa", user.getUsername());
        assertEquals("koala", user.getPassword());
        assertTrue(user.isEnabled());
        assertEquals(new GrantedAuthorityImpl("ROLE_ONE"), user.getAuthorities()[0]);
        assertEquals(new GrantedAuthorityImpl("ROLE_TWO"), user.getAuthorities()[1]);
        assertTrue(user.toString().indexOf("marissa") != -1);
    }

    public void testUserIsEnabled() throws Exception {
        UserDetails user = new User("marissa", "koala", false, true, true, true,
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
        assertTrue(!user.isEnabled());
    }
}
