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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * A simple filter that the test case can delegate to.
 *
 * @author Ben Alex
 * @version $Id: MockFilter.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class MockFilter implements Filter {
    //~ Instance fields ================================================================================================

    private boolean wasDestroyed = false;
    private boolean wasDoFiltered = false;
    private boolean wasInitialized = false;

    //~ Methods ========================================================================================================

    public void destroy() {
        wasDestroyed = true;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        wasDoFiltered = true;
        chain.doFilter(request, response);
    }

    public void init(FilterConfig config) throws ServletException {
        wasInitialized = true;
    }

    public boolean isWasDestroyed() {
        return wasDestroyed;
    }

    public boolean isWasDoFiltered() {
        return wasDoFiltered;
    }

    public boolean isWasInitialized() {
        return wasInitialized;
    }
}
