package calories.tracker.app.dao;


import calories.tracker.app.model.Meal;
import calories.tracker.app.model.User;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Repository class for the Meal entity
 *
 */
@Repository
public class MealRepository {

    private static final Logger LOGGER = Logger.getLogger(MealRepository.class);

    @PersistenceContext
    EntityManager em;

    /**
     *
     * counts the matching meals, given the bellow criteria
     *
     * @param username - the currently logged in username
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @return -  a list of matching meals, or an empty collection if no match found
     */
    public Long countMealsByDateTime(String username, Date fromDate, Date toDate, Time fromTime, Time toTime) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // query for counting the total results
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Meal> countRoot = cq.from(Meal.class);
        cq.select((cb.count(countRoot)));
        cq.where(getCommonWhereCondition(cb, username, countRoot, fromDate, toDate, fromTime, toTime));
        Long resultsCount = em.createQuery(cq).getSingleResult();

        LOGGER.info("Found " + resultsCount + " results.");

        return resultsCount;
    }

    /**
     *
     * finds a list of meals, given the bellow criteria
     *
     * @param username - the currently logged in username
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @return -  a list of matching meals, or an empty collection if no match found
     */
    public List<Meal> findMealsByDateTime(String username, Date fromDate, Date toDate,
                                          Time fromTime, Time toTime, int pageNumber) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // the actual search query that returns one page of results
        CriteriaQuery<Meal> searchQuery = cb.createQuery(Meal.class);
        Root<Meal> searchRoot = searchQuery.from(Meal.class);
        searchQuery.select(searchRoot);
        searchQuery.where(getCommonWhereCondition(cb, username, searchRoot, fromDate, toDate, fromTime, toTime));

        List<Order> orderList = new ArrayList();
        orderList.add(cb.desc(searchRoot.get("date")));
        orderList.add(cb.asc(searchRoot.get("time")));
        searchQuery.orderBy(orderList);

        TypedQuery<Meal> filterQuery = em.createQuery(searchQuery)
                .setFirstResult((pageNumber - 1) * 10)
                .setMaxResults(10);

        return filterQuery.getResultList();
    }

    /**
     * Delete a meal, given its identifier
     *
     * @param deletedMealId - the id of the meal to be deleted
     */
    public void delete(Long deletedMealId) {
        Meal delete = em.find(Meal.class, deletedMealId);
        em.remove(delete);
    }

    /**
     *
     * finds a meal given its id
     *
     */
    public Meal findMealById(Long id) {
        return em.find(Meal.class, id);
    }

    /**
     *
     * save changes made to a meal, or create the meal if its a new meal.
     *
     */
    public Meal save(Meal meal) {
        return em.merge(meal);
    }


    private Predicate[] getCommonWhereCondition(CriteriaBuilder cb, String username, Root<Meal> searchRoot, Date fromDate, Date toDate,
                                                Time fromTime, Time toTime) {

        List<Predicate> predicates = new ArrayList<>();
        Join<Meal, User> user = searchRoot.join("user");

        predicates.add(cb.equal(user.<String>get("username"), username));
        predicates.add(cb.greaterThanOrEqualTo(searchRoot.<Date>get("date"), fromDate));

        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(searchRoot.<Date>get("date"), toDate));
        }

        if (fromTime != null) {
            predicates.add(cb.greaterThanOrEqualTo(searchRoot.<Date>get("time"), fromTime));
        }

        if (toTime != null) {
            predicates.add(cb.lessThanOrEqualTo(searchRoot.<Date>get("time"), toTime));
        }

        return predicates.toArray(new Predicate[]{});
    }

}
