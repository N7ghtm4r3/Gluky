package com.tecknobit.gluky.services.measurements.entities.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.glukycore.enums.MeasurementType;
import jakarta.persistence.*;

import static com.tecknobit.glukycore.ConstantsKt.*;

@Entity
@Table(name = MEALS_KEY)
public class Meal extends GlycemicMeasurementItem {

    public static final String EMPTY_CONTENT = "{}";

    @Column
    @Enumerated(value = EnumType.STRING)
    private final MeasurementType type;

    @Column(
            name = RAW_CONTENT_KEY,
            columnDefinition = "TEXT DEFAULT '" + EMPTY_CONTENT + "'",
            insertable = false
    )
    private final String rawContent;

    @Column(
            name = POST_PRANDIAL_GLYCEMIA_KEY,
            columnDefinition = "INTEGER DEFAULT -1",
            insertable = false
    )
    private final int postPrandialGlycemia;

    @EmptyConstructor
    public Meal() {
        this(null, null, null);
    }

    public Meal(String id, MeasurementType type, DailyMeasurements dailyMeasurements) {
        this(id, -1, -1, -1, dailyMeasurements, type, EMPTY_CONTENT, -1);
    }

    public Meal(String id, long annotationDate, int glycemia, int insulinUnits, DailyMeasurements dailyMeasurements,
                MeasurementType type, String rawContent, int postPrandialGlycemia) {
        super(id, annotationDate, glycemia, insulinUnits, dailyMeasurements);
        this.type = type;
        this.rawContent = rawContent;
        this.postPrandialGlycemia = postPrandialGlycemia;
    }

    public MeasurementType getType() {
        return type;
    }

    @JsonGetter(RAW_CONTENT_KEY)
    public String getRawContent() {
        return rawContent;
    }

    @JsonGetter(POST_PRANDIAL_GLYCEMIA_KEY)
    public int getPostPrandialGlycemia() {
        return postPrandialGlycemia;
    }

}
