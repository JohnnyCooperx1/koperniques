package server.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.dao.UserDAO;
import server.model.User;

import javax.inject.Inject;

@Service("preAuthenticationUserDetailsService")
@Transactional(readOnly = true)
public class PreAuthenticationUserDetailsService implements UserDetailsService {

    @Inject
    protected UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.getUserByName(username, null);
        if (user == null) {
            throw new UsernameNotFoundException("User: " + username + " not found");
        }
        return user;
    }
}
