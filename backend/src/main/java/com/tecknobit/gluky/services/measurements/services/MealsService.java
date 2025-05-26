package com.tecknobit.gluky.services.measurements.services;

import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.measurements.repositories.MealsRepository;
import com.tecknobit.glukycore.enums.MeasurementType;
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

}
