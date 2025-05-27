package com.tecknobit.gluky.services.measurements.services.types;

import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.equinoxcore.annotations.Structure;
import org.springframework.stereotype.Service;

@Service
@Structure
abstract public class GlycemicMeasurementsService {

    @Returner
    protected int convertGlycemicValue(String glycemicValue) {
        if (glycemicValue == null || glycemicValue.isBlank())
            return -1;
        return Integer.parseInt(glycemicValue);
    }

}
