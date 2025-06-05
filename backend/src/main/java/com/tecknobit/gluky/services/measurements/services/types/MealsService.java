package com.tecknobit.gluky.services.measurements.services.types;

import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.measurements.repositories.types.MealsRepository;
import com.tecknobit.glukycore.enums.MeasurementType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;

/**
 * The {@code MealsService} class is useful to manage all the {@link Meal} database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see GlycemicMeasurementsService
 */
@Service
public class MealsService extends GlycemicMeasurementsService {

    /**
     * {@code mealsRepository} the instance for the meals repository
     */
    private final MealsRepository mealsRepository;

    /**
     * Constructor to init the service
     *
     * @param mealsRepository The instance for the meals repository
     */
    @Autowired
    public MealsService(MealsRepository mealsRepository) {
        this.mealsRepository = mealsRepository;
    }

    /**
     * Method used to register all the meals measurements for the specified day
     *
     * @param day The day owner of the meals
     * @return the meals just registered as {@link ArrayList} of {@link Meal}
     */
    public ArrayList<Meal> registerMealsForDay(DailyMeasurements day) {
        ArrayList<Meal> meals = new ArrayList<>();
        for (MeasurementType mealType : MeasurementType.Companion.meals())
            meals.add(new Meal(generateIdentifier(), mealType, day));
        mealsRepository.saveAll(meals);
        return meals;
    }

    /**
     * Method used to fill a meal
     *
     * @param meal The meal to fill
     * @param glycemia The value of the glycemia
     * @param postPrandialGlycemia The value of the post-prandial glycemia
     * @param insulinUnits The administered insulin units
     * @param content The content of the meal "raw" formatted as json
     */
    public void fillMeal(GlycemicMeasurementItem meal, String glycemia, String postPrandialGlycemia,
                         int insulinUnits, JSONObject content) {
        String mealId = meal.getId();
        int glycemiaValue = convertGlycemicValue(glycemia);
        int postPrandialGlycemiaValue = convertGlycemicValue(postPrandialGlycemia);
        if (!meal.isFilled()) {
            long annotationDate = System.currentTimeMillis();
            mealsRepository.fillMeal(
                    annotationDate,
                    glycemiaValue,
                    postPrandialGlycemiaValue,
                    insulinUnits,
                    content.toString(),
                    mealId
            );
        } else {
            mealsRepository.fillMeal(
                    glycemiaValue,
                    postPrandialGlycemiaValue,
                    insulinUnits,
                    content.toString(),
                    mealId
            );
        }
    }

}
