package com.tecknobit.gluky.services.measurements.services;

import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.measurements.repositories.MeasurementsRepository;
import com.tecknobit.gluky.services.measurements.services.types.BasalInsulinService;
import com.tecknobit.gluky.services.measurements.services.types.MealsService;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.glukycore.enums.GlycemicTrendGroupingDay;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import com.tecknobit.glukycore.enums.MeasurementType;
import jakarta.transaction.Transactional;
import kotlin.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;
import static com.tecknobit.glukycore.enums.GlycemicTrendGroupingDay.ALL;
import static com.tecknobit.glukycore.helpers.GlukyInputsValidator.UNSET_CUSTOM_DATE;

@Service
public class MeasurementsService {

    private static final String TARGET_DAY_PATTERN = "dd-MM-yyyy";

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

    public List<DailyMeasurements> getMultipleDailyMeasurements(String userId, GlycemicTrendPeriod period,
                                                                GlycemicTrendGroupingDay groupingDay, long from, long to) {
        Pair<Long, Long> normalizedDates = normalizeDates(from, to, period);
        from = convertToStartOfTheDay(normalizedDates.getFirst());
        to = convertToStartOfTheDay(normalizedDates.getSecond());
        if (groupingDay == ALL)
            return measurementsRepository.retrieveMeasurements(userId, from, to);
        else
            return measurementsRepository.retrieveMeasurements(userId, groupingDay.getCapitalized(), from, to);
    }

    public Pair<Long, Long> normalizeDates(long from, long to, GlycemicTrendPeriod period) {
        if (to == UNSET_CUSTOM_DATE)
            to = System.currentTimeMillis();
        if (from == UNSET_CUSTOM_DATE)
            from = to - period.getMillis();
        return new Pair<>(from, to);
    }

    @Returner
    private long convertToStartOfTheDay(long targetDay) {
        TimeFormatter timeFormatter = TimeFormatter.getInstance(TARGET_DAY_PATTERN);
        String targetDateStringed = timeFormatter.formatAsString(targetDay);
        return normalizeTargetDay(targetDateStringed);
    }

    @Returner
    private long normalizeTargetDay(String targetDay) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TARGET_DAY_PATTERN);
        LocalDate localDate = LocalDate.parse(targetDay, formatter);
        Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        return instant.toEpochMilli();
    }

}
