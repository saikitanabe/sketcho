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

package org.acegisecurity.concurrent;

import junit.framework.TestCase;

import org.acegisecurity.ui.session.HttpSessionDestroyedEvent;

import org.springframework.mock.web.MockHttpSession;

import java.util.Date;


/**
 * Tests {@link SessionRegistryImpl}.
 *
* @author Ben Alex
 * @version $Id: SessionRegistryImplTests.java 1988 2007-08-30 11:21:28Z luke_t $
 */
public class SessionRegistryImplTests extends TestCase {
    //~ Methods ========================================================================================================

    public void testEventPublishing() {
        MockHttpSession httpSession = new MockHttpSession();
        Object principal = "Some principal object";
        String sessionId = httpSession.getId();
        assertNotNull(sessionId);

        SessionRegistryImpl sessionRegistry = new SessionRegistryImpl();

        // Register new Session
        sessionRegistry.registerNewSession(sessionId, principal);

        // Deregister session via an ApplicationEvent
        sessionRegistry.onApplicationEvent(new HttpSessionDestroyedEvent(httpSession));

        // Check attempts to retrieve cleared session return null
        assertNull(sessionRegistry.getSessionInformation(sessionId));
    }

    public void testMultiplePrincipals() throws Exception {
        Object principal1 = "principal_1";
        Object principal2 = "principal_2";
        String sessionId1 = "1234567890";
        String sessionId2 = "9876543210";
        String sessionId3 = "5432109876";

        SessionRegistryImpl sessionRegistry = new SessionRegistryImpl();

        sessionRegistry.registerNewSession(sessionId1, principal1);
        sessionRegistry.registerNewSession(sessionId2, principal1);
        sessionRegistry.registerNewSession(sessionId3, principal2);

        assertEquals(principal1, sessionRegistry.getAllPrincipals()[0]);
        assertEquals(principal2, sessionRegistry.getAllPrincipals()[1]);
    }

    public void testSessionInformationLifecycle() throws Exception {
        Object principal = "Some principal object";
        String sessionId = "1234567890";
        SessionRegistryImpl sessionRegistry = new SessionRegistryImpl();

        // Register new Session
        sessionRegistry.registerNewSession(sessionId, principal);

        // Retrieve existing session by session ID
        Date currentDateTime = sessionRegistry.getSessionInformation(sessionId).getLastRequest();
        assertEquals(principal, sessionRegistry.getSessionInformation(sessionId).getPrincipal());
        assertEquals(sessionId, sessionRegistry.getSessionInformation(sessionId).getSessionId());
        assertNotNull(sessionRegistry.getSessionInformation(sessionId).getLastRequest());

        // Retrieve existing session by principal
        assertEquals(1, sessionRegistry.getAllSessions(principal, false).length);

        // Sleep to ensure SessionRegistryImpl will update time
        Thread.sleep(1000);

        // Update request date/time
        sessionRegistry.refreshLastRequest(sessionId);

        Date retrieved = sessionRegistry.getSessionInformation(sessionId).getLastRequest();
        assertTrue(retrieved.after(currentDateTime));

        // Check it retrieves correctly when looked up via principal
        assertEquals(retrieved, sessionRegistry.getAllSessions(principal, false)[0].getLastRequest());

        // Clear session information
        sessionRegistry.removeSessionInformation(sessionId);

        // Check attempts to retrieve cleared session return null
        assertNull(sessionRegistry.getSessionInformation(sessionId));
        assertNull(sessionRegistry.getAllSessions(principal, false));
    }

    public void testTwoSessionsOnePrincipalExpiring() throws Exception {
        Object principal = "Some principal object";
        String sessionId1 = "1234567890";
        String sessionId2 = "9876543210";
        SessionRegistryImpl sessionRegistry = new SessionRegistryImpl();

        // Register new Session
        sessionRegistry.registerNewSession(sessionId1, principal);
        assertEquals(1, sessionRegistry.getAllSessions(principal, false).length);
        assertEquals(sessionId1, sessionRegistry.getAllSessions(principal, false)[0].getSessionId());

        // Register new Session
        sessionRegistry.registerNewSession(sessionId2, principal);
        assertEquals(2, sessionRegistry.getAllSessions(principal, false).length);
        assertEquals(sessionId2, sessionRegistry.getAllSessions(principal, false)[1].getSessionId());

        // Expire one session
        SessionInformation session = sessionRegistry.getSessionInformation(sessionId2);
        session.expireNow();

        // Check retrieval still correct
        assertTrue(sessionRegistry.getSessionInformation(sessionId2).isExpired());
        assertFalse(sessionRegistry.getSessionInformation(sessionId1).isExpired());
    }

    public void testTwoSessionsOnePrincipalHandling() throws Exception {
        Object principal = "Some principal object";
        String sessionId1 = "1234567890";
        String sessionId2 = "9876543210";
        SessionRegistryImpl sessionRegistry = new SessionRegistryImpl();

        // Register new Session
        sessionRegistry.registerNewSession(sessionId1, principal);
        assertEquals(1, sessionRegistry.getAllSessions(principal, false).length);
        assertEquals(sessionId1, sessionRegistry.getAllSessions(principal, false)[0].getSessionId());

        // Register new Session
        sessionRegistry.registerNewSession(sessionId2, principal);
        assertEquals(2, sessionRegistry.getAllSessions(principal, false).length);
        assertEquals(sessionId2, sessionRegistry.getAllSessions(principal, false)[1].getSessionId());

        // Clear session information
        sessionRegistry.removeSessionInformation(sessionId1);
        assertEquals(1, sessionRegistry.getAllSessions(principal, false).length);
        assertEquals(sessionId2, sessionRegistry.getAllSessions(principal, false)[0].getSessionId());

        // Clear final session
        sessionRegistry.removeSessionInformation(sessionId2);
        assertNull(sessionRegistry.getSessionInformation(sessionId2));
        assertNull(sessionRegistry.getAllSessions(principal, false));
    }
}
