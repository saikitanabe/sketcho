/*
 * Copyright 2005-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acegisecurity.ui.portlet;

import java.util.ArrayList;
import java.util.HashMap;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import junit.framework.TestCase;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.TestingAuthenticationToken;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.portlet.PortletAuthenticationToken;
import org.acegisecurity.providers.portlet.PortletTestUtils;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.userdetails.User;
import org.springframework.mock.web.portlet.MockActionRequest;
import org.springframework.mock.web.portlet.MockActionResponse;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;

/**
 * Tests {@link PortletProcessingInterceptor}.
 *
 * @author John A. Lewis
 * @since 2.0
 * @version $Id$
 */
public class PortletProcessingInterceptorTests extends TestCase {

	//~ Constructors ===================================================================================================

	public PortletProcessingInterceptorTests() {
		super();
	}

	public PortletProcessingInterceptorTests(String arg0) {
		super(arg0);
	}

	//~ Methods ========================================================================================================

	public void setUp() throws Exception {
		super.setUp();
		SecurityContextHolder.clearContext();
	}

	public void tearDown() throws Exception {
		super.tearDown();
		SecurityContextHolder.clearContext();
	}

	public void testRequiresAuthenticationManager() throws Exception {
		PortletProcessingInterceptor interceptor = new PortletProcessingInterceptor();
		try {
			interceptor.afterPropertiesSet();
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// ignored
		}
	}

	public void testNormalRenderRequestProcessing() throws Exception {

		// Build mock request and response
		MockRenderRequest request = PortletTestUtils.createRenderRequest();
		MockRenderResponse response = PortletTestUtils.createRenderResponse();

		// Prepare interceptor
		PortletProcessingInterceptor interceptor = new PortletProcessingInterceptor();
		interceptor.setAuthenticationManager(new MockPortletAuthenticationManager());
		interceptor.afterPropertiesSet();

		// Execute preHandlerRender phase and verify results
		interceptor.preHandleRender(request, response, null);
		assertEquals(PortletTestUtils.createAuthenticatedToken(),
				SecurityContextHolder.getContext().getAuthentication());

		// Execute postHandlerRender phase and verify nothing changed
		interceptor.postHandleRender(request, response, null, null);
		assertEquals(PortletTestUtils.createAuthenticatedToken(),
				SecurityContextHolder.getContext().getAuthentication());

		// Execute afterRenderCompletion phase and verify nothing changed
		interceptor.afterRenderCompletion(request, response, null, null);
		assertEquals(PortletTestUtils.createAuthenticatedToken(),
				SecurityContextHolder.getContext().getAuthentication());
	}

	public void testNormalActionRequestProcessing() throws Exception {

		// Build mock request and response
		MockActionRequest request = PortletTestUtils.createActionRequest();
		MockActionResponse response = PortletTestUtils.createActionResponse();

		// Prepare interceptor
		PortletProcessingInterceptor interceptor = new PortletProcessingInterceptor();
		interceptor.setAuthenticationManager(new MockPortletAuthenticationManager());
		interceptor.afterPropertiesSet();

		// Execute preHandlerAction phase and verify results
		interceptor.preHandleAction(request, response, null);
		assertEquals(PortletTestUtils.createAuthenticatedToken(),
				SecurityContextHolder.getContext().getAuthentication());

		// Execute afterActionCompletion phase and verify nothing changed
		interceptor.afterActionCompletion(request, response, null, null);
		assertEquals(PortletTestUtils.createAuthenticatedToken(),
				SecurityContextHolder.getContext().getAuthentication());
	}

	public void testAuthenticationFailsWithNoCredentials()
		throws Exception {

		// Build mock request and response
		MockActionRequest request = new MockActionRequest();
		MockActionResponse response = new MockActionResponse();

		// Prepare and execute interceptor
		PortletProcessingInterceptor interceptor = new PortletProcessingInterceptor();
		interceptor.setAuthenticationManager(new MockPortletAuthenticationManager());
		interceptor.afterPropertiesSet();
		interceptor.preHandleAction(request, response, null);

		// Verify that authentication is empty
		assertNull(SecurityContextHolder.getContext().getAuthentication());

		// Verify that proper exception was thrown
		assertTrue(request.getPortletSession().getAttribute(
					AbstractProcessingFilter.ACEGI_SECURITY_LAST_EXCEPTION_KEY,
					PortletSession.APPLICATION_SCOPE)
					instanceof BadCredentialsException);
	}

