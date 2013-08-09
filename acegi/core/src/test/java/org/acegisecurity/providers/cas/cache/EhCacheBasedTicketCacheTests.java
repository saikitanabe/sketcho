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

import junit.framework.TestCase;

import net.sf.ehcache.Ehcache;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.MockApplicationContext;

import org.acegisecurity.providers.cas.CasAuthenticationToken;

import org.acegisecurity.userdetails.User;

import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Vector;


/**
 * Tests {@link EhCacheBasedTicketCache}.
 *
 * @author Ben Alex
 * @version $Id: EhCacheBasedTicketCacheTests.java 1965 2007-08-27 23:41:59Z luke_t $
 */
public class EhCacheBasedTicketCacheTests extends TestCase {
    //~ Constructors ===================================================================================================

    public EhCacheBasedTicketCacheTests() {
    }

    public EhCacheBasedTicketCacheTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    private Ehcache getCache() {
        ApplicationContext ctx = MockApplicationContext.getContext();

        return (Ehcache) ctx.getBean("eHCacheBackend");
    }

    private CasAuthenticationToken getToken() {
        List proxyList = new Vector();
        proxyList.add("https://localhost/newPortal/j_acegi_cas_security_check");

        User user = new User("marissa", "password", true, true, true, true,
                new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});

        return new CasAuthenticationToken("key", user, "ST-0-ER94xMJmn6pha35CQRoZ",
            new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")}, user,
            proxyList, "PGTIOU-0-R0zlgrl4pdAQwBvJWO3vnNpevwqStbSGcq3vKB2SqSFFRnjPHt");
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testCacheOperation() throws Exception {
        EhCacheBasedTicketCache cache = new EhCacheBasedTicketCache();
        cache.setCache(getCache());
        cache.afterPropertiesSet();

        // Check it gets stored in the cache
        cache.putTicketInCache(getToken());
        assertEquals(getToken(), cache.getByTicketId("ST-0-ER94xMJmn6pha35CQRoZ"));

        // Check it gets removed from the cache
        cache.removeTicketFromCache(getToken());
        assertNull(cache.getByTicketId("ST-0-ER94xMJmn6pha35CQRoZ"));

        // Check it doesn't return values for null or unknown service tickets
        assertNull(cache.getByTicketId(null));
        assertNull(cache.getByTicketId("UNKNOWN_SERVICE_TICKET"));
    }

    public void testStartupDetectsMissingCache() throws Exception {
        EhCacheBasedTicketCache cache = new EhCacheBasedTicketCache();

        try {
            cache.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        Ehcache myCache = getCache();
        cache.setCache(myCache);
        assertEquals(myCache, cache.getCache());
    }
}
