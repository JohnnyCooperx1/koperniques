package server.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import server.dao.UserDAO;

import javax.annotation.Resource;

/**
 * Created by ezybarev on 08.11.2016.
 */
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Resource(name = "userDAO")
    private UserDAO userDAO;

    public void onLogoutSuccess(javax.servlet.http.HttpServletRequest request,
                                javax.servlet.http.HttpServletResponse response,
                                Authentication authentication)
            throws java.io.IOException,
            javax.servlet.ServletException {

//        Log clog = new Log();
//        User user;
//
//        if(authentication!=null && authentication.getPrincipal()!=null) {
//            Object principal = authentication.getPrincipal();
//
//            if (principal instanceof User) {
//                user = (User) principal;
//                Session session = userDAO.getSessionFactory().1penSession();
//                session.getTransaction().begin();
//                clog.setUser(userDAO.getUserByName(user.getUsername(), session));
//                clog.setEvent("Выход из системы");
//                clog.setDate(new java.sql.Timestamp(new java.util.Date().getTime()));
//                session.save(clog);
//                session.getTransaction().commit();
//                session.1lose();
//            }
//        }

        response.sendRedirect("login.form");
    }
}