	public void testExistingAuthenticationIsLeftAlone() throws Exception {

		// Build mock request and response
		MockActionRequest request = PortletTestUtils.createActionRequest();
		MockActionResponse response = PortletTestUtils.createActionResponse();

		// Prepare interceptor
		PortletProcessingInterceptor interceptor = new PortletProcessingInterceptor();
		interceptor.setAuthenticationManager(new MockPortletAuthenticationManager());
		interceptor.afterPropertiesSet();

		UsernamePasswordAuthenticationToken testingToken = new UsernamePasswordAuthenticationToken("dummy", "dummy", null);
		UsernamePasswordAuthenticationToken baselineToken = new UsernamePasswordAuthenticationToken("dummy", "dummy", null);
		SecurityContextHolder.getContext().setAuthentication(testingToken);

		// Execute preHandlerAction phase and verify results
		interceptor.preHandleAction(request, response, null);
		assertTrue(SecurityContextHolder.getContext().getAuthentication() == testingToken);
		assertEquals(baselineToken, SecurityContextHolder.getContext().getAuthentication());

		// Execute afterActionCompletion phase and verify nothing changed
		interceptor.afterActionCompletion(request, response, null, null);
		assertTrue(SecurityContextHolder.getContext().getAuthentication() == testingToken);
		assertEquals(baselineToken, SecurityContextHolder.getContext().getAuthentication());
	}

	public void testUsernameFromRemoteUser() throws Exception {

		// Build mock request and response
		MockActionRequest request = new MockActionRequest();
		MockActionResponse response = new MockActionResponse();
		request.setRemoteUser(PortletTestUtils.TESTUSER);
		request.setAuthType(PortletRequest.FORM_AUTH);

		// Prepare and execute interceptor
		PortletProcessingInterceptor interceptor = new PortletProcessingInterceptor();
		interceptor.setAuthenticationManager(new MockPortletAuthenticationManager());
		interceptor.afterPropertiesSet();
		interceptor.preHandleAction(request, response, null);

		// Verify username
		assertEquals(PortletTestUtils.TESTUSER,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}

	public void testUsernameFromPrincipal() throws Exception {

		// Build mock request and response
		MockActionRequest request = new MockActionRequest();
		MockActionResponse response = new MockActionResponse();
		request.setUserPrincipal(new TestingAuthenticationToken(PortletTestUtils.TESTUSER, PortletTestUtils.TESTCRED, null));
		request.setAuthType(PortletRequest.FORM_AUTH);

		// Prepare and execute interceptor
		PortletProcessingInterceptor interceptor = new PortletProcessingInterceptor();
		interceptor.setAuthenticationManager(new MockPortletAuthenticationManager());
		interceptor.afterPropertiesSet();
		interceptor.preHandleAction(request, response, null);

		// Verify username
		assertEquals(PortletTestUtils.TESTUSER,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}

	public void testUsernameFromUserInfo() throws Exception {

		// Build mock request and response
		MockActionRequest request = new MockActionRequest();
		MockActionResponse response = new MockActionResponse();
		HashMap userInfo = new HashMap();
		userInfo.put("user.name.given", "Test");
		userInfo.put("user.name.family", "User");
		userInfo.put("user.id", "mytestuser");
		request.setAttribute(PortletRequest.USER_INFO, userInfo);
		request.setAuthType(PortletRequest.FORM_AUTH);

		// Prepare and execute interceptor
		PortletProcessingInterceptor interceptor = new PortletProcessingInterceptor();
		interceptor.setAuthenticationManager(new MockPortletAuthenticationManager());
		ArrayList userNameAttributes = new ArrayList();
		userNameAttributes.add("user.name");
		userNameAttributes.add("user.id");
		interceptor.setUserNameAttributes(userNameAttributes);
		interceptor.afterPropertiesSet();
		interceptor.preHandleAction(request, response, null);

		// Verify username
		assertEquals("mytestuser", SecurityContextHolder.getContext().getAuthentication().getName());
	}

	//~ Inner Classes ==================================================================================================

	private static class MockPortletAuthenticationManager implements AuthenticationManager {

		public Authentication authenticate(Authentication token) {

			// Make sure we got a valid token
			if (!(token instanceof PortletAuthenticationToken)) {
				TestCase.fail("Expected PortletAuthentication object-- got: " + token);
			}

			// Make sure the token details are the PortletRequest
			if (!(token.getDetails() instanceof PortletRequest)) {
				TestCase.fail("Expected Authentication.getDetails to be a PortletRequest object -- got: " + token.getDetails());
			}

			// Make sure it's got a principal
			if (token.getPrincipal() == null) {
				throw new BadCredentialsException("Mock authentication manager rejecting null principal");
			}

			// Make sure it's got credentials
			if (token.getCredentials() == null) {
				throw new BadCredentialsException("Mock authentication manager rejecting null credentials");
			}

			// create resulting Authentication object
			User user = new User(token.getName(), token.getCredentials().toString(), true, true, true, true,
					new GrantedAuthority[] {new GrantedAuthorityImpl(PortletTestUtils.TESTROLE1), new GrantedAuthorityImpl(PortletTestUtils.TESTROLE2)});
			PortletAuthenticationToken result = new PortletAuthenticationToken(
					user, user.getPassword(), user.getAuthorities());
			result.setAuthenticated(true);
			return result;
		}

	}

}
