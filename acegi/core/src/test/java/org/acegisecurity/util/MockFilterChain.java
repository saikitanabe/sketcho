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

package org.acegisecurity.util;

import junit.framework.TestCase;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * A mock <code>FilterChain</code>.
 *
 * @author Ben Alex
 * @version $Id: MockFilterChain.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class MockFilterChain implements FilterChain {
    //~ Instance fields ================================================================================================

    private boolean expectToProceed;

    //~ Constructors ===================================================================================================

    public MockFilterChain(boolean expectToProceed) {
        this.expectToProceed = expectToProceed;
    }

    //~ Methods ========================================================================================================

    public void doFilter(ServletRequest request, ServletResponse response)
        throws IOException, ServletException {
        if (expectToProceed) {
            TestCase.assertTrue(true);
        } else {
            TestCase.fail("Did not expect filter chain to proceed");
        }
    }
}
