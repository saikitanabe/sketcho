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
package org.acegisecurity.providers.cas.cache;

import java.util.ArrayList;
import java.util.List;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.providers.cas.CasAuthenticationToken;
import org.acegisecurity.providers.cas.StatelessTicketCache;
import org.acegisecurity.userdetails.User;

import junit.framework.TestCase;

/**
 * Test cases for the @link {@link NullStatelessTicketCache}
 * 
 * @author Scott Battaglia
 * @version $Id$
 *
 */
public class NullStatelessTicketCacheTests extends TestCase {

	private StatelessTicketCache cache = new NullStatelessTicketCache();
	
	public void testGetter() {
		assertNull(cache.getByTicketId(null));
		assertNull(cache.getByTicketId("test"));
	}
	
	public void testInsertAndGet() {
		final CasAuthenticationToken token = getToken();
		cache.putTicketInCache(token);
		assertNull(cache.getByTicketId((String) token.getCredentials()));
	}

	private CasAuthenticationToken getToken() {
        List proxyList = new ArrayList();
        proxyList.add("https://localhost/newPortal/j_spring_cas_security_check");

        User user = new User("marissa", "password", true, true, true, true,
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        return new CasAuthenticationToken("key", user, "ST-0-ER94xMJmn6pha35CQRoZ",
            new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")}, user,
            proxyList, "PGTIOU-0-R0zlgrl4pdAQwBvJWO3vnNpevwqStbSGcq3vKB2SqSFFRnjPHt");
    }
}
