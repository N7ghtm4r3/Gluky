package com.tecknobit.gluky.services.measurements.repositories;

import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tecknobit.equinoxbackend.apis.database.SQLConstants._WHERE_;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.glukycore.ConstantsKt.*;

/**
 * The {@code MeasurementsRepository} interface is useful to manage the queries for the measurement operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see DailyMeasurements
 */
@Repository
public interface MeasurementsRepository extends JpaRepository<DailyMeasurements, String> {

    /**
     * Query used to retrieve the daily measurements owned by the user
     *
     * @param ownerId   The identifier of the owner
     * @param targetDay The target day to retrieve
     * @return the daily measurements as {@link DailyMeasurements}
     */
    @Query(
            value = "SELECT * FROM " + MEASUREMENTS_KEY +
                    _WHERE_ + OWNER_KEY + "=:" + OWNER_KEY +
                    " AND " + CREATION_DATE_KEY + "=:" + TARGET_DAY_KEY,
            nativeQuery = true
    )
    DailyMeasurements getDailyMeasurements(
            @Param(OWNER_KEY) String ownerId,
            @Param(TARGET_DAY_KEY) long targetDay
    );

    /**
     * Query used to attach the identifiers of the measurements
     *
     * @param breakfastId The identifier of the breakfast measurement
     * @param morningSnackId The identifier of the morning snack measurement
     * @param lunchId The identifier of the lunch measurement
     * @param afternoonSnackId The identifier of the afternoon snack measurement
     * @param dinnerId The identifier of the dinner measurement
     * @param basalInsulinId The identifier of the basal insulin measurement
     * @param measurementsId The identifier of the measurements container where attach the identifiers
     */
    @Transactional
    @Modifying(
            clearAutomatically = true
    )
    @Query(
            value = "UPDATE " + MEASUREMENTS_KEY + " SET " +
                    BREAKFAST_KEY + "=:" + BREAKFAST_KEY + "," +
                    MORNING_SNACK_KEY + "=:" + MORNING_SNACK_KEY + "," +
                    LUNCH_KEY + "=:" + LUNCH_KEY + "," +
                    AFTERNOON_SNACK_KEY + "=:" + AFTERNOON_SNACK_KEY + "," +
                    DINNER_KEY + "=:" + DINNER_KEY + "," +
                    BASAL_INSULIN_KEY + "=:" + BASAL_INSULIN_KEY +
                    _WHERE_ + IDENTIFIER_KEY + "=:" + MEASUREMENT_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void attachMeasurements(
            @Param(BREAKFAST_KEY) String breakfastId,
            @Param(MORNING_SNACK_KEY) String morningSnackId,
            @Param(LUNCH_KEY) String lunchId,
            @Param(AFTERNOON_SNACK_KEY) String afternoonSnackId,
            @Param(DINNER_KEY) String dinnerId,
            @Param(BASAL_INSULIN_KEY) String basalInsulinId,
            @Param(MEASUREMENT_IDENTIFIER_KEY) String measurementsId
    );

    /**
     * Query used to save the daily notes related to a specific day
     *
     * @param dailyNotes The content of the notes
     * @param measurementsId The identifier of measurements container where save the notes
     */
    @Transactional
    @Modifying(
            clearAutomatically = true
    )
    @Query(
            value = "UPDATE " + MEASUREMENTS_KEY + " SET " +
                    DAILY_NOTES_KEY + "=:" + DAILY_NOTES_KEY +
                    _WHERE_ + IDENTIFIER_KEY + "=:" + MEASUREMENT_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void saveDailyNotes(
            @Param(DAILY_NOTES_KEY) String dailyNotes,
            @Param(MEASUREMENT_IDENTIFIER_KEY) String measurementsId
    );

    /**
     * Query used to retrieve the measurements which are between the specified dates range and which match with the
     * specified {@code groupingDay}
     *
     * @param owner The owner of the measurements
     * @param groupingDay The grouping day
     * @param from The start date from retrieve the measurements
     * @param to The end date to retrieve the measurements
     *
     * @return the measurements as {@link List} of {@link DailyMeasurements}
     */
    @Query(
            value = "SELECT * FROM " + MEASUREMENTS_KEY +
                    _WHERE_ + CREATION_DATE_KEY + " BETWEEN :" + FROM_DATE_KEY + " AND :" + TO_DATE_KEY +
                    " AND " + OWNER_KEY + "=:" + OWNER_KEY +
                    " AND DAYNAME(FROM_UNIXTIME(" + CREATION_DATE_KEY + "/ 1000)) =:" + GLYCEMIC_TREND_GROUPING_DAY_KEY +
                    " ORDER BY " + CREATION_DATE_KEY,
            nativeQuery = true
    )
    List<DailyMeasurements> retrieveMeasurements(
            @Param(OWNER_KEY) String owner,
            @Param(GLYCEMIC_TREND_GROUPING_DAY_KEY) String groupingDay,
            @Param(FROM_DATE_KEY) long from,
            @Param(TO_DATE_KEY) long to
    );

    /**
     * Query used to retrieve the measurements which are between the specified dates range
     *
     * @param owner The owner of the measurements
     * @param from The start date from retrieve the measurements
     * @param to The end date to retrieve the measurements
     *
     * @return the measurements as {@link List} of {@link DailyMeasurements}
     */
    @Query(
            value = "SELECT * FROM " + MEASUREMENTS_KEY +
                    _WHERE_ + CREATION_DATE_KEY + " BETWEEN :" + FROM_DATE_KEY + " AND :" + TO_DATE_KEY +
                    " AND " + OWNER_KEY + "=:" + OWNER_KEY +
                    " ORDER BY " + CREATION_DATE_KEY,
            nativeQuery = true
    )
    List<DailyMeasurements> retrieveMeasurements(
            @Param(OWNER_KEY) String owner,
            @Param(FROM_DATE_KEY) long from,
            @Param(TO_DATE_KEY) long to
    );

}
