package com.tecknobit.gluky.services.measurements.service;

import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.repository.MeasurementsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeasurementsService {

    private static final TimeFormatter formatter = TimeFormatter.getInstance("dd-MM-yyyy");

    private final MeasurementsRepository measurementsRepository;

    @Autowired
    public MeasurementsService(MeasurementsRepository measurementsRepository) {
        this.measurementsRepository = measurementsRepository;
    }

    public DailyMeasurements getDailyMeasurements(String userId, String targetDay) {
        long creationDate = normalizeTargetDay(targetDay);
        return measurementsRepository.getDailyMeasurements(userId, creationDate);
    }

    @Returner
    private long normalizeTargetDay(String targetDay) {
        return formatter.formatAsTimestamp(targetDay);
    }

}
