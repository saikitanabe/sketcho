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

package org.acegisecurity.providers.x509.cache;

import junit.framework.TestCase;

import net.sf.ehcache.Ehcache;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.MockApplicationContext;

import org.acegisecurity.providers.x509.X509TestUtils;

import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;

import org.springframework.context.ApplicationContext;


/**
 * Tests for {@link EhCacheBasedX509UserCache}.
 *
 * @author Luke Taylor
 * @version $Id: EhCacheBasedX509UserCacheTests.java 1965 2007-08-27 23:41:59Z luke_t $
 */
public class EhCacheBasedX509UserCacheTests extends TestCase {
    //~ Constructors ===================================================================================================

    public EhCacheBasedX509UserCacheTests() {
    }

    public EhCacheBasedX509UserCacheTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    private Ehcache getCache() {
        ApplicationContext ctx = MockApplicationContext.getContext();

        return (Ehcache) ctx.getBean("eHCacheBackend");
    }

    private UserDetails getUser() {
        return new User("marissa", "password", true, true, true, true,
            new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ONE"), new GrantedAuthorityImpl("ROLE_TWO")});
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testCacheOperation() throws Exception {
        EhCacheBasedX509UserCache cache = new EhCacheBasedX509UserCache();
        cache.setCache(getCache());
        cache.afterPropertiesSet();

        // Check it gets stored in the cache
        cache.putUserInCache(X509TestUtils.buildTestCertificate(), getUser());
        assertEquals(getUser().getPassword(), cache.getUserFromCache(X509TestUtils.buildTestCertificate()).getPassword());

        // Check it gets removed from the cache
        cache.removeUserFromCache(X509TestUtils.buildTestCertificate());
        assertNull(cache.getUserFromCache(X509TestUtils.buildTestCertificate()));

        // Check it doesn't return values for null user
        assertNull(cache.getUserFromCache(null));
    }
}
