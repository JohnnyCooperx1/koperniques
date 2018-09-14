package server.security;


import org.springframework.security.core.Authentication;

/**
 * Created with IntelliJ IDEA.
 * User: it
 * Date: 08.08.13
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
public class ReadPermission extends BasePermission implements Permission {
    @Override
    public boolean isAllowed(Authentication auth, Object targetObject) {
//        if (targetObject instanceof BaseObject) {
//            BaseObject baseObject = (BaseObject)targetObject;
//            if (baseObject.getSecurity() != null) {
//                //String security = baseObject.getSecurity();
////                if (isAdminOrSuperAdmin(auth)) {
////                    return true;
////                }
////                else
////                {
////                    return hasRole(auth,security);
////                }
//                return true;
//            }
//            return true;
//        }
//        else
            return true;
    }
}
