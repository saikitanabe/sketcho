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

package org.acegisecurity;

import junit.framework.TestCase;

import org.acegisecurity.providers.TestingAuthenticationToken;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationToken;
import org.acegisecurity.providers.rememberme.RememberMeAuthenticationToken;


/**
 * Tests {@link org.acegisecurity.AuthenticationTrustResolverImpl}.
 *
 * @author Ben Alex
 * @version $Id: AuthenticationTrustResolverImplTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class AuthenticationTrustResolverImplTests extends TestCase {
    //~ Constructors ===================================================================================================

    public AuthenticationTrustResolverImplTests() {
        super();
    }

    public AuthenticationTrustResolverImplTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AuthenticationTrustResolverImplTests.class);
    }

    public void testCorrectOperationIsAnonymous() {
        AuthenticationTrustResolverImpl trustResolver = new AuthenticationTrustResolverImpl();
        assertTrue(trustResolver.isAnonymous(
                new AnonymousAuthenticationToken("ignored", "ignored",
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ignored")})));
        assertFalse(trustResolver.isAnonymous(
                new TestingAuthenticationToken("ignored", "ignored",
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ignored")})));
    }

    public void testCorrectOperationIsRememberMe() {
        AuthenticationTrustResolverImpl trustResolver = new AuthenticationTrustResolverImpl();
        assertTrue(trustResolver.isRememberMe(
                new RememberMeAuthenticationToken("ignored", "ignored",
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ignored")})));
        assertFalse(trustResolver.isAnonymous(
                new TestingAuthenticationToken("ignored", "ignored",
                    new GrantedAuthority[] {new GrantedAuthorityImpl("ignored")})));
    }

    public void testGettersSetters() {
        AuthenticationTrustResolverImpl trustResolver = new AuthenticationTrustResolverImpl();

        assertEquals(AnonymousAuthenticationToken.class, trustResolver.getAnonymousClass());
        trustResolver.setAnonymousClass(String.class);
        assertEquals(String.class, trustResolver.getAnonymousClass());

        assertEquals(RememberMeAuthenticationToken.class, trustResolver.getRememberMeClass());
        trustResolver.setRememberMeClass(String.class);
        assertEquals(String.class, trustResolver.getRememberMeClass());
    }
}
