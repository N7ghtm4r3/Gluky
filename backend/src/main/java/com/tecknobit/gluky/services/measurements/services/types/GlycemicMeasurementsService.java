package com.tecknobit.gluky.services.measurements.services.types;

import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.equinoxcore.annotations.Structure;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import org.springframework.stereotype.Service;

/**
 * The {@code GlycemicMeasurementsService} class is useful to manage all the {@link GlycemicMeasurementItem} database
 * operations and to provide a structure to the specific item related services
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Service
@Structure
abstract public class GlycemicMeasurementsService {

    /**
     * Method used to convert as stringed glycemic value into a {@code int} value
     *
     * @param glycemicValue The glycemic value to convert
     * @return the glycemic value converted as {@code int}
     */
    @Returner
    protected int convertGlycemicValue(String glycemicValue) {
        if (glycemicValue == null || glycemicValue.isBlank())
            return -1;
        return Integer.parseInt(glycemicValue);
    }

}
