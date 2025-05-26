package com.tecknobit.gluky.services.measurements.entities.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.OnDelete;

import static com.tecknobit.glukycore.ConstantsKt.*;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@MappedSuperclass
public abstract class GlycemicMeasurementItem extends EquinoxItem {

    @Column(
            name = ANNOTATION_DATE_KEY,
            columnDefinition = "BIGINT DEFAULT -1",
            insertable = false
    )
    protected final long annotationDate;

    @Column(
            columnDefinition = "INTEGER DEFAULT -1",
            insertable = false
    )
    protected final int glycemia;

    @Column(
            name = INSULIN_UNITS_KEY,
            columnDefinition = "INTEGER DEFAULT -1",
            insertable = false
    )
    protected final int insulinUnits;

    @ManyToOne
    @OnDelete(action = CASCADE)
    @JoinColumn(name = MEASUREMENT_IDENTIFIER_KEY)
    protected final DailyMeasurements dailyMeasurements;

    public GlycemicMeasurementItem(String id, long annotationDate, int glycemia, int insulinUnits,
                                   DailyMeasurements dailyMeasurements) {
        super(id);
        this.annotationDate = annotationDate;
        this.glycemia = glycemia;
        this.insulinUnits = insulinUnits;
        this.dailyMeasurements = dailyMeasurements;
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
