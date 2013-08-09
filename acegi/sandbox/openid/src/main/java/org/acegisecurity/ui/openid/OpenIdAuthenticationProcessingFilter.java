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

package org.acegisecurity.ui.openid;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationServiceException;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.openid.OpenIDAuthenticationToken;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class OpenIdAuthenticationProcessingFilter extends AbstractProcessingFilter {
    //~ Static fields/initializers =====================================================================================

    private static final Log log = LogFactory.getLog(OpenIdAuthenticationProcessingFilter.class);
    public static final String DEFAULT_CLAMED_IDENTITY_FIELD = "j_username";

    //~ Instance fields ================================================================================================

    private OpenIDConsumer consumer;
    private String claimedIdentityFieldName = DEFAULT_CLAMED_IDENTITY_FIELD;
    private String errorPage = "index.jsp";

    //~ Methods ========================================================================================================

    public Authentication attemptAuthentication(HttpServletRequest req)
            throws AuthenticationException {
        OpenIDAuthenticationToken token;

        String identity = req.getParameter("openid.identity");

        if (!StringUtils.hasText(identity)) {
            throw new OpenIdAuthenticationRequiredException("External Authentication Required", obtainUsername(req));
        }

        try {
            token = consumer.endConsumption(req);
        } catch (OpenIDConsumerException oice) {
            throw new AuthenticationServiceException("Consumer error", oice);
        }

        // delegate to the auth provider
        Authentication authentication = this.getAuthenticationManager().authenticate(token);

        if (authentication.isAuthenticated()) {
            req.getSession()
                    .setAttribute(AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY, token.getIdentityUrl());
        }

        return authentication;
    }

    protected String determineFailureUrl(HttpServletRequest request, AuthenticationException failed) {
        if (failed instanceof OpenIdAuthenticationRequiredException) {
            OpenIdAuthenticationRequiredException openIdRequiredException = (OpenIdAuthenticationRequiredException) failed;
            String claimedIdentity = openIdRequiredException.getClaimedIdentity();

            if (StringUtils.hasText(claimedIdentity)) {
                try {
                    String returnToUrl = buildReturnToUrl(request);
                    return consumer.beginConsumption(request, claimedIdentity, returnToUrl);
                } catch (OpenIDConsumerException e) {
                    log.error("Unable to consume claimedIdentity [" + claimedIdentity + "]", e);
                }
            }
        }

        return super.determineFailureUrl(request, failed);
    }

    protected String buildReturnToUrl(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }

    public String getClaimedIdentityFieldName() {
        return claimedIdentityFieldName;
    }

    public OpenIDConsumer getConsumer() {
        return consumer;
    }

    public String getDefaultFilterProcessesUrl() {
        return "/j_acegi_openid_security_check";
    }

    public String getErrorPage() {
        return errorPage;
    }

    protected boolean isAuthenticated(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return (auth != null) && auth.isAuthenticated();
    }

    /**
     * The OpenIdAuthenticationProcessingFilter will ignore the request coming in if this method returns false.
     * The default functionality checks if the request scheme starts with http. <br/
     * > This method should be overridden in subclasses that wish to consider a different strategy
     *
     * @param request HttpServletRequest we're processing
     * @return true if this request is determined to be an OpenID request.
     */
    protected boolean isOpenIdRequest(HttpServletRequest request) {
        String username = obtainUsername(request);
        return (StringUtils.hasText(username)) && username.toLowerCase().startsWith("http");
    }

    protected String obtainUsername(HttpServletRequest req) {
        return req.getParameter(claimedIdentityFieldName);
    }

    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException failed) throws IOException {
        if (failed instanceof OpenIdAuthenticationRequiredException) {
            OpenIdAuthenticationRequiredException openIdAuthenticationRequiredException = (OpenIdAuthenticationRequiredException) failed;
            request.setAttribute(OpenIdAuthenticationRequiredException.class.getName(),
                    openIdAuthenticationRequiredException.getClaimedIdentity());
        }
    }

    public void setClaimedIdentityFieldName(String claimedIdentityFieldName) {
        this.claimedIdentityFieldName = claimedIdentityFieldName;
    }

    public void setConsumer(OpenIDConsumer consumer) {
        this.consumer = consumer;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(null);

        if (logger.isDebugEnabled()) {
            logger.debug("Updated SecurityContextHolder to contain null Authentication");
        }

        String failureUrl = determineFailureUrl(request, failed);

        if (logger.isDebugEnabled()) {
            logger.debug("Authentication request failed: " + failed.toString());
        }

        try {
            request.getSession().setAttribute(ACEGI_SECURITY_LAST_EXCEPTION_KEY, failed);
        } catch (Exception ignored) {
        }

        super.getRememberMeServices().loginFail(request, response);

        sendRedirect(request, response, failureUrl);
    }
}
