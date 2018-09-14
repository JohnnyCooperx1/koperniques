package server.security;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import server.Constants;
import server.model.User;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;


@Named
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    protected final Log logger = LogFactory.getLog(getClass());

//    @Inject
//    private Logger log;

    public CustomAuthenticationSuccessHandler() {}

    protected AuthenticationSuccessHandler target = new SavedRequestAwareAuthenticationSuccessHandler();

    public CustomAuthenticationSuccessHandler(String targetUrl) {
        ((SavedRequestAwareAuthenticationSuccessHandler) target).setAlwaysUseDefaultTargetUrl(true);
        ((SavedRequestAwareAuthenticationSuccessHandler) target).setDefaultTargetUrl(targetUrl);
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response, final Authentication authentication)
            throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof User)) {
            throw new ServletException("User details is not User, invalid authentication");
        }
        User user = (User)authentication.getPrincipal();
        if (user == null )
            throw new ServletException("User or domain is null, invalid authentication");

        Cookie[] cookies = request.getCookies();
        if (cookies!=null) {
            int fl=0;
            for(Cookie c : cookies) {
                fl++;
//                if (c.getName().equalsIgnoreCase(Constants.COOKIE_DOMAIN)) {
//                    c.setValue(user.getDomain().getId().toString());
//                    c.setPath("/");
//                    c.setMaxAge(60*60*24*365);
//                    response.addCookie(c);
//                }

//                if ((fl == cookies.length) && (!c.getName().equalsIgnoreCase(Constants.COOKIE_DOMAIN))) {
//                    Cookie cookie = new Cookie(Constants.COOKIE_DOMAIN, user.getDomain().getId().toString());
//
//                    cookie.setPath("/");
//                    cookie.setMaxAge(60*60*24*365);
//                    response.addCookie(cookie);
//                }
            }
        } else {
//            Cookie cookie = new Cookie(Constants.COOKIE_DOMAIN,user.getDomain().getId().toString());
//            cookie.setPath("/");
//            cookie.setValue(user.getDomain().getId().toString());
//            cookie.setMaxAge(60*60*24*365);
//            response.addCookie(cookie);
        }

        if (user.isForcechange()) {
            response.sendRedirect(request.getContextPath() + "/manager/user/changePassword.form");
        } else
            proceed(request, response, authentication);
    }

    public void proceed(HttpServletRequest request,
                        HttpServletResponse response,
                        Authentication auth) throws IOException, ServletException {
        target.onAuthenticationSuccess(request, response, auth);
    }
}
