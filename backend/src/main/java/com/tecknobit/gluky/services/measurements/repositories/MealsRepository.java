package com.tecknobit.gluky.services.measurements.repositories;

import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper._WHERE_;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.glukycore.ConstantsKt.*;

@Repository
public interface MealsRepository extends JpaRepository<Meal, String> {

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

}
