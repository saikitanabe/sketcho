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

import org.acegisecurity.GrantedAuthority;

import org.acegisecurity.adapters.AbstractAdapterAuthenticationToken;

import org.mortbay.http.UserPrincipal;


/**
 * A Jetty compatible {@link org.acegisecurity.Authentication} object.
 *
 * @author Ben Alex
 * @version $Id: JettyAcegiUserToken.java 1680 2006-09-15 08:38:11Z benalex $
 */
public class JettyAcegiUserToken extends AbstractAdapterAuthenticationToken implements UserPrincipal {
    //~ Instance fields ================================================================================================

	private static final long serialVersionUID = 1L;
    private String password;
    private String username;

    //~ Constructors ===================================================================================================

    public JettyAcegiUserToken(String key, String username, String password, GrantedAuthority[] authorities) {
        super(key, authorities);
        this.username = username;
        this.password = password;
    }

    protected JettyAcegiUserToken() {
        throw new IllegalArgumentException("Cannot use default constructor");
    }

    //~ Methods ========================================================================================================

    public Object getCredentials() {
        return this.password;
    }

    public String getName() {
        return this.username;
    }

    public Object getPrincipal() {
        return this.username;
    }
}
