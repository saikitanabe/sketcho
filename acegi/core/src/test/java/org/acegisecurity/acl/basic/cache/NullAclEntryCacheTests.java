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

import org.acegisecurity.acl.basic.BasicAclEntry;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;
import org.acegisecurity.acl.basic.SimpleAclEntry;


/**
 * Tests {@link NullAclEntryCache}.
 *
 * @author Ben Alex
 * @version $Id: NullAclEntryCacheTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class NullAclEntryCacheTests extends TestCase {
    //~ Constructors ===================================================================================================

    public NullAclEntryCacheTests() {
        super();
    }

    public NullAclEntryCacheTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NullAclEntryCacheTests.class);
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testCacheOperation() throws Exception {
        NullAclEntryCache cache = new NullAclEntryCache();
        cache.putEntriesInCache(new BasicAclEntry[] {new SimpleAclEntry()});
        cache.getEntriesFromCache(new NamedEntityObjectIdentity("not_used", "not_used"));
        cache.removeEntriesFromCache(new NamedEntityObjectIdentity("not_used", "not_used"));
    }
}
