package com.tecknobit.gluky.services.measurements.repositories.types;

import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
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
public interface BasalInsulinRepository extends JpaRepository<BasalInsulin, String> {

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

}
