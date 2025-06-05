package com.tecknobit.gluky.services.measurements.entities.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.OnDelete;

import static com.tecknobit.glukycore.ConstantsKt.*;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

/**
 * The {@code GlycemicMeasurementItem} class represents an item which handle glycemic measurements such glycemia and
 * related insulin units
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 */
@MappedSuperclass
public abstract class GlycemicMeasurementItem extends EquinoxItem {

    /**
     * {@code UNSET_VALUE} constant value used to represents an unset value
     */
    public static final int UNSET_VALUE = -1;

    /**
     * {@code annotationDate} the date when the item has been annotated
     */
    @Column(
            name = ANNOTATION_DATE_KEY,
            columnDefinition = "BIGINT DEFAULT " + UNSET_VALUE,
            insertable = false
    )
    protected final long annotationDate;

    /**
     * {@code glycemia} the value of the glycemia when annotated
     */
    @Column(
            columnDefinition = "INTEGER DEFAULT " + UNSET_VALUE,
            insertable = false
    )
    protected final int glycemia;

    /**
     * {@code insulinUnits} the value of the administered insulin units related to the {@link #glycemia} value
     */
    @Column(
            name = INSULIN_UNITS_KEY,
            columnDefinition = "INTEGER DEFAULT " + UNSET_VALUE,
            insertable = false
    )
    protected final int insulinUnits;

    /**
     * {@code dailyMeasurements} the container where the item is attached
     */
    @ManyToOne
    @OnDelete(action = CASCADE)
    @JoinColumn(name = MEASUREMENT_IDENTIFIER_KEY)
    protected final DailyMeasurements dailyMeasurements;

    /**
     * Constructor to init the {@link GlycemicMeasurementItem}
     *
     * @param id                The identifier of the item
     * @param annotationDate    The date when the item has been annotated
     * @param glycemia          The value of the glycemia when annotated
     * @param insulinUnits      The value of the administered insulin units related to the {@link #glycemia} value
     * @param dailyMeasurements The container where the item is attached
     */
    public GlycemicMeasurementItem(String id, long annotationDate, int glycemia, int insulinUnits,
                                   DailyMeasurements dailyMeasurements) {
        super(id);
        this.annotationDate = annotationDate;
        this.glycemia = glycemia;
        this.insulinUnits = insulinUnits;
        this.dailyMeasurements = dailyMeasurements;
    }

    /**
     * Method used to get {@link #annotationDate} instance
     *
     * @return {@link #annotationDate} instance as {@code long}
     */
    @JsonGetter(ANNOTATION_DATE_KEY)
    public long getAnnotationDate() {
        return annotationDate;
    }

    /**
     * Method used to get {@link #glycemia} instance
     *
     * @return {@link #glycemia} instance as {@code int}
     */
    public int getGlycemia() {
        return glycemia;
    }

    /**
     * Method used to get {@link #insulinUnits} instance
     *
     * @return {@link #insulinUnits} instance as {@code int}
     */
    @JsonGetter(INSULIN_UNITS_KEY)
    public int getInsulinUnits() {
        return insulinUnits;
    }

    /**
     * Method used to check whether the item has been filled
     *
     * @return whether the item has been filled as {@code boolean}
     */
    @JsonIgnore
    public boolean isFilled() {
        return annotationDate != -1;
    }

}
