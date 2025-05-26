package com.tecknobit.gluky.services.measurements.entities.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.glukycore.enums.MeasurementType;
import jakarta.persistence.*;
import org.json.JSONObject;

import static com.tecknobit.glukycore.ConstantsKt.*;

@Entity
@Table(name = MEALS_KEY)
public class Meal extends GlycemicMeasurementItem {

    @Column
    @Enumerated(value = EnumType.STRING)
    private final MeasurementType type;

    @Column
    private final String content;

    @Column(name = RAW_CONTENT_KEY)
    private final String rawContent;

    @Column(name = POST_PRANDIAL_GLYCEMIA_KEY)
    private final int postPrandialGlycemia;

    @EmptyConstructor
    public Meal() {
        this(null, -1, 0, 0, null, null, null, 0);
    }

    public Meal(String id, long annotationDate, int glycemia, int insulinUnits, MeasurementType type, String content,
                String rawContent, int postPrandialGlycemia) {
        super(id, annotationDate, glycemia, insulinUnits);
        this.type = type;
        this.content = content;
        this.rawContent = rawContent;
        this.postPrandialGlycemia = postPrandialGlycemia;
    }

    public MeasurementType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    @JsonGetter(RAW_CONTENT_KEY)
    public JSONObject getRawContent() {
        return new JSONObject(); // TODO: 26/05/2025 PARSE content CORRECLTY 
    }

    @JsonGetter(POST_PRANDIAL_GLYCEMIA_KEY)
    public int getPostPrandialGlycemia() {
        return postPrandialGlycemia;
    }

}
