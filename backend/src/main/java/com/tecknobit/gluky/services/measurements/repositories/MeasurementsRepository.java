package com.tecknobit.gluky.services.measurements.repositories;

import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper._WHERE_;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.glukycore.ConstantsKt.*;

@Repository
public interface MeasurementsRepository extends JpaRepository<DailyMeasurements, String> {

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
