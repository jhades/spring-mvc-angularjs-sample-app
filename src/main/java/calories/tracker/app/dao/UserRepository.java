package calories.tracker.app.dao;


import calories.tracker.app.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * Repository class for the User entity
 *
 */
@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * finds a user given its username
     *
     * @param username - the username of the searched user
     * @return  a matching user, or null if no user found.
     */
    public User findUserByUsername(String username) {

        List<User> users = em.createNamedQuery(User.FIND_BY_USERNAME, User.class)
                .setParameter("username", username)
                .getResultList();

        return users.size() == 1 ? users.get(0) : null;
    }

    /**
     *
     * find the total calories that a given user has consumed so far in ongoing day
     *
     * @param username
     * @return the total number of calories for the user for today
     */
    public Long findTodaysCaloriesForUser(String username) {
        return (Long) em.createNamedQuery(User.COUNT_TODAYS_CALORIES).setParameter("username", username).getSingleResult();
    }

    /**
     *
     * save changes made to a user, or insert it if its new
     *
     * @param user
     */
    public void save(User user) {
        em.merge(user);
    }

    /**
     * checks if a username is still available in the database
     *
     * @param username - the username to be checked for availability
     * @return true if the username is still available
     */
    public boolean isUsernameAvailable(String username) {

        List<User> users = em.createNamedQuery(User.FIND_BY_USERNAME, User.class)
                .setParameter("username", username)
                .getResultList();

        return users.isEmpty();
    }
}
