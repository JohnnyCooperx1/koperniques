package server.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ezybarev on 14.11.2017.
 */
@Entity
@Table(name = "oc_user_role")
public class UserRole implements Serializable {
    private static final long serialVersionUID = -4059810600435364181L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_oc")
    @SequenceGenerator(name = "seq_oc", sequenceName = "seq_oc", allocationSize=1)
    @Column(name = "user_role_id")
    protected Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    protected User user;

    @Column(name = "authority")
    protected String authority;

    public UserRole() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public UserRole(User user, String authority) {
        setUser(user);
        setAuthority(authority);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}