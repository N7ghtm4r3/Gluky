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
import com.tecknobit.glukycore.helpers.GlukyInputsValidator;
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

/**
 * The {@code MeasurementsService} class is useful to manage all the measurements database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Service
public class MeasurementsService {

    /**
     * {@code TARGET_DAY_PATTERN} the pattern to format the target day values
     */
    private static final String TARGET_DAY_PATTERN = "dd-MM-yyyy";

    /**
     * {@code measurementsRepository} the instance for the measurements repository
     */
    private final MeasurementsRepository measurementsRepository;

    /**
     * {@code mealsService} the instance for the meals repository
     */
    private final MealsService mealsService;

    /**
     * {@code basalInsulinService} the instance for the basal insulin records repository
     */
    private final BasalInsulinService basalInsulinService;

    /**
     * Constructor to init the service
     *
     * @param measurementsRepository The instance for the measurements repository
     * @param mealsService           The instance for the meals repository
     * @param basalInsulinService    The instance for the basal insulin records repository
     */
    @Autowired
    public MeasurementsService(MeasurementsRepository measurementsRepository, MealsService mealsService,
                               BasalInsulinService basalInsulinService) {
        this.measurementsRepository = measurementsRepository;
        this.mealsService = mealsService;
        this.basalInsulinService = basalInsulinService;
    }

    /**
     * Method used to retrieve the daily measurements
     *
     * @param userId The identifier of the user
     * @param targetDay The target day to retrieve the related measurements
     *
     * @return the daily measurements as {@link DailyMeasurements}
     */
    public DailyMeasurements getDailyMeasurements(String userId, String targetDay) {
        long creationDate = normalizeTargetDay(targetDay);
        return measurementsRepository.getDailyMeasurements(userId, creationDate);
    }

    /**
     * Method used to fill a day
     *
     * @param user The user who requested to fill the day
     * @param targetDay The target day to fill
     *
     * @return the daily measurements just filled as {@link DailyMeasurements}
     */
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

    /**
     * Method used to fill a meal
     *
     * @param measurements The container of the meal
     * @param type The type of the meal
     * @param glycemia The value of the glycemia
     * @param postPrandialGlycemia The value of the post-prandial glycemia
     * @param insulinUnits The administered insulin units
     * @param content The content of the meal "raw" formatted as json
     */
    public void fillMeal(DailyMeasurements measurements, MeasurementType type, String glycemia,
                         String postPrandialGlycemia, int insulinUnits, JSONObject content) {
        GlycemicMeasurementItem meal = measurements.getMeasurement(type);
        mealsService.fillMeal(meal, glycemia, postPrandialGlycemia, insulinUnits, content);
    }

    /**
     * Method used to fill a basal insulin record
     *
     * @param measurements The container of the meal
     * @param glycemia The value of the glycemia
     * @param insulinUnits The administered insulin units
     */
    public void fillBasalInsulin(DailyMeasurements measurements, String glycemia, int insulinUnits) {
        BasalInsulin basalInsulin = measurements.getBasalInsulin();
        basalInsulinService.fillBasalInsulin(basalInsulin, glycemia, insulinUnits);
    }

    /**
     * Method used to save the notes about a day
     *
     * @param measurements The container of the notes
     * @param dailyNotes The content of the notes
     */
    public void saveDailyNotes(DailyMeasurements measurements, String dailyNotes) {
        String measurementsId = measurements.getId();
        measurementsRepository.saveDailyNotes(dailyNotes, measurementsId);
    }


    /**
     * Method used to retrieve the measurements which are between the specified dates range and match with the specified
     * {@code groupingDay}
     *
     * @param userId The identifier of the user
     * @param period The period to respect with the dates range
     * @param groupingDay The grouping day
     * @param from The start date from retrieve the measurements
     * @param to The end date to retrieve the measurements
     *
     * @return the measurements as {@link List} of {@link DailyMeasurements}
     */
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

    /**
     * Method used to normalize the dates whether are {@link GlukyInputsValidator#UNSET_CUSTOM_DATE}
     *
     * @param from   The start date from retrieve the measurements
     * @param to     The end date to retrieve the measurements
     * @param period The period to respect with the dates range
     * @return the normalized dates as {@link Pair} of {@link Long}
     */
    @Returner
    public Pair<Long, Long> normalizeDates(long from, long to, GlycemicTrendPeriod period) {
        if (to == UNSET_CUSTOM_DATE)
            to = System.currentTimeMillis();
        if (from == UNSET_CUSTOM_DATE)
            from = to - period.getMillis();
        return new Pair<>(from, to);
    }

    /**
     * Method used to convert a target day value as start of the day value
     *
     * @param targetDay The value of the target day to convert
     *
     * @return the target day converted as start of the day as {@code long}
     */
    @Returner
    private long convertToStartOfTheDay(long targetDay) {
        TimeFormatter timeFormatter = TimeFormatter.getInstance(TARGET_DAY_PATTERN);
        String targetDateStringed = timeFormatter.formatAsString(targetDay);
        return normalizeTargetDay(targetDateStringed);
    }

    /**
     * Method used to normalize a stringed target day to the related timestamp value
     *
     * @param targetDay The value of the target day to normalize
     *
     * @return the target day normalized as {@link String}
     */
    @Returner
    private long normalizeTargetDay(String targetDay) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TARGET_DAY_PATTERN);
        LocalDate localDate = LocalDate.parse(targetDay, formatter);
        Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        return instant.toEpochMilli();
    }

}
