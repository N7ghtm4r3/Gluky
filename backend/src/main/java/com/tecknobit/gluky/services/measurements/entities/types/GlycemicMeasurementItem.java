package com.tecknobit.gluky.services.measurements.entities.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

import static com.tecknobit.glukycore.ConstantsKt.ANNOTATION_DATE_KEY;
import static com.tecknobit.glukycore.ConstantsKt.INSULIN_UNITS_KEY;

@MappedSuperclass
public abstract class GlycemicMeasurementItem extends EquinoxItem {

    @Column(name = ANNOTATION_DATE_KEY)
    protected final long annotationDate;

    @Column
    protected final int glycemia;

    @Column(name = INSULIN_UNITS_KEY)
    protected final int insulinUnits;

    @ManyToOne
    protected DailyMeasurements dailyMeasurements;

    public GlycemicMeasurementItem(String id, long annotationDate, int glycemia, int insulinUnits) {
        super(id);
        this.annotationDate = annotationDate;
        this.glycemia = glycemia;
        this.insulinUnits = insulinUnits;
    }

    @JsonGetter(ANNOTATION_DATE_KEY)
    public long getAnnotationDate() {
        return annotationDate;
    }

    public int getGlycemia() {
        return glycemia;
    }

    @JsonGetter(INSULIN_UNITS_KEY)
    public int getInsulinUnits() {
        return insulinUnits;
    }

}
