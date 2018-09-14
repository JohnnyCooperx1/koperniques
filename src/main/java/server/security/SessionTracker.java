package server.security;


import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import server.model.User;

import javax.servlet.http.*;
import java.util.HashMap;

/**
 * Fake headers request object. Adds a request header
 * with the name "username". The value of this request header
 * will be taken from a cookie (also with the name, "username").
 *
 * @author Jee Vang
 *
 */
public class SessionTracker implements HttpSessionListener {

    protected static HashMap<String,HttpSession> sessions = new HashMap<>();

    //@Inject
    //protected Logger log;

    public static HttpSession getSessionById(String id){
        return sessions.get(id);
    }

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
        try {
            String sessionId = arg0.getSession().getId();
            sessions.put(sessionId, arg0.getSession());
        }
        catch (Exception ignored) {}
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {
        try {
            try{
                User user = ((User)((SecurityContext)arg0.getSession().getAttribute("SPRING_SECURITY_CONTEXT")).getAuthentication().getPrincipal());
//                Logger log = (Logger)WebApplicationContextUtils.getWebApplicationContext(arg0.getSession().getServletContext()).getBean("Logger");
//                log.addEvent(Logger.LogActions.LOGOUT, Logger.LogObjects.USER, user, "success");
            }
            catch (Exception ignored) {}

            sessions.remove(arg0.getSession().getId());
        }
        catch (Exception ignored) {}
    }
}