package server.security;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import server.exception.PermissionNotDefinedException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CustomPermissionEvaluator implements PermissionEvaluator {

    protected final Log logger = LogFactory.getLog(getClass());

    private Map<String, Permission> permissionNameToPermissionMap = new HashMap<String, Permission>();

    public CustomPermissionEvaluator() {
        super();
    }

    public CustomPermissionEvaluator(Map<String, Permission> permissionNameToPermissionMap) {
        this.permissionNameToPermissionMap = permissionNameToPermissionMap;
    }

    public boolean hasPermission(Authentication authentication, Object targetObject, Object permission) {
        boolean hasPermission = false;
        if (canHandle(authentication, targetObject, permission)) {
            try {
                hasPermission = checkPermission(authentication, targetObject, (String) permission);
            } catch (PermissionNotDefinedException e) {
                //logger.error(e);
                return false;
            }
        }
        return hasPermission;
    }

    private boolean canHandle(Authentication authentication, Object targetObject, Object permission) {
        return targetObject != null && authentication != null && permission instanceof String;
    }

    private boolean checkPermission(Authentication authentication, Object targetObject, String permissionKey) throws PermissionNotDefinedException {
        verifyPermissionIsDefined(permissionKey);
        Permission permission = permissionNameToPermissionMap.get(permissionKey);
        return permission.isAllowed(authentication, targetObject);
    }

    private void verifyPermissionIsDefined(String permissionKey) throws PermissionNotDefinedException {
        if (!permissionNameToPermissionMap.containsKey(permissionKey)) {
            throw new PermissionNotDefinedException("No permission with key " + permissionKey + " is defined in " + this.getClass().toString());
        }
    }

    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        //logger.error("Id and Class permissions are not supperted by " + this.getClass().toString());
        return false;
    }
}
