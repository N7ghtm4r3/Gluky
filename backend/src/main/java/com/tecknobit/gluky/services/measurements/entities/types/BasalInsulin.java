package com.tecknobit.gluky.services.measurements.entities.types;

import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import static com.tecknobit.glukycore.ConstantsKt.BASAL_INSULIN_RECORDS_KEYS;

@Entity
@Table(name = BASAL_INSULIN_RECORDS_KEYS)
public class BasalInsulin extends GlycemicMeasurementItem {

    @EmptyConstructor
    public BasalInsulin() {
        this(null, -1, 0, 0);
    }

    public BasalInsulin(String id, long annotationDate, int glycemia, int insulinUnits) {
        super(id, annotationDate, glycemia, insulinUnits);
    }

}
