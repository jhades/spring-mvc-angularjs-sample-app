package calories.tracker.app.dto;

/**
 *
 * JSON-serializable DTO containing user data
 *
 */
public class UserInfoDTO {

    private String userName;
    private Long maxCaloriesPerDay;
    private Long todaysCalories;

    public UserInfoDTO(String userName, Long maxCaloriesPerDay, Long todaysCalories) {
        this.userName = userName;
        this.maxCaloriesPerDay = maxCaloriesPerDay;
        this.todaysCalories = todaysCalories;
    }

    public Long getMaxCaloriesPerDay() {
        return maxCaloriesPerDay;
    }

    public void setMaxCaloriesPerDay(Long maxCaloriesPerDay) {
        this.maxCaloriesPerDay = maxCaloriesPerDay;
    }

    public Long getTodaysCalories() {
        return todaysCalories;
    }

    public void setTodaysCalories(Long todaysCalories) {
        this.todaysCalories = todaysCalories;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
