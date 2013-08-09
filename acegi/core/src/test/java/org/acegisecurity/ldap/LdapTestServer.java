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

import org.apache.directory.server.core.configuration.Configuration;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.configuration.MutableStartupConfiguration;
import org.apache.directory.server.core.jndi.CoreContextFactory;
import org.apache.directory.server.core.partition.DefaultPartitionNexus;

import java.io.File;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;


/**
 * An embedded LDAP test server, complete with test data for running the unit tests against.
 *
 * @author Luke Taylor
 * @version $Id: LdapTestServer.java 1968 2007-08-28 15:26:59Z luke_t $
 */
public class LdapTestServer {
    //~ Instance fields ================================================================================================

    private DirContext serverContext;

    private MutableStartupConfiguration cfg;

    //~ Constructors ===================================================================================================

    /**
     * Starts up and configures ApacheDS.
     */
    public LdapTestServer() {
        startLdapServer();
        createManagerUser();
        initTestData();
    }

    //~ Methods ========================================================================================================

    public void createGroup(String cn, String groupContext, String ou, String[] memberDns) {
        Attributes group = new BasicAttributes("cn", cn);
        Attribute members = new BasicAttribute("member");
        Attribute orgUnit = new BasicAttribute("ou", ou);

        for (int i = 0; i < memberDns.length; i++) {
            members.add(memberDns[i]);
        }

        Attribute objectClass = new BasicAttribute("objectClass");
        objectClass.add("top");
        objectClass.add("groupOfNames");

        group.put(objectClass);
        group.put(members);
        group.put(orgUnit);

        try {
            DirContext ctx = serverContext.createSubcontext("cn=" + cn + "," + groupContext, group);
            System.out.println("Created group " + ctx.getNameInNamespace());
        } catch (NameAlreadyBoundException ignore) {
//            System.out.println(" group " + cn + " already exists.");
        } catch (NamingException ne) {
            System.err.println("Failed to create group.");
            ne.printStackTrace();
        }
    }

    private void createManagerUser() {
        Attributes user = new BasicAttributes("cn", "manager", true);
        user.put("userPassword", "acegisecurity");

        Attribute objectClass = new BasicAttribute("objectClass");
        user.put(objectClass);
        objectClass.add("top");
        objectClass.add("person");
        objectClass.add("organizationalPerson");
        objectClass.add("inetOrgPerson");
        user.put("sn", "Manager");
        user.put("cn", "manager");

        try {
            serverContext.createSubcontext("cn=manager", user);
        } catch (NameAlreadyBoundException ignore) {
            //           System.out.println("Manager user already exists.");
        } catch (NamingException ne) {
            System.err.println("Failed to create manager user.");
            ne.printStackTrace();
        }
    }

    public void createOu(String name) {
        Attributes ou = new BasicAttributes("ou", name);
        Attribute objectClass = new BasicAttribute("objectClass");
        objectClass.add("top");
        objectClass.add("organizationalUnit");
        ou.put(objectClass);

        try {
            serverContext.createSubcontext(name, ou);
        } catch (NameAlreadyBoundException ignore) {
            //           System.out.println(" ou " + name + " already exists.");
        } catch (NamingException ne) {
            System.err.println("Failed to create ou.");
            ne.printStackTrace();
        }
    }

    public void createUser(String uid, String cn, String password) {
        Attributes user = new BasicAttributes("uid", uid);
        user.put("cn", cn);
        user.put("userPassword", LdapUtils.getUtf8Bytes(password));

        Attribute objectClass = new BasicAttribute("objectClass");
        user.put(objectClass);
        objectClass.add("top");
        objectClass.add("person");
        objectClass.add("organizationalPerson");
        objectClass.add("inetOrgPerson");
        user.put("sn", uid);

        try {
            serverContext.createSubcontext("uid=" + uid + ",ou=people", user);
        } catch (NameAlreadyBoundException ignore) {
//            System.out.println(" user " + uid + " already exists.");
        } catch (NamingException ne) {
            System.err.println("Failed to create  user.");
            ne.printStackTrace();
        }
    }

    public Configuration getConfiguration() {
        return cfg;
    }

    private void initConfiguration() throws NamingException {
        // Create the partition for the acegi tests
        MutablePartitionConfiguration acegiDit = new MutablePartitionConfiguration();
        acegiDit.setName("acegisecurity");
        acegiDit.setSuffix("dc=acegisecurity,dc=org");

        BasicAttributes attributes = new BasicAttributes();
        BasicAttribute objectClass = new BasicAttribute("objectClass");
        objectClass.add("top");
        objectClass.add("domain");
        objectClass.add("extensibleObject");
        attributes.put(objectClass);
        acegiDit.setContextEntry(attributes);

        Set indexedAttrs = new HashSet();
        indexedAttrs.add("objectClass");
        indexedAttrs.add("uid");
        indexedAttrs.add("cn");
        indexedAttrs.add("ou");
        indexedAttrs.add("member");

        acegiDit.setIndexedAttributes(indexedAttrs);

        Set partitions = new HashSet();
        partitions.add(acegiDit);

        cfg.setContextPartitionConfigurations(partitions);
    }

    private void initTestData() {
        createOu("ou=people");
        createOu("ou=groups");
        createOu("ou=subgroups,ou=groups");

        createUser("bob", "Bob Hamilton", "bobspassword");
        createUser("ben", "Ben Alex", "{SHA}nFCebWjxfaLbHHG1Qk5UU4trbvQ=");

        String[] developers = new String[]{
                "uid=ben,ou=people,dc=acegisecurity,dc=org", "uid=bob,ou=people,dc=acegisecurity,dc=org"
        };
        createGroup("developers", "ou=groups", "developer", developers);
        createGroup("managers", "ou=groups", "manager", new String[]{developers[0]});
        createGroup("submanagers", "ou=subgroups,ou=groups", "submanager", new String[]{developers[0]});
    }

    public static void main(String[] args) {
        LdapTestServer server = new LdapTestServer();
    }

    private void startLdapServer() {
        cfg = new MutableStartupConfiguration();

        // Attempt to use the maven target directory for the apache ds store. Property is passed
        // through surefire plugin setup in pom.xml.

        String apacheWorkDir = System.getProperty("apacheDSWorkDir");

        if (apacheWorkDir == null) {
            apacheWorkDir = System.getProperty("java.io.tmpdir") + File.separator + "apacheds-work";
        }

        File workingDir = new File(apacheWorkDir);

        // Delete any previous contents (often not compatible between apache-ds versions).
        deleteDir(workingDir);

        ((MutableStartupConfiguration) cfg).setWorkingDirectory(workingDir);

        System.out.println("Ldap Server Working directory is " + workingDir.getAbsolutePath());

        Properties env = new Properties();

        env.setProperty(Context.PROVIDER_URL, "dc=acegisecurity,dc=org");
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, CoreContextFactory.class.getName());
        env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");
        env.setProperty(Context.SECURITY_PRINCIPAL, DefaultPartitionNexus.ADMIN_PRINCIPAL);
        env.setProperty(Context.SECURITY_CREDENTIALS, DefaultPartitionNexus.ADMIN_PASSWORD);

        try {
            initConfiguration();
            env.putAll(cfg.toJndiEnvironment());
            serverContext = new InitialDirContext(env);
        } catch (NamingException e) {
            System.err.println("Failed to start Apache DS");
            e.printStackTrace();
        }
    }

    /**
     * Recursively deletes a directory
     */
    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }
}
