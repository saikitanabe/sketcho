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

package org.acegisecurity.userdetails.memory;

import junit.framework.TestCase;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;

import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.acegisecurity.userdetails.memory.UserMap;


/**
 * Tests {@link UserMap}.
 *
 * @author Ben Alex
 * @version $Id: UserMapTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class UserMapTests extends TestCase {
    //~ Constructors ===================================================================================================

    public UserMapTests() {
        super();
    }

    public UserMapTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UserMapTests.class);
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testAddAndRetrieveUser() {
        UserDetails marissa = new User("marissa", "koala", true, true, true, true,
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
        UserDetails scott = new User("scott", "wombat", true, true, true, true,
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_THREE")});
        UserDetails peter = new User("peter", "opal", true, true, true, true,
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_FOUR")});
        UserMap map = new UserMap();
        map.addUser(marissa);
        map.addUser(scott);
        map.addUser(peter);
        assertEquals(3, map.getUserCount());

        assertEquals(marissa, map.getUser("marissa"));
        assertEquals(scott, map.getUser("scott"));
        assertEquals(peter, map.getUser("peter"));
    }

    public void testNullUserCannotBeAdded() {
        UserMap map = new UserMap();
        assertEquals(0, map.getUserCount());

        try {
            map.addUser(null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    public void testUnknownUserIsNotRetrieved() {
        UserDetails marissa = new User("marissa", "koala", true, true, true, true,
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
        UserMap map = new UserMap();
        assertEquals(0, map.getUserCount());
        map.addUser(marissa);
        assertEquals(1, map.getUserCount());

        try {
            map.getUser("scott");
            fail("Should have thrown UsernameNotFoundException");
        } catch (UsernameNotFoundException expected) {
            assertTrue(true);
        }
    }
}
