package com.tecknobit.gluky.services.measurements.entities.types;

import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import static com.tecknobit.glukycore.ConstantsKt.BASAL_INSULIN_RECORDS_KEY;

/**
 * The {@code BasalInsulin} class represents the measurement of the basal insulin
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see GlycemicMeasurementItem
 */
@Entity
@Table(name = BASAL_INSULIN_RECORDS_KEY)
public class BasalInsulin extends GlycemicMeasurementItem {

    /**
     * Constructor to init the {@link BasalInsulin} class
     *
     * @apiNote empty constructor required
     */
    @EmptyConstructor
    public BasalInsulin() {
        this(null, null);
    }

    /**
     * Constructor to init the {@link BasalInsulin} class
     *
     * @param id                The identifier of the basal insulin measurement
     * @param dailyMeasurements The container where the measurement is attached
     */
    public BasalInsulin(String id, DailyMeasurements dailyMeasurements) {
        this(id, -1, -1, -1, dailyMeasurements);
    }

    /**
     * Constructor to init the {@link BasalInsulin} class
     *
     * @param id The identifier of the basal insulin measurement
     * @param annotationDate The date when the measurement has been annotated
     * @param glycemia The value of the glycemia when annotated
     * @param insulinUnits The value of the administered insulin units related to the {@link #glycemia} value
     * @param dailyMeasurements The container where the measurement is attached
     */
    public BasalInsulin(String id, long annotationDate, int glycemia, int insulinUnits,
                        DailyMeasurements dailyMeasurements) {
        super(id, annotationDate, glycemia, insulinUnits, dailyMeasurements);
    }

}
