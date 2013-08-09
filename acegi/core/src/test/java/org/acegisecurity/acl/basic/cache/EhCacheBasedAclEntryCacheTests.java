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

package org.acegisecurity.acl.basic.cache;

import junit.framework.TestCase;

import net.sf.ehcache.Ehcache;

import org.acegisecurity.MockApplicationContext;

import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.BasicAclEntry;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;
import org.acegisecurity.acl.basic.SimpleAclEntry;

import org.springframework.context.ApplicationContext;


/**
 * Tests {@link EhCacheBasedAclEntryCache}.
 *
 * @author Ben Alex
 * @version $Id: EhCacheBasedAclEntryCacheTests.java 1965 2007-08-27 23:41:59Z luke_t $
 */
public class EhCacheBasedAclEntryCacheTests extends TestCase {
    //~ Static fields/initializers =====================================================================================

    private static final AclObjectIdentity OBJECT_100 = new NamedEntityObjectIdentity("OBJECT", "100");
    private static final AclObjectIdentity OBJECT_200 = new NamedEntityObjectIdentity("OBJECT", "200");
    private static final BasicAclEntry OBJECT_100_MARISSA = new SimpleAclEntry("marissa", OBJECT_100, null, 2);
    private static final BasicAclEntry OBJECT_100_SCOTT = new SimpleAclEntry("scott", OBJECT_100, null, 4);
    private static final BasicAclEntry OBJECT_200_PETER = new SimpleAclEntry("peter", OBJECT_200, null, 4);

    //~ Constructors ===================================================================================================

    public EhCacheBasedAclEntryCacheTests() {
        super();
    }

    public EhCacheBasedAclEntryCacheTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    private Ehcache getCache() {
        ApplicationContext ctx = MockApplicationContext.getContext();

        return (Ehcache) ctx.getBean("eHCacheBackend");
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testCacheOperation() throws Exception {
        EhCacheBasedAclEntryCache cache = new EhCacheBasedAclEntryCache();
        cache.setCache(getCache());
        cache.afterPropertiesSet();

        cache.putEntriesInCache(new BasicAclEntry[] {OBJECT_100_SCOTT, OBJECT_100_MARISSA});
        cache.putEntriesInCache(new BasicAclEntry[] {OBJECT_200_PETER});

        // Check we can get them from cache again
        assertEquals(OBJECT_100_SCOTT, cache.getEntriesFromCache(new NamedEntityObjectIdentity("OBJECT", "100"))[0]);
        assertEquals(OBJECT_100_MARISSA, cache.getEntriesFromCache(new NamedEntityObjectIdentity("OBJECT", "100"))[1]);
        assertEquals(OBJECT_200_PETER, cache.getEntriesFromCache(new NamedEntityObjectIdentity("OBJECT", "200"))[0]);
        assertNull(cache.getEntriesFromCache(new NamedEntityObjectIdentity("OBJECT", "NOT_IN_CACHE")));

        // Check after eviction we cannot get them from cache
        cache.removeEntriesFromCache(new NamedEntityObjectIdentity("OBJECT", "100"));
        assertNull(cache.getEntriesFromCache(new NamedEntityObjectIdentity("OBJECT", "100")));
    }

    public void testStartupDetectsMissingCache() throws Exception {
        EhCacheBasedAclEntryCache cache = new EhCacheBasedAclEntryCache();

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
