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

package org.acegisecurity.vote;

import junit.framework.TestCase;

import org.acegisecurity.Authentication;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.SecurityConfig;

import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationToken;
import org.acegisecurity.providers.rememberme.RememberMeAuthenticationToken;


/**
 * Tests {@link AuthenticatedVoter}.
 *
 * @author Ben Alex
 * @version $Id: AuthenticatedVoterTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class AuthenticatedVoterTests extends TestCase {
    //~ Constructors ===================================================================================================

    public AuthenticatedVoterTests() {
        super();
    }

    public AuthenticatedVoterTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    private Authentication createAnonymous() {
        return new AnonymousAuthenticationToken("ignored", "ignored",
            new GrantedAuthority[] {new GrantedAuthorityImpl("ignored")});
    }

    private Authentication createFullyAuthenticated() {
        return new UsernamePasswordAuthenticationToken("ignored", "ignored",
            new GrantedAuthority[] {new GrantedAuthorityImpl("ignored")});
    }

    private Authentication createRememberMe() {
        return new RememberMeAuthenticationToken("ignored", "ignored",
            new GrantedAuthority[] {new GrantedAuthorityImpl("ignored")});
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AuthenticatedVoterTests.class);
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testAnonymousWorks() {
        AuthenticatedVoter voter = new AuthenticatedVoter();
        ConfigAttributeDefinition def = new ConfigAttributeDefinition();
        def.addConfigAttribute(new SecurityConfig(AuthenticatedVoter.IS_AUTHENTICATED_ANONYMOUSLY));
        assertEquals(AccessDecisionVoter.ACCESS_GRANTED, voter.vote(createAnonymous(), null, def));
        assertEquals(AccessDecisionVoter.ACCESS_GRANTED, voter.vote(createRememberMe(), null, def));
        assertEquals(AccessDecisionVoter.ACCESS_GRANTED, voter.vote(createFullyAuthenticated(), null, def));
    }

    public void testFullyWorks() {
        AuthenticatedVoter voter = new AuthenticatedVoter();
        ConfigAttributeDefinition def = new ConfigAttributeDefinition();
        def.addConfigAttribute(new SecurityConfig(AuthenticatedVoter.IS_AUTHENTICATED_FULLY));
        assertEquals(AccessDecisionVoter.ACCESS_DENIED, voter.vote(createAnonymous(), null, def));
        assertEquals(AccessDecisionVoter.ACCESS_DENIED, voter.vote(createRememberMe(), null, def));
        assertEquals(AccessDecisionVoter.ACCESS_GRANTED, voter.vote(createFullyAuthenticated(), null, def));
    }

    public void testRememberMeWorks() {
        AuthenticatedVoter voter = new AuthenticatedVoter();
        ConfigAttributeDefinition def = new ConfigAttributeDefinition();
        def.addConfigAttribute(new SecurityConfig(AuthenticatedVoter.IS_AUTHENTICATED_REMEMBERED));
        assertEquals(AccessDecisionVoter.ACCESS_DENIED, voter.vote(createAnonymous(), null, def));
        assertEquals(AccessDecisionVoter.ACCESS_GRANTED, voter.vote(createRememberMe(), null, def));
        assertEquals(AccessDecisionVoter.ACCESS_GRANTED, voter.vote(createFullyAuthenticated(), null, def));
    }

    public void testSetterRejectsNull() {
        AuthenticatedVoter voter = new AuthenticatedVoter();

        try {
            voter.setAuthenticationTrustResolver(null);
            fail("Expected IAE");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    public void testSupports() {
        AuthenticatedVoter voter = new AuthenticatedVoter();
        assertTrue(voter.supports(String.class));
        assertTrue(voter.supports(new SecurityConfig(AuthenticatedVoter.IS_AUTHENTICATED_ANONYMOUSLY)));
        assertTrue(voter.supports(new SecurityConfig(AuthenticatedVoter.IS_AUTHENTICATED_FULLY)));
        assertTrue(voter.supports(new SecurityConfig(AuthenticatedVoter.IS_AUTHENTICATED_REMEMBERED)));
        assertFalse(voter.supports(new SecurityConfig("FOO")));
    }
}
