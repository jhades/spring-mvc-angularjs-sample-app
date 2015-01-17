package calories.tracker.app.controllers;


import calories.tracker.app.dto.MealDTO;
import calories.tracker.app.dto.MealsDTO;
import calories.tracker.app.model.Meal;
import calories.tracker.app.model.SearchResult;
import calories.tracker.app.services.MealService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *  REST service for meals - allows to update, create and search for meals for the currently logged in user.
 *
 */
@Controller
@RequestMapping("meal")
public class MealController {

    Logger LOGGER = Logger.getLogger(MealController.class);

    private static final long DAY_IN_MS = 1000 * 60 * 60 * 24;


    @Autowired
    private MealService mealService;

    /**
     * search Meals for the current user by date and time ranges.
     *
     *
     * @param principal  - the current logged in user
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @param pageNumber - the page number (each page has 10 entries)
     * @return - @see MealsDTO with the current page, total pages and the list of meals
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public MealsDTO searchMealsByDate(
            Principal principal,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") Date fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") Date toDate,
            @RequestParam(value = "fromTime", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date fromTime,
            @RequestParam(value = "toTime", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date toTime,
            @RequestParam(value = "pageNumber") Integer pageNumber) {

        if (fromDate == null && toDate == null) {
            fromDate = new Date(System.currentTimeMillis() - (3 * DAY_IN_MS));
            toDate = new Date();
        }

        SearchResult<Meal> result = mealService.findMeals(
                principal.getName(),
                fromDate,
                toDate,
                fromTime != null ? new Time(fromTime.getTime()) : null,
                toTime != null ? new Time(toTime.getTime()) : null,
                pageNumber);

        Long resultsCount = result.getResultsCount();
        Long totalPages = resultsCount / 10;

        if (resultsCount % 10 > 0) {
            totalPages++;
        }

        return new MealsDTO(pageNumber, totalPages, MealDTO.mapFromMealsEntities(result.getResult()));
    }

    /**
     *
     * saves a list of meals - they be either new or existing
     *
     * @param principal - the current logged in user
     * @param meals - the list of meals to save
     * @return - an updated version of the saved meals
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST)
    public List<MealDTO> saveMeals(Principal principal, @RequestBody List<MealDTO> meals) {

        List<Meal> savedMeals = mealService.saveMeals(principal.getName(), meals);

        return savedMeals.stream()
                .map(MealDTO::mapFromMealEntity)
                .collect(Collectors.toList());
    }

    /**
     *
     * deletes a list of meals
     *
     * @param deletedMealIds - the ids of the meals to be deleted
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteMeals(@RequestBody List<Long> deletedMealIds) {
        mealService.deleteMeals(deletedMealIds);
    }

    /**
     *
     * error handler for backend errors - a 400 status code will be sent back, and the body
     * of the message contains the exception text.
     *
     * @param exc - the exception caught
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> errorHandler(Exception exc) {
        LOGGER.error(exc.getMessage(), exc);
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
