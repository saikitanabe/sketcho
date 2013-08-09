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

package org.acegisecurity.adapters.cas3;

import org.acegisecurity.AuthenticationManager;

import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;


/**
 * Tests {@link CasAuthenticationHandler}
 *
 * @author Scott Battaglia
 * @version $Id: CasAuthenticationHandlerTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class CasAuthenticationHandlerTests extends AbstractDependencyInjectionSpringContextTests {
    //~ Instance fields ================================================================================================

    private AuthenticationManager authenticationManager;
    private CasAuthenticationHandler casAuthenticationHandler;

    //~ Methods ========================================================================================================

    protected String[] getConfigLocations() {
        return new String[] {"/org/acegisecurity/adapters/cas/applicationContext-valid.xml"};
    }

    private UsernamePasswordCredentials getCredentialsFor(final String username, final String password) {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);

        return credentials;
    }

    protected void onSetUp() throws Exception {
        this.casAuthenticationHandler = new CasAuthenticationHandler();
        this.casAuthenticationHandler.setAuthenticationManager(authenticationManager);
        this.casAuthenticationHandler.afterPropertiesSet();
    }

    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void testAfterPropertiesSet() throws Exception {
        this.casAuthenticationHandler.setAuthenticationManager(null);

        try {
            this.casAuthenticationHandler.afterPropertiesSet();
            fail("IllegalArgumenException expected when no AuthenticationManager is set.");
        } catch (final IllegalArgumentException e) {
            // this is okay
        }
    }

    public void testGracefullyHandlesInvalidInput() {
        try {
            assertFalse(this.casAuthenticationHandler.authenticate(getCredentialsFor("", "")));
            assertFalse(this.casAuthenticationHandler.authenticate(getCredentialsFor(null, null)));
        } catch (final AuthenticationException e) {
            fail("AuthenticationException not expected.");
        }
    }

    public void testInvalidUsernamePasswordCombination() {
        try {
            assertFalse(this.casAuthenticationHandler.authenticate(getCredentialsFor("scott", "scott")));
        } catch (final AuthenticationException e) {
            fail("AuthenticationException not expected.");
        }
    }

    public void testValidUsernamePasswordCombination() {
        try {
            assertTrue(this.casAuthenticationHandler.authenticate(getCredentialsFor("scott", "wombat")));
        } catch (final AuthenticationException e) {
            fail("AuthenticationException not expected.");
        }
    }
}
