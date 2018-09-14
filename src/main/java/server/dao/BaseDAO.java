package server.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.core.GenericTypeResolver;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Created by ezybarev on 14.11.2017.
 */
@Repository
public class BaseDAO<E> {

    @Inject
    protected SessionFactory sessionFactory;

    @Inject
    protected NamedParameterJdbcTemplate jdbcTemplate;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private final Class<E> genericType;

    public BaseDAO() {
        this.genericType = (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(),BaseDAO.class);
    }

    public E saveOrUpdate(E e) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.getTransaction();
        tx.begin();
        try {
            session.saveOrUpdate(e);
            tx.commit();
        } catch (Exception ex) {
            tx.rollback();
        } finally {
            if (session.isOpen())
                session.close();
        }
        return e;
    }

    public E saveOrUpdate(E e, Session session) {
        session.saveOrUpdate(e);
        return e;
    }

//    public E getById(Session session, Serializable id) {
//        return session.get(genericType, id);
//    }
}
