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

package org.acegisecurity.adapters.jetty;

import junit.framework.TestCase;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;


/**
 * Tests {@link JettyAcegiUserToken}.
 *
 * @author Ben Alex
 * @version $Id: JettyAcegiUserTokenTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class JettyAcegiUserTokenTests extends TestCase {
    //~ Constructors ===================================================================================================

    public JettyAcegiUserTokenTests() {
        super();
    }

    public JettyAcegiUserTokenTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(JettyAcegiUserTokenTests.class);
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testGetters() throws Exception {
        JettyAcegiUserToken token = new JettyAcegiUserToken("my_password", "Test", "Password",
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
        assertEquals("Test", token.getPrincipal());
        assertEquals("Password", token.getCredentials());
        assertEquals("my_password".hashCode(), token.getKeyHash());
        assertEquals("Test", token.getName());
    }

    public void testNoArgsConstructor() {
        try {
            new JettyAcegiUserToken();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }
}
