package com.tecknobit.gluky.services.measurements.services;

import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.measurements.repositories.MeasurementsRepository;
import com.tecknobit.gluky.services.measurements.services.types.BasalInsulinService;
import com.tecknobit.gluky.services.measurements.services.types.MealsService;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.glukycore.enums.MeasurementType;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;
import static com.tecknobit.gluky.services.shared.controllers.DefaultGlukyController.dayFormatter;

@Service
public class MeasurementsService {

    private final MeasurementsRepository measurementsRepository;

    private final MealsService mealsService;

    private final BasalInsulinService basalInsulinService;

    @Autowired
    public MeasurementsService(MeasurementsRepository measurementsRepository, MealsService mealsService,
                               BasalInsulinService basalInsulinService) {
        this.measurementsRepository = measurementsRepository;
        this.mealsService = mealsService;
        this.basalInsulinService = basalInsulinService;
    }

    public DailyMeasurements getDailyMeasurements(String userId, String targetDay) {
        long creationDate = normalizeTargetDay(targetDay);
        return measurementsRepository.getDailyMeasurements(userId, creationDate);
    }

    @Transactional
    public DailyMeasurements fillDay(GlukyUser user, String targetDay) {
        long creationDate = normalizeTargetDay(targetDay);
        String measurementsId = generateIdentifier();
        DailyMeasurements dailyMeasurements = new DailyMeasurements(measurementsId, creationDate, user);
        measurementsRepository.save(dailyMeasurements);
        ArrayList<Meal> meals = mealsService.registerMealsForDay(dailyMeasurements);
        dailyMeasurements.attachMeals(meals);
        BasalInsulin basalInsulin = basalInsulinService.registerBasalInsulinForDay(dailyMeasurements);
        dailyMeasurements.attachBasalInsulin(basalInsulin);
        measurementsRepository.attachMeasurements(
                dailyMeasurements.getBreakfast().getId(),
                dailyMeasurements.getMorningSnack().getId(),
                dailyMeasurements.getLunch().getId(),
                dailyMeasurements.getAfternoonSnack().getId(),
                dailyMeasurements.getDinner().getId(),
                basalInsulin.getId(),
                measurementsId
        );
        return dailyMeasurements;
    }

    @Returner
    private long normalizeTargetDay(String targetDay) {
        return dayFormatter.formatAsTimestamp(targetDay);
    }

    public void fillMeal(DailyMeasurements measurements, MeasurementType type, String glycemia,
                         String postPrandialGlycemia, int insulinUnits, JSONObject content) {
        GlycemicMeasurementItem meal = measurements.getMeasurement(type);
        mealsService.fillMeal(meal, glycemia, postPrandialGlycemia, insulinUnits, content);
    }

    public void fillBasalInsulin(DailyMeasurements measurements, String glycemia, int insulinUnits) {
        BasalInsulin basalInsulin = measurements.getBasalInsulin();
        basalInsulinService.fillBasalInsulin(basalInsulin, glycemia, insulinUnits);
    }

    public void saveDailyNotes(DailyMeasurements measurements, String dailyNotes) {
        String measurementsId = measurements.getId();
        measurementsRepository.saveDailyNotes(dailyNotes, measurementsId);
    }

}
