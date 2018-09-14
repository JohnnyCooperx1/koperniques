package server.dao;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import server.model.User;

@Repository("userDAO")
public class UserDAO extends BaseDAO {


    public User getUserByName(String name, Session session){
        Query query = session.createQuery("from User where  username like :name");
        query.setString("name", name);
        User result= (User)query.uniqueResult();
        return result;

    }

}