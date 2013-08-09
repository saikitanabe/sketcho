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

package org.acegisecurity.event.authorization;

import junit.framework.TestCase;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.ConfigAttributeDefinition;

import org.acegisecurity.event.authorization.AuthorizationFailureEvent;

import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.acegisecurity.util.SimpleMethodInvocation;


/**
 * Tests {@link AuthorizationFailureEvent}.
 *
 * @author Ben Alex
 * @version $Id: AuthorizationFailureEventTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class AuthorizationFailureEventTests extends TestCase {
    //~ Constructors ===================================================================================================

    public AuthorizationFailureEventTests() {
        super();
    }

    public AuthorizationFailureEventTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AuthorizationFailureEventTests.class);
    }

    public void testRejectsNulls() {
        try {
            new AuthorizationFailureEvent(null, new ConfigAttributeDefinition(),
                new UsernamePasswordAuthenticationToken("foo", "bar"), new AccessDeniedException("error"));
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            new AuthorizationFailureEvent(new SimpleMethodInvocation(), null,
                new UsernamePasswordAuthenticationToken("foo", "bar"), new AccessDeniedException("error"));
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            new AuthorizationFailureEvent(new SimpleMethodInvocation(), new ConfigAttributeDefinition(), null,
                new AccessDeniedException("error"));
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            new AuthorizationFailureEvent(new SimpleMethodInvocation(), new ConfigAttributeDefinition(),
                new UsernamePasswordAuthenticationToken("foo", "bar"), null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }
}
