package com.tecknobit.gluky.services.measurements.repository;

import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper._WHERE_;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.CREATION_DATE_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.OWNER_KEY;
import static com.tecknobit.glukycore.ConstantsKt.MEASUREMENTS_KEY;
import static com.tecknobit.glukycore.ConstantsKt.TARGET_DAY_KEY;

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

}
