package server.security;

import org.springframework.security.authentication.AuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

public class CustomAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest,CustomWebAuthenticaionDetails> {
    @Override
    public CustomWebAuthenticaionDetails buildDetails(HttpServletRequest request) {
        return new CustomWebAuthenticaionDetails(request);
    }
}
