package com.tecknobit.gluky.services.measurements.services;

import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.measurements.repositories.MealsRepository;
import com.tecknobit.glukycore.enums.MeasurementType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;

@Service
public class MealsService {

    private final MealsRepository mealsRepository;

    @Autowired
    public MealsService(MealsRepository mealsRepository) {
        this.mealsRepository = mealsRepository;
    }

    public ArrayList<Meal> registerMealsForDay(DailyMeasurements day) {
        ArrayList<Meal> meals = new ArrayList<>();
        for (MeasurementType mealType : MeasurementType.Companion.meals())
            meals.add(new Meal(generateIdentifier(), mealType, day));
        mealsRepository.saveAll(meals);
        return meals;
    }

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

    @Returner
    private int convertGlycemicValue(String glycemicValue) {
        if (glycemicValue == null || glycemicValue.isBlank())
            return -1;
        return Integer.parseInt(glycemicValue);
    }

}
