package woofworks.repository;

import org.hibernate.Query;
import woofworks.model.User;

/**
 * Created by Tim on 2016-11-12.
 */
public class UserRepository extends BaseRepository {

    /**
     * Returns the specified user based on their username
     *
     * @param username
     * @return
     */
    public User getUserByUserName(String username) {
        String hql = "from User u where lower(u.login) = lower(:username)";

        Query query = createQuery(hql);
        query.setParameter("username", username);

        return (User) query.uniqueResult();
    }
}
