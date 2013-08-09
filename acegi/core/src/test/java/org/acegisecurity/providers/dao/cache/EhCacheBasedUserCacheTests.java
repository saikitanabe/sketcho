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

package org.acegisecurity.providers.dao.cache;

import junit.framework.TestCase;

import net.sf.ehcache.Ehcache;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.MockApplicationContext;

import org.acegisecurity.userdetails.User;

import org.springframework.context.ApplicationContext;


/**
 * Tests {@link EhCacheBasedUserCache}.
 *
 * @author Ben Alex
 * @version $Id: EhCacheBasedUserCacheTests.java 1965 2007-08-27 23:41:59Z luke_t $
 */
public class EhCacheBasedUserCacheTests extends TestCase {
    //~ Constructors ===================================================================================================

    public EhCacheBasedUserCacheTests() {
    }

    public EhCacheBasedUserCacheTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    private Ehcache getCache() {
        ApplicationContext ctx = MockApplicationContext.getContext();

        return (Ehcache) ctx.getBean("eHCacheBackend");
    }

    private User getUser() {
        return new User("john", "password", true, true, true, true,
            new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testCacheOperation() throws Exception {
        EhCacheBasedUserCache cache = new EhCacheBasedUserCache();
        cache.setCache(getCache());
        cache.afterPropertiesSet();

        // Check it gets stored in the cache
        cache.putUserInCache(getUser());
        assertEquals(getUser().getPassword(), cache.getUserFromCache(getUser().getUsername()).getPassword());

        // Check it gets removed from the cache
        cache.removeUserFromCache(getUser());
        assertNull(cache.getUserFromCache(getUser().getUsername()));

        // Check it doesn't return values for null or unknown users
        assertNull(cache.getUserFromCache(null));
        assertNull(cache.getUserFromCache("UNKNOWN_USER"));
    }

    public void testStartupDetectsMissingCache() throws Exception {
        EhCacheBasedUserCache cache = new EhCacheBasedUserCache();

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
