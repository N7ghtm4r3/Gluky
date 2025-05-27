package com.tecknobit.gluky.services.measurements.services;

import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.measurements.repositories.MeasurementsRepository;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;

@Service
public class MeasurementsService {

    private static final TimeFormatter formatter = TimeFormatter.getInstance("dd-MM-yyyy");

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

    public boolean isDayFilled(String userId, String targetDay) {
        return getDailyMeasurements(userId, targetDay) != null;
    }

    @Returner
    private long normalizeTargetDay(String targetDay) {
        return formatter.formatAsTimestamp(targetDay);
    }

}
