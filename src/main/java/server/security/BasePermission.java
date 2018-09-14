package server.security;


import org.springframework.security.core.Authentication;
import server.common.Utils;

public class BasePermission {

    protected boolean hasRole(Authentication auth, String role) {
        return Utils.hasRole(auth,role);
    }

    protected boolean hasAnyRole(Authentication auth,String[] roles) {
        return Utils.hasAnyRole(auth,roles);
    }

}
