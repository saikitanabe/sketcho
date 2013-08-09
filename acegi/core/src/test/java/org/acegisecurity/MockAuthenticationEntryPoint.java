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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.ui.AuthenticationEntryPoint;


/**
 * Performs a HTTP redirect to the constructor-indicated URL.
 *
 * @author Ben Alex
 * @version $Id: MockAuthenticationEntryPoint.java 1948 2007-08-25 00:15:30Z benalex $
 */
public class MockAuthenticationEntryPoint implements AuthenticationEntryPoint {
    //~ Instance fields ================================================================================================

    private String url;

    //~ Constructors ===================================================================================================

	public MockAuthenticationEntryPoint(String url) {
        this.url = url;
    }

    private MockAuthenticationEntryPoint() {
        super();
    }

    //~ Methods ========================================================================================================

    public void commence(ServletRequest request, ServletResponse response,
        AuthenticationException authenticationException)
        throws IOException, ServletException {
        ((HttpServletResponse) response).sendRedirect(((HttpServletRequest) request).getContextPath() + url);
    }
}
