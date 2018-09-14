package server.security;


import org.hibernate.Session;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.dao.UserDAO;
import server.model.User;

import javax.inject.Inject;

@Service("customUserDetailsService")
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    @Inject
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Session session = userDAO.getSessionFactory().openSession();
        User user = userDAO.getUserByName(username, session);
        if (user != null)
            user.getAuthorities();
        session.close();

        if (user == null) {
            throw new UsernameNotFoundException("User: " + username + " not found");
        }
        return user;
    }
}
