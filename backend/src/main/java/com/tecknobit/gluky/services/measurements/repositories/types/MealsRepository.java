package com.tecknobit.gluky.services.measurements.repositories.types;

import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper._WHERE_;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.glukycore.ConstantsKt.*;

@Repository
public interface MealsRepository extends GlycemicMeasurementsRepository<Meal> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE " + MEALS_KEY + " SET " +
                    ANNOTATION_DATE_KEY + "=:" + ANNOTATION_DATE_KEY + "," +
                    GLYCEMIA_KEY + "=:" + GLYCEMIA_KEY + "," +
                    POST_PRANDIAL_GLYCEMIA_KEY + "=:" + POST_PRANDIAL_GLYCEMIA_KEY + "," +
                    INSULIN_UNITS_KEY + "=:" + INSULIN_UNITS_KEY + "," +
                    RAW_CONTENT_KEY + "=:" + RAW_CONTENT_KEY +
                    _WHERE_ + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void fillMeal(
            @Param(ANNOTATION_DATE_KEY) long annotationDate,
            @Param(GLYCEMIA_KEY) int glycemia,
            @Param(POST_PRANDIAL_GLYCEMIA_KEY) int postPrandialGlycemia,
            @Param(INSULIN_UNITS_KEY) int insulinUnits,
            @Param(RAW_CONTENT_KEY) String rawContent,
            @Param(IDENTIFIER_KEY) String mealId
    );

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE " + MEALS_KEY + " SET " +
                    GLYCEMIA_KEY + "=:" + GLYCEMIA_KEY + "," +
                    POST_PRANDIAL_GLYCEMIA_KEY + "=:" + POST_PRANDIAL_GLYCEMIA_KEY + "," +
                    INSULIN_UNITS_KEY + "=:" + INSULIN_UNITS_KEY + "," +
                    RAW_CONTENT_KEY + "=:" + RAW_CONTENT_KEY +
                    _WHERE_ + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void fillMeal(
            @Param(GLYCEMIA_KEY) int glycemia,
            @Param(POST_PRANDIAL_GLYCEMIA_KEY) int postPrandialGlycemia,
            @Param(INSULIN_UNITS_KEY) int insulinUnits,
            @Param(RAW_CONTENT_KEY) String rawContent,
            @Param(IDENTIFIER_KEY) String mealId
    );

    @Override
    @Query(
            value = "SELECT ms.* FROM " + MEALS_KEY + " AS ms " +
                    "INNER JOIN " + MEASUREMENTS_KEY + " AS m ON ms." + MEASUREMENT_IDENTIFIER_KEY + "=m." + IDENTIFIER_KEY +
                    _WHERE_ + "m." + CREATION_DATE_KEY + " >= :" + FROM_DATE_KEY +
                    " AND m." + CREATION_DATE_KEY + "< :" + TO_DATE_KEY +
                    " AND m." + OWNER_KEY + "=:" + OWNER_KEY +
                    " AND DAYNAME(FROM_UNIXTIME(" + CREATION_DATE_KEY + "/ 1000)) =:" + GLYCEMIC_TREND_GROUPING_DAY_KEY +
                    " ORDER BY " + CREATION_DATE_KEY,
            nativeQuery = true
    )
    List<Meal> retrieveMeasurements(
            @Param(OWNER_KEY) String owner,
            @Param(GLYCEMIC_TREND_GROUPING_DAY_KEY) String groupingDay,
            @Param(FROM_DATE_KEY) long from,
            @Param(TO_DATE_KEY) long to
    );

    @Override
    @Query(
            value = "SELECT ms.* FROM " + MEALS_KEY + " AS ms " +
                    "INNER JOIN " + MEASUREMENTS_KEY + " AS m ON ms." + MEASUREMENT_IDENTIFIER_KEY + "=m." + IDENTIFIER_KEY +
                    _WHERE_ + "m." + CREATION_DATE_KEY + " >= :" + FROM_DATE_KEY +
                    " AND m." + CREATION_DATE_KEY + "< :" + TO_DATE_KEY +
                    " AND m." + OWNER_KEY + "=:" + OWNER_KEY +
                    " ORDER BY " + CREATION_DATE_KEY,
            nativeQuery = true
    )
    List<Meal> retrieveMeasurements(
            @Param(OWNER_KEY) String owner,
            @Param(FROM_DATE_KEY) long from,
            @Param(TO_DATE_KEY) long to
    );

}
