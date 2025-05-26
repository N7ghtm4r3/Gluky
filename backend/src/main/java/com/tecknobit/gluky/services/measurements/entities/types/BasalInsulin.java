package com.tecknobit.gluky.services.measurements.entities.types;

import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import static com.tecknobit.glukycore.ConstantsKt.BASAL_INSULIN_RECORDS_KEYS;

@Entity
@Table(name = BASAL_INSULIN_RECORDS_KEYS)
public class BasalInsulin extends GlycemicMeasurementItem {

    @EmptyConstructor
    public BasalInsulin() {
        this(null, null);
    }

    public BasalInsulin(String id, DailyMeasurements dailyMeasurements) {
        this(id, -1, -1, -1, dailyMeasurements);
    }

    public BasalInsulin(String id, long annotationDate, int glycemia, int insulinUnits,
                        DailyMeasurements dailyMeasurements) {
        super(id, annotationDate, glycemia, insulinUnits, dailyMeasurements);
    }

}
