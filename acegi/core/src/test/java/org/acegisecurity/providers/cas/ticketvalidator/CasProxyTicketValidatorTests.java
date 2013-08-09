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

package org.acegisecurity.providers.cas.ticketvalidator;

import edu.yale.its.tp.cas.client.ProxyTicketValidator;

import junit.framework.TestCase;

import org.acegisecurity.AuthenticationServiceException;
import org.acegisecurity.BadCredentialsException;

import org.acegisecurity.providers.cas.TicketResponse;

import org.acegisecurity.ui.cas.ServiceProperties;

import java.util.Vector;


/**
 * Tests {@link CasProxyTicketValidator}.
 *
 * @author Ben Alex
 * @version $Id: CasProxyTicketValidatorTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class CasProxyTicketValidatorTests extends TestCase {
    //~ Constructors ===================================================================================================

    public CasProxyTicketValidatorTests() {
        super();
    }

    public CasProxyTicketValidatorTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(CasProxyTicketValidatorTests.class);
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testGetters() {
        CasProxyTicketValidator tv = new CasProxyTicketValidator();
        tv.setProxyCallbackUrl("http://my.com/webapp/casProxy/someValidator");
        assertEquals("http://my.com/webapp/casProxy/someValidator", tv.getProxyCallbackUrl());
    }

    public void testNormalOperation() {
        ServiceProperties sp = new ServiceProperties();
        sp.setSendRenew(true);
        sp.setService("https://my.com/webapp//j_acegi_cas_security_check");

        CasProxyTicketValidator tv = new MockCasProxyTicketValidator(true, false);
        tv.setCasValidate("https://company.com/cas/proxyvalidate");
        tv.setServiceProperties(sp);
        tv.setProxyCallbackUrl("http://my.com/webapp/casProxy/someValidator");

        TicketResponse response = tv.confirmTicketValid("ST-0-ER94xMJmn6pha35CQRoZ");

        assertEquals("user", response.getUser());
    }

    public void testProxyTicketValidatorInternalExceptionsGracefullyHandled() {
        CasProxyTicketValidator tv = new MockCasProxyTicketValidator(false, true);
        tv.setCasValidate("https://company.com/cas/proxyvalidate");
        tv.setServiceProperties(new ServiceProperties());
        tv.setProxyCallbackUrl("http://my.com/webapp/casProxy/someValidator");

        try {
            tv.confirmTicketValid("ST-0-ER94xMJmn6pha35CQRoZ");
            fail("Should have thrown AuthenticationServiceException");
        } catch (AuthenticationServiceException expected) {
            assertTrue(true);
        }
    }

    public void testValidationFailsOkAndOperationWithoutAProxyCallbackUrl() {
        CasProxyTicketValidator tv = new MockCasProxyTicketValidator(false, false);
        tv.setCasValidate("https://company.com/cas/proxyvalidate");
        tv.setServiceProperties(new ServiceProperties());

        try {
            tv.confirmTicketValid("ST-0-ER94xMJmn6pha35CQRoZ");
            fail("Should have thrown BadCredentialsExpected");
        } catch (BadCredentialsException expected) {
            assertTrue(true);
        }
    }

    //~ Inner Classes ==================================================================================================

    private class MockCasProxyTicketValidator extends CasProxyTicketValidator {
        private boolean returnTicket;
        private boolean throwAuthenticationServiceException;

        public MockCasProxyTicketValidator(boolean returnTicket, boolean throwAuthenticationServiceException) {
            this.returnTicket = returnTicket;
            this.throwAuthenticationServiceException = throwAuthenticationServiceException;
        }

        private MockCasProxyTicketValidator() {
            super();
        }

        protected TicketResponse validateNow(ProxyTicketValidator pv)
            throws AuthenticationServiceException, BadCredentialsException {
            if (returnTicket) {
                return new TicketResponse("user", new Vector(),
                    "PGTIOU-0-R0zlgrl4pdAQwBvJWO3vnNpevwqStbSGcq3vKB2SqSFFRnjPHt");
            }

            if (throwAuthenticationServiceException) {
                throw new AuthenticationServiceException("As requested by mock");
            }

            throw new BadCredentialsException("As requested by mock");
        }
    }
}
