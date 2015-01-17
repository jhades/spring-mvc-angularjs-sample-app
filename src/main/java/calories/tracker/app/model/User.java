package calories.tracker.app.model;


import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * The User JPA entity.
 *
 */
@Entity
@Table(name = "USERS")
@NamedQueries({
        @NamedQuery(
                name = User.FIND_BY_USERNAME,
                query = "select u from User u where username = :username"
        ),
        @NamedQuery(
                name = User.COUNT_TODAYS_CALORIES,
                query = "select sum(m.calories) from Meal m where m.user.username = :username and m.date = CURRENT_DATE"
        )
})
public class User extends AbstractEntity {

    public static final String FIND_BY_USERNAME = "user.findByUserName";
    public static final String COUNT_TODAYS_CALORIES = "user.todaysCalories";

    private String username;
    private String passwordDigest;
    private String email;
    private Long maxCaloriesPerDay;


    public User() {

    }

    public User(String username, String passwordDigest, String email, Long maxCaloriesPerDay) {
        this.username = username;
        this.passwordDigest = passwordDigest;
        this.email = email;
        this.maxCaloriesPerDay = maxCaloriesPerDay;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordDigest() {
        return passwordDigest;
    }

    public void setPasswordDigest(String passwordDigest) {
        this.passwordDigest = passwordDigest;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getMaxCaloriesPerDay() {
        return maxCaloriesPerDay;
    }

    public void setMaxCaloriesPerDay(Long maxCaloriesPerDay) {
        this.maxCaloriesPerDay = maxCaloriesPerDay;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", maxCaloriesPerDay=" + maxCaloriesPerDay +
                '}';
    }
}
