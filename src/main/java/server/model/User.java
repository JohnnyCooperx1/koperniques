package server.model;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name= "oc_user")
public class User implements Serializable, UserDetails {

    //
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_oc")
//    @SequenceGenerator(name = "seq_oc", sequenceName = "seq_oc", allocationSize=1)
//    @Column(name = "user_id")
//    protected Integer id;
//
    @Id
    @Column(name = "username", nullable = false)
    protected String username;

    @Column(name = "password")
    protected String password;

    @Column(name = "enabled", nullable = false)
    protected boolean enabled;

    @Column(name = "name")
    protected String fullname;

    @Column(name = "forcechange", nullable = false)
    protected boolean forcechange;

    @Column(name = "expired")
    protected Timestamp expired;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_create")
    private Date dateCreate;


    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }


    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public boolean isForcechange() {
        return forcechange;
    }

    public void setForcechange(boolean forcechange) {
        this.forcechange = forcechange;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getExpired() {
        return expired;
    }

    public void setExpired(Timestamp expired) {
        this.expired = expired;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Set<String> getUserRolesList() {
        return userRolesList;
    }

    public void setUserRolesList(Set<String> userRolesList) {
        this.userRolesList = userRolesList;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    protected Set<UserRole> userRoles = new HashSet<UserRole>(0);

    @Transient
    protected Set<String> userRolesList = new HashSet<>(0);

    public boolean equalPassword(String password, PasswordEncoder passwordEncoder) {
        return password != null && passwordEncoder.matches(password, this.password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        for (UserRole role : getUserRoles()) {
            authList.add(new SimpleGrantedAuthority(role.getAuthority()));
        }
        return authList;
    }

    public boolean hasAnyUserRole(String[] roles) {
        if (userRoles != null) {
            for (UserRole ur : userRoles) {
                for (String role : roles) {
                    if (ur.getAuthority().equalsIgnoreCase(role))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

//    @Override
//    public void initObject(Set<Class> classesToInit) {
//        if (classesToInit == null) return;
//        if (userRoles != null && classesToInit.contains(UserRole.class)) {
//            Hibernate.initialize(userRoles);
//            classesToInit.remove(UserRole.class);
//        }
//    }

}