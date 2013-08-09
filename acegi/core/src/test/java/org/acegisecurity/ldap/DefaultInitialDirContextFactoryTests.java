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

package org.acegisecurity.ldap;

import org.acegisecurity.AcegiMessageSource;
import org.acegisecurity.BadCredentialsException;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;


/**
 * Tests {@link org.acegisecurity.ldap.DefaultInitialDirContextFactory}.
 *
 * @author Luke Taylor
 * @version $Id: DefaultInitialDirContextFactoryTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class DefaultInitialDirContextFactoryTests extends AbstractLdapServerTestCase {
    //~ Instance fields ================================================================================================

    DefaultInitialDirContextFactory idf;

    //~ Methods ========================================================================================================

    public void onSetUp() {
        idf = getInitialCtxFactory();
        idf.setMessageSource(new AcegiMessageSource());
    }

    public void testAnonymousBindSucceeds() throws Exception {
        DirContext ctx = idf.newInitialDirContext();
        // Connection pooling should be set by default for anon users.
        // Can't rely on this property being there with embedded server
        // assertEquals("true",ctx.getEnvironment().get("com.sun.jndi.ldap.connect.pool"));
        ctx.close();
    }

    public void testBaseDnIsParsedFromCorrectlyFromUrl() {
        idf = new DefaultInitialDirContextFactory("ldap://acegisecurity.org/dc=acegisecurity,dc=org");
        assertEquals("dc=acegisecurity,dc=org", idf.getRootDn());

        // Check with an empty root
        idf = new DefaultInitialDirContextFactory("ldap://acegisecurity.org/");
        assertEquals("", idf.getRootDn());

        // Empty root without trailing slash
        idf = new DefaultInitialDirContextFactory("ldap://acegisecurity.org");
        assertEquals("", idf.getRootDn());
    }

    public void testBindAsManagerFailsIfNoPasswordSet()
        throws Exception {
        idf.setManagerDn(MANAGER_USER);

        DirContext ctx = null;

        try {
            ctx = idf.newInitialDirContext();
            fail("Binding with no manager password should fail.");

// Can't rely on this property being there with embedded server
//        assertEquals("true",ctx.getEnvironment().get("com.sun.jndi.ldap.connect.pool"));
        } catch (BadCredentialsException expected) {}

        LdapUtils.closeContext(ctx);
    }

    public void testBindAsManagerSucceeds() throws Exception {
        idf.setManagerPassword(MANAGER_PASSWORD);
        idf.setManagerDn(MANAGER_USER);

        DirContext ctx = idf.newInitialDirContext();
// Can't rely on this property being there with embedded server
//        assertEquals("true",ctx.getEnvironment().get("com.sun.jndi.ldap.connect.pool"));
        ctx.close();
    }

    public void testConnectionAsSpecificUserSucceeds()
        throws Exception {
        DirContext ctx = idf.newInitialDirContext("uid=Bob,ou=people,dc=acegisecurity,dc=org", "bobspassword");
        // We don't want pooling for specific users.
        // assertNull(ctx.getEnvironment().get("com.sun.jndi.ldap.connect.pool"));
//        com.sun.jndi.ldap.LdapPoolManager.showStats(System.out);
        ctx.close();
    }

    public void testConnectionFailure() throws Exception {
        // Use the wrong port
        idf = new DefaultInitialDirContextFactory("ldap://localhost:60389");
        idf.setInitialContextFactory("com.sun.jndi.ldap.LdapCtxFactory");

        Hashtable env = new Hashtable();
        env.put("com.sun.jndi.ldap.connect.timeout", "200");
        idf.setExtraEnvVars(env);
        idf.setUseConnectionPool(false); // coverage purposes only

        try {
            idf.newInitialDirContext();
            fail("Connection succeeded unexpectedly");
        } catch (LdapDataAccessException expected) {}
    }

    public void testEnvironment() {
        idf = new DefaultInitialDirContextFactory("ldap://acegisecurity.org/");

        // check basic env
        Hashtable env = idf.getEnvironment();
        //assertEquals("com.sun.jndi.ldap.LdapCtxFactory", env.get(Context.INITIAL_CONTEXT_FACTORY));
        assertEquals("ldap://acegisecurity.org/", env.get(Context.PROVIDER_URL));
        assertEquals("simple", env.get(Context.SECURITY_AUTHENTICATION));
        assertNull(env.get(Context.SECURITY_PRINCIPAL));
        assertNull(env.get(Context.SECURITY_CREDENTIALS));

        // Ctx factory.
        idf.setInitialContextFactory("org.acegisecurity.NonExistentCtxFactory");
        env = idf.getEnvironment();
        assertEquals("org.acegisecurity.NonExistentCtxFactory", env.get(Context.INITIAL_CONTEXT_FACTORY));

        // Auth type
        idf.setAuthenticationType("myauthtype");
        env = idf.getEnvironment();
        assertEquals("myauthtype", env.get(Context.SECURITY_AUTHENTICATION));

        // Check extra vars
        Hashtable extraVars = new Hashtable();
        extraVars.put("extravar", "extravarvalue");
        idf.setExtraEnvVars(extraVars);
        env = idf.getEnvironment();
        assertEquals("extravarvalue", env.get("extravar"));
    }

    public void testInvalidPasswordCausesBadCredentialsException()
        throws Exception {
        idf.setManagerDn(MANAGER_USER);
        idf.setManagerPassword("wrongpassword");

        DirContext ctx = null;

        try {
            ctx = idf.newInitialDirContext();
            fail("Binding with wrong credentials should fail.");
        } catch (BadCredentialsException expected) {}

        LdapUtils.closeContext(ctx);
    }

    public void testMultipleProviderUrlsAreAccepted() {
        idf = new DefaultInitialDirContextFactory("ldaps://acegisecurity.org/dc=acegisecurity,dc=org "
                + "ldap://monkeymachine.co.uk/dc=acegisecurity,dc=org");
    }

    public void testMultipleProviderUrlsWithDifferentRootsAreRejected() {
        try {
            idf = new DefaultInitialDirContextFactory("ldap://acegisecurity.org/dc=acegisecurity,dc=org "
                    + "ldap://monkeymachine.co.uk/dc=someotherplace,dc=org");
            fail("Different root DNs should cause an exception");
        } catch (IllegalArgumentException expected) {}
    }

    public void testSecureLdapUrlIsSupported() {
        idf = new DefaultInitialDirContextFactory("ldaps://localhost/dc=acegisecurity,dc=org");
        assertEquals("dc=acegisecurity,dc=org", idf.getRootDn());
    }

//    public void testNonLdapUrlIsRejected() throws Exception {
//        DefaultInitialDirContextFactory idf = new DefaultInitialDirContextFactory();
//
//        idf.setUrl("http://acegisecurity.org/dc=acegisecurity,dc=org");
//        idf.setInitialContextFactory(CoreContextFactory.class.getName());
//
//        try {
//            idf.afterPropertiesSet();
//            fail("Expected exception for non 'ldap://' URL");
//        } catch(IllegalArgumentException expected) {
//        }
//    }
    public void testServiceLocationUrlIsSupported() {
        idf = new DefaultInitialDirContextFactory("ldap:///dc=acegisecurity,dc=org");
        assertEquals("dc=acegisecurity,dc=org", idf.getRootDn());
    }
}
