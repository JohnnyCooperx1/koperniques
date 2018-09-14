package server.security;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import server.Constants;
import server.common.Utils;


import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class HeaderAuthenticationFilter  extends GenericFilterBean implements
        ApplicationEventPublisherAware {

//    @Inject
//    private Logger logging;

    protected final Log log = LogFactory.getLog(HeaderAuthenticationFilter.class);
    private String principalRequestHeader = "SAFECOPY_REMOTE_USER\"";
    private String testUserId = null;
    private boolean exceptionIfHeaderMissing = false;


    private ApplicationEventPublisher eventPublisher = null;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource
            = new WebAuthenticationDetailsSource();
    private AuthenticationManager authenticationManager = null;
    private boolean continueFilterChainOnUnsuccessfulAuthentication = true;
    private boolean checkForPrincipalChanges;
    private boolean invalidateSessionOnPrincipalChange = true;



    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String principal = request.getHeader(principalRequestHeader);
        if (principal == null) {
            if (exceptionIfHeaderMissing) {
                throw new PreAuthenticatedCredentialsNotFoundException(principalRequestHeader
                        + " header not found in request.");
            }
            if (!Utils.isEmpty(testUserId)) {
                log.warn("spring configuration has a test user id " + testUserId);
                principal = testUserId;
            } else if (request.getSession().getAttribute("session_user") != null) {
// A bit of a hack for testers - allow the principal to be
// obtained by session. Must be set by a page with no security filters enabled.
// should remove for production.
                principal = (String) request.getSession().getAttribute("session_user");
            }
        }
        // also set it into the session, sometimes that's easier for jsp/faces
        // to get at..
        request.getSession().setAttribute("session_user", principal);
        return principal;
    }

    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "password_not_applicable";
    }

    public void setPrincipalRequestHeader(String principalRequestHeader) {
        Assert.hasText(principalRequestHeader, "principalRequestHeader must not be empty or null");
        this.principalRequestHeader = principalRequestHeader;
    }

    public void setTestUserId(String testId) {
        if (!Utils.isEmpty(testId)) {
            this.testUserId = testId;
        }
    }

    public void setExceptionIfHeaderMissing(boolean exceptionIfHeaderMissing) {
        this.exceptionIfHeaderMissing = exceptionIfHeaderMissing;
    }



        /**
         * Check whether all required properties have been set.
         */
        @Override
        public void afterPropertiesSet() {
            try {
                super.afterPropertiesSet();
            } catch(ServletException e) {
                // convert to RuntimeException for passivity on afterPropertiesSet signature
                throw new RuntimeException(e);
            }
            Assert.notNull(authenticationManager, "An AuthenticationManager must be set");
        }

        /**
         * Try to authenticate a pre-authenticated user with Spring Security if the user has not yet been authenticated.
         */
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            if (logger.isDebugEnabled()) {
                //logger.debug("Checking secure context token: " + SecurityContextHolder.getContext().getAuthentication());
            }

            if (requiresAuthentication((HttpServletRequest) request)) {
                doAuthenticate((HttpServletRequest) request, (HttpServletResponse) response);
            }

            chain.doFilter(request, response);
        }

        /**
         * Do the actual authentication for a pre-authenticated user.
         */
        private void doAuthenticate(HttpServletRequest request, HttpServletResponse response) {
            Authentication authResult;

            Object principal = getPreAuthenticatedPrincipal(request);
            Object credentials = getPreAuthenticatedCredentials(request);

            if (principal == null) {
                if (logger.isDebugEnabled()) {
                    //logger.debug("No pre-authenticated principal found in request");
                }

                return;
            }

            if (logger.isDebugEnabled()) {
                //logger.debug("preAuthenticatedPrincipal = " + principal + ", trying to authenticate");
            }

            try {
                PreAuthenticatedAuthenticationToken authRequest = new PreAuthenticatedAuthenticationToken(principal, credentials);
                authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
                authResult = authenticationManager.authenticate(authRequest);
                successfulAuthentication(request, response, authResult);
            } catch (AuthenticationException failed) {
                unsuccessfulAuthentication(request, response, failed);

                if (!continueFilterChainOnUnsuccessfulAuthentication) {
                    throw failed;
                }
            }
        }

        private boolean requiresAuthentication(HttpServletRequest request) {
            Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

            if (currentUser == null) {
                return true;
            }

            if (!checkForPrincipalChanges) {
                return false;
            }

            Object principal = getPreAuthenticatedPrincipal(request);

            if (currentUser.getName().equals(principal)) {
                return false;
            }

            //logger.debug("Pre-authenticated principal has changed to " + principal + " and will be reauthenticated");

            if (invalidateSessionOnPrincipalChange) {
                SecurityContextHolder.clearContext();

                HttpSession session = request.getSession(false);

                if (session != null) {
                    //logger.debug("Invalidating existing session");
                    session.invalidate();
                    request.getSession();
                }
            }

            return true;
        }

        /**
         * Puts the <code>Authentication</code> instance returned by the
         * authentication manager into the secure context.
         */
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
            SecurityContextHolder.getContext().setAuthentication(authResult);
            // Fire event
            if (this.eventPublisher != null) {
                eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
            }
        }

        /**
         * Ensures the authentication object in the secure context is set to null when authentication fails.
         * <p>
         * Caches the failure exception as a request attribute
         */
        protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
            SecurityContextHolder.clearContext();

            if (logger.isDebugEnabled()) {
                //logger.debug("Cleared security context due to exception", failed);
            }
            request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, failed);
        }

        /**
         * @param anApplicationEventPublisher
         *            The ApplicationEventPublisher to use
         */
        public void setApplicationEventPublisher(ApplicationEventPublisher anApplicationEventPublisher) {
            this.eventPublisher = anApplicationEventPublisher;
        }

        /**
         * @param authenticationDetailsSource
         *            The AuthenticationDetailsSource to use
         */
        public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest,?> authenticationDetailsSource) {
            Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
            this.authenticationDetailsSource = authenticationDetailsSource;
        }

        protected AuthenticationDetailsSource<HttpServletRequest, ?> getAuthenticationDetailsSource() {
            return authenticationDetailsSource;
        }

        /**
         * @param authenticationManager
         *            The AuthenticationManager to use
         */
        public void setAuthenticationManager(AuthenticationManager authenticationManager) {
            this.authenticationManager = authenticationManager;
        }

        /**
         * If set to {@code true}, any {@code AuthenticationException} raised by the {@code AuthenticationManager} will be
         * swallowed, and the request will be allowed to proceed, potentially using alternative authentication mechanisms.
         * If {@code false} (the default), authentication failure will result in an immediate exception.
         *
         * @param shouldContinue set to {@code true} to allow the request to proceed after a failed authentication.
         */
        public void setContinueFilterChainOnUnsuccessfulAuthentication(boolean shouldContinue) {
            continueFilterChainOnUnsuccessfulAuthentication = shouldContinue;
        }

        /**
         * If set, the pre-authenticated principal will be checked on each request and compared
         * against the name of the current <tt>Authentication</tt> object. If a change is detected,
         * the user will be reauthenticated.
         *
         * @param checkForPrincipalChanges  i dont know
         */
        public void setCheckForPrincipalChanges(boolean checkForPrincipalChanges) {
            this.checkForPrincipalChanges = checkForPrincipalChanges;
        }

        /**
         * If <tt>checkForPrincipalChanges</tt> is set, and a change of principal is detected, determines whether
         * any existing session should be invalidated before proceeding to authenticate the new principal.
         *
         * @param invalidateSessionOnPrincipalChange <tt>false</tt> to retain the existing session. Defaults to <tt>true</tt>.
         */
        public void setInvalidateSessionOnPrincipalChange(boolean invalidateSessionOnPrincipalChange) {
            this.invalidateSessionOnPrincipalChange = invalidateSessionOnPrincipalChange;
        }
}