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

package org.acegisecurity.userdetails.jdbc;

import junit.framework.TestCase;

import org.acegisecurity.PopulatedDatabase;

import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;

import org.springframework.jdbc.object.MappingSqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashSet;


/**
 * Tests {@link JdbcDaoImpl}.
 *
 * @author Ben Alex
 * @version $Id: JdbcDaoTests.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class JdbcDaoTests extends TestCase {
    //~ Constructors ===================================================================================================

    public JdbcDaoTests() {
        super();
    }

    public JdbcDaoTests(String arg0) {
        super(arg0);
    }

    //~ Methods ========================================================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(JdbcDaoTests.class);
    }

    private JdbcDaoImpl makePopulatedJdbcDao() throws Exception {
        JdbcDaoImpl dao = new JdbcDaoImpl();
        dao.setDataSource(PopulatedDatabase.getDataSource());
        dao.afterPropertiesSet();

        return dao;
    }

    private JdbcDaoImpl makePopulatedJdbcDaoWithRolePrefix()
        throws Exception {
        JdbcDaoImpl dao = new JdbcDaoImpl();
        dao.setDataSource(PopulatedDatabase.getDataSource());
        dao.setRolePrefix("ARBITRARY_PREFIX_");
        dao.afterPropertiesSet();

        return dao;
    }

    public final void setUp() throws Exception {
        super.setUp();
    }

    public void testCheckDaoAccessUserSuccess() throws Exception {
        JdbcDaoImpl dao = makePopulatedJdbcDao();
        UserDetails user = dao.loadUserByUsername("marissa");
        assertEquals("marissa", user.getUsername());
        assertEquals("koala", user.getPassword());
        assertTrue(user.isEnabled());

        HashSet authorities = new HashSet(2);
        authorities.add(user.getAuthorities()[0].getAuthority());
        authorities.add(user.getAuthorities()[1].getAuthority());
        assertTrue(authorities.contains("ROLE_TELLER"));
        assertTrue(authorities.contains("ROLE_SUPERVISOR"));
    }

    public void testCheckDaoOnlyReturnsGrantedAuthoritiesGrantedToUser()
        throws Exception {
        JdbcDaoImpl dao = makePopulatedJdbcDao();
        UserDetails user = dao.loadUserByUsername("scott");
        assertEquals("ROLE_TELLER", user.getAuthorities()[0].getAuthority());
        assertEquals(1, user.getAuthorities().length);
    }

    public void testCheckDaoReturnsCorrectDisabledProperty()
        throws Exception {
        JdbcDaoImpl dao = makePopulatedJdbcDao();
        UserDetails user = dao.loadUserByUsername("peter");
        assertTrue(!user.isEnabled());
    }

    public void testGettersSetters() {
        JdbcDaoImpl dao = new JdbcDaoImpl();
        dao.setAuthoritiesByUsernameQuery("SELECT * FROM FOO");
        assertEquals("SELECT * FROM FOO", dao.getAuthoritiesByUsernameQuery());

        dao.setUsersByUsernameQuery("SELECT USERS FROM FOO");
        assertEquals("SELECT USERS FROM FOO", dao.getUsersByUsernameQuery());
    }

    public void testLookupFailsIfUserHasNoGrantedAuthorities()
        throws Exception {
        JdbcDaoImpl dao = makePopulatedJdbcDao();

        try {
            dao.loadUserByUsername("cooper");
            fail("Should have thrown UsernameNotFoundException");
        } catch (UsernameNotFoundException expected) {
            assertEquals("User has no GrantedAuthority", expected.getMessage());
        }
    }

    public void testLookupFailsWithWrongUsername() throws Exception {
        JdbcDaoImpl dao = makePopulatedJdbcDao();

        try {
            dao.loadUserByUsername("UNKNOWN_USER");
            fail("Should have thrown UsernameNotFoundException");
        } catch (UsernameNotFoundException expected) {
            assertTrue(true);
        }
    }

    public void testLookupSuccessWithMixedCase() throws Exception {
        JdbcDaoImpl dao = makePopulatedJdbcDao();
        assertEquals("koala", dao.loadUserByUsername("MaRiSSA").getPassword());
        assertEquals("wombat", dao.loadUserByUsername("ScOTt").getPassword());
    }

    public void testRolePrefixWorks() throws Exception {
        JdbcDaoImpl dao = makePopulatedJdbcDaoWithRolePrefix();
        assertEquals("ARBITRARY_PREFIX_", dao.getRolePrefix());

        UserDetails user = dao.loadUserByUsername("marissa");
        assertEquals("marissa", user.getUsername());
        assertEquals(2, user.getAuthorities().length);

        HashSet authorities = new HashSet(2);
        authorities.add(user.getAuthorities()[0].getAuthority());
        authorities.add(user.getAuthorities()[1].getAuthority());
        assertTrue(authorities.contains("ARBITRARY_PREFIX_ROLE_TELLER"));
        assertTrue(authorities.contains("ARBITRARY_PREFIX_ROLE_SUPERVISOR"));
    }

    public void testStartupFailsIfDataSourceNotSet() throws Exception {
        JdbcDaoImpl dao = new JdbcDaoImpl();

        try {
            dao.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    public void testStartupFailsIfUserMapSetToNull() throws Exception {
        JdbcDaoImpl dao = new JdbcDaoImpl();

        try {
            dao.setDataSource(null);
            dao.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    //~ Inner Classes ==================================================================================================

    private class MockMappingSqlQuery extends MappingSqlQuery {
        protected Object mapRow(ResultSet arg0, int arg1)
            throws SQLException {
            return null;
        }
    }
}
