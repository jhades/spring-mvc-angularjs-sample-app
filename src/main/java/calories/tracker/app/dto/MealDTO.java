package calories.tracker.app.dto;


import calories.tracker.app.dto.serialization.CustomTimeDeserializer;
import calories.tracker.app.dto.serialization.CustomTimeSerializer;
import calories.tracker.app.model.Meal;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * JSON serializable DTO containing Meal data
 *
 */
public class MealDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "CET")
    private Date date;

    @JsonSerialize(using = CustomTimeSerializer.class)
    @JsonDeserialize(using = CustomTimeDeserializer.class)
    private Time time;

    private String description;
    private Long calories;

    public MealDTO() {
    }

    public MealDTO(Long id, Date date, Time time, String description, Long calories) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.description = description;
        this.calories = calories;
    }

    public static MealDTO mapFromMealEntity(Meal meal) {
        return new MealDTO(meal.getId(), meal.getDate(), meal.getTime(),
                meal.getDescription(), meal.getCalories());
    }

    public static List<MealDTO> mapFromMealsEntities(List<Meal> meals) {
        return meals.stream().map((meal) -> mapFromMealEntity(meal)).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCalories() {
        return calories;
    }

    public void setCalories(Long calories) {
        this.calories = calories;
    }

}
