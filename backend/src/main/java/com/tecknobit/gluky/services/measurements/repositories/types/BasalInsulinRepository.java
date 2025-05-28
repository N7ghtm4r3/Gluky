package com.tecknobit.gluky.services.measurements.repositories.types;

import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
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
public interface BasalInsulinRepository extends GlycemicMeasurementsRepository<BasalInsulin> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE " + BASAL_INSULIN_RECORDS_KEY + " SET " +
                    ANNOTATION_DATE_KEY + "=:" + ANNOTATION_DATE_KEY + "," +
                    GLYCEMIA_KEY + "=:" + GLYCEMIA_KEY + "," +
                    INSULIN_UNITS_KEY + "=:" + INSULIN_UNITS_KEY +
                    _WHERE_ + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void fillBasalInsulin(
            @Param(ANNOTATION_DATE_KEY) long annotationDate,
            @Param(GLYCEMIA_KEY) int glycemia,
            @Param(INSULIN_UNITS_KEY) int insulinUnits,
            @Param(IDENTIFIER_KEY) String basalInsulinId
    );

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE " + BASAL_INSULIN_RECORDS_KEY + " SET " +
                    GLYCEMIA_KEY + "=:" + GLYCEMIA_KEY + "," +
                    INSULIN_UNITS_KEY + "=:" + INSULIN_UNITS_KEY +
                    _WHERE_ + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void fillBasalInsulin(
            @Param(GLYCEMIA_KEY) int glycemia,
            @Param(INSULIN_UNITS_KEY) int insulinUnits,
            @Param(IDENTIFIER_KEY) String basalInsulinId
    );

    @Override
    @Query(
            value = "SELECT bs.* FROM " + BASAL_INSULIN_RECORDS_KEY + " AS bs " +
                    "INNER JOIN " + MEASUREMENTS_KEY + " AS m ON bs." + MEASUREMENT_IDENTIFIER_KEY + "=m." + IDENTIFIER_KEY +
                    _WHERE_ + "m." + CREATION_DATE_KEY + " BETWEEN :" + FROM_DATE_KEY + " AND :" + TO_DATE_KEY +
                    " AND m." + OWNER_KEY + "=:" + OWNER_KEY +
                    " AND DAYNAME(FROM_UNIXTIME(" + CREATION_DATE_KEY + "/ 1000)) =:" + GLYCEMIC_TREND_GROUPING_DAY_KEY +
                    " ORDER BY " + CREATION_DATE_KEY,
            nativeQuery = true
    )
    List<BasalInsulin> retrieveMeasurements(
            @Param(OWNER_KEY) String owner,
            @Param(GLYCEMIC_TREND_GROUPING_DAY_KEY) String groupingDay,
            @Param(FROM_DATE_KEY) long from,
            @Param(TO_DATE_KEY) long to
    );

    @Override
    @Query(
            value = "SELECT bs.* FROM " + BASAL_INSULIN_RECORDS_KEY + " AS bs " +
                    "INNER JOIN " + MEASUREMENTS_KEY + " AS m ON bs." + MEASUREMENT_IDENTIFIER_KEY + "=m." + IDENTIFIER_KEY +
                    _WHERE_ + "m." + CREATION_DATE_KEY + " BETWEEN :" + FROM_DATE_KEY + " AND :" + TO_DATE_KEY +
                    " AND m." + OWNER_KEY + "=:" + OWNER_KEY +
                    " ORDER BY " + CREATION_DATE_KEY,
            nativeQuery = true
    )
    List<BasalInsulin> retrieveMeasurements(
            @Param(OWNER_KEY) String owner,
            @Param(FROM_DATE_KEY) long from,
            @Param(TO_DATE_KEY) long to
    );

}
