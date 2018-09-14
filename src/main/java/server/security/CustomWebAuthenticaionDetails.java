package server.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: it
 * Date: 19.08.13
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public class CustomWebAuthenticaionDetails extends WebAuthenticationDetails {

    protected final Log logger = LogFactory.getLog(getClass());

    private final String provider;

    private final String otp;

    private static final String PROVIDER_PARAM_KEY  = "j_provider";
    private static final String OTP_PARAM_KEY  = "j_otp";

    public CustomWebAuthenticaionDetails(HttpServletRequest request) {
        super(request);    //To change body of overridden methods use File | Settings | File Templates.
        this.provider = request.getParameter(PROVIDER_PARAM_KEY);
        this.otp = request.getParameter(OTP_PARAM_KEY);
    }

    public String getProvider() {
        return provider;
    }

    public String getOtp() {
        return otp;
    }
}
