package calories.tracker.app;

import calories.tracker.app.dto.MealDTO;
import calories.tracker.app.model.Meal;
import calories.tracker.app.model.SearchResult;
import calories.tracker.app.services.MealService;
import calories.tracker.config.root.RootContextConfig;
import calories.tracker.config.root.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static calories.tracker.app.TestUtils.date;
import static calories.tracker.app.TestUtils.time;
import static calories.tracker.app.dto.MealDTO.mapFromMealEntity;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes={TestConfiguration.class, RootContextConfig.class})
public class MealServiceTest {

    @Autowired
    private MealService mealService;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testFindMealsByDate() {
        SearchResult<Meal> result = mealService.findMeals(UserServiceTest.USERNAME, date(2015,1,1), date(2015,1,2), null ,null, 1);
        assertTrue("results not expected, total " + result.getResultsCount(), result.getResultsCount() == 4);
    }

    @Test
    public void testFindMealsByDateTime() {
        SearchResult<Meal> result = mealService.findMeals(UserServiceTest.USERNAME, date(2015,1,1), date(2015,1,2),
                time("11:00") ,time("14:00"), 1);
        assertTrue("results not expected, total " + result.getResultsCount(), result.getResultsCount() == 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromDateAfterToDate() {
        mealService.findMeals(UserServiceTest.USERNAME, date(2015,1,2), date(2015,1,1), null ,null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromTimeAfterToTime() {
        mealService.findMeals(UserServiceTest.USERNAME, date(2015,1,2), date(2015,1,1), time("12:00") ,time("11:00"), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromDateNull() {
        mealService.findMeals(UserServiceTest.USERNAME, null, date(2015,1,1), time("12:00") ,time("11:00"), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toDateNull() {
        mealService.findMeals(UserServiceTest.USERNAME, date(2015,1,1), null, time("12:00") ,time("11:00"), 1);
    }

    @Test
    public void deleteMeals() {
        mealService.deleteMeals(Arrays.asList(15L));
        Meal meal = em.find(Meal.class, 15L);
        assertNull("meal was not deleted" , meal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteMealsNull() {
        mealService.deleteMeals(null);
    }

    @Test
    public void saveMeals() {
        MealDTO meal1 = mapFromMealEntity(em.find(Meal.class, 1L));
        MealDTO meal2 = mapFromMealEntity(em.find(Meal.class, 2L));

        meal1.setDescription("test1");
        meal2.setCalories(10L);

        List<MealDTO> meals = Arrays.asList(meal1, meal2);

        mealService.saveMeals(UserServiceTest.USERNAME, meals);


        Meal m1 = em.find(Meal.class, 1L);
        assertTrue("description not as expected: " + m1.getDescription(), "test1".equals(m1.getDescription()));

        Meal m2 = em.find(Meal.class, 2L);
        assertTrue("calories not as expected: " + m2.getCalories(), m2.getCalories() == 10L);
    }


}
