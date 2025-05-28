package com.tecknobit.gluky.services.measurements.services.types;

import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.equinoxcore.annotations.Structure;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.gluky.services.measurements.repositories.types.GlycemicMeasurementsRepository;
import com.tecknobit.glukycore.enums.GlycemicTrendGroupingDay;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.glukycore.enums.GlycemicTrendGroupingDay.ALL;

@Service
@Structure
abstract public class GlycemicMeasurementsService<T extends GlycemicMeasurementItem,
        R extends GlycemicMeasurementsRepository<T>> {

    private final R repository;

    protected GlycemicMeasurementsService(R repository) {
        this.repository = repository;
    }

    public List<T> retrieveMeasurements(String userId, GlycemicTrendGroupingDay groupingDay, long from, long to) {
        if (groupingDay == ALL)
            return repository.retrieveMeasurements(userId, from, to);
        else
            return repository.retrieveMeasurements(userId, groupingDay.getCapitalized(), from, to);
    }

    @Returner
    protected int convertGlycemicValue(String glycemicValue) {
        if (glycemicValue == null || glycemicValue.isBlank())
            return -1;
        return Integer.parseInt(glycemicValue);
    }

}
