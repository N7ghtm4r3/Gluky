package com.tecknobit.gluky.services.measurements.entities.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.glukycore.enums.MeasurementType;
import jakarta.persistence.*;

import static com.tecknobit.glukycore.ConstantsKt.*;

/**
 * The {@code Meal} class represents the measurement of a meal
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see GlycemicMeasurementItem
 */
@Entity
@Table(name = MEALS_KEY)
public class Meal extends GlycemicMeasurementItem {

    /**
     * {@code EMPTY_CONTENT} the constant value used to represent an empty {@link #rawContent}
     */
    public static final String EMPTY_CONTENT = "{}";

    /**
     * {@code type} the type of the meal
     */
    @Column
    @Enumerated(value = EnumType.STRING)
    private final MeasurementType type;

    /**
     * {@code rawContent} the content of the meal "raw" formatted as json
     */
    @Column(
            name = RAW_CONTENT_KEY,
            columnDefinition = "TEXT DEFAULT '" + EMPTY_CONTENT + "'",
            insertable = false
    )
    private final String rawContent;

    /**
     * {@code postPrandialGlycemia} the value of the post-prandial glycemia when annotated
     */
    @Column(
            name = POST_PRANDIAL_GLYCEMIA_KEY,
            columnDefinition = "INTEGER DEFAULT -1",
            insertable = false
    )
    private final int postPrandialGlycemia;

    /**
     * Constructor to init the {@link Meal} class
     *
     * @apiNote empty constructor required
     */
    @EmptyConstructor
    public Meal() {
        this(null, null, null);
    }

    /**
     * Constructor to init the {@link Meal} class
     *
     * @param id                The identifier of the meal
     * @param type              The type of the meal
     * @param dailyMeasurements The container where the measurement is attached
     */
    public Meal(String id, MeasurementType type, DailyMeasurements dailyMeasurements) {
        this(id, -1, -1, -1, dailyMeasurements, type, EMPTY_CONTENT, -1);
    }

    /**
     * Constructor to init the {@link Meal} class
     *
     * @param id The identifier of the meal
     * @param annotationDate The date when the meal has been annotated
     * @param glycemia The value of the glycemia when annotated
     * @param insulinUnits The value of the administered insulin units related to the {@link #glycemia} value
     * @param dailyMeasurements The container where the meal is attached
     * @param type The type of the meal
     * @param rawContent The content of the meal "raw" formatted as json
     * @param postPrandialGlycemia The value of the post-prandial glycemia when annotated
     */
    public Meal(String id, long annotationDate, int glycemia, int insulinUnits, DailyMeasurements dailyMeasurements,
                MeasurementType type, String rawContent, int postPrandialGlycemia) {
        super(id, annotationDate, glycemia, insulinUnits, dailyMeasurements);
        this.type = type;
        this.rawContent = rawContent;
        this.postPrandialGlycemia = postPrandialGlycemia;
    }

    /**
     * Method used to get {@link #type} instance
     *
     * @return {@link #type} instance as {@link MeasurementType}
     */
    public MeasurementType getType() {
        return type;
    }

    /**
     * Method used to get {@link #rawContent} instance
     *
     * @return {@link #rawContent} instance as {@link String}
     */
    @JsonGetter(RAW_CONTENT_KEY)
    public String getRawContent() {
        return rawContent;
    }

    /**
     * Method used to get {@link #postPrandialGlycemia} instance
     *
     * @return {@link #postPrandialGlycemia} instance as {@code int}
     */
    @JsonGetter(POST_PRANDIAL_GLYCEMIA_KEY)
    public int getPostPrandialGlycemia() {
        return postPrandialGlycemia;
    }

}
