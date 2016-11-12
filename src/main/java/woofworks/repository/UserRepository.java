package woofworks.repository;

import org.hibernate.Query;
import woofworks.model.SessionToken;
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

    /**
     * Returns a session token object based on the token
     *
     * @param token
     * @return
     */
    public SessionToken getSessionToken(String token) {
        String hql = "from SessionToken where token = :token";

        Query query = createQuery(hql);
        query.setParameter("token", token);

        return (SessionToken) query.uniqueResult();
    }
}
