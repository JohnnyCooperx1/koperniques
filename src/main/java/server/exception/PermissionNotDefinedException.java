package server.exception;

/**
 * Created by ezybarev on 14.11.2017.
 */
public class PermissionNotDefinedException extends CommonException {
    public PermissionNotDefinedException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PermissionNotDefinedException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PermissionNotDefinedException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PermissionNotDefinedException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected PermissionNotDefinedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);    //To change body of overridden methods use File | Settings | File Templates.
    }
}