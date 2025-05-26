package com.tecknobit.gluky.services.measurements.services;

import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.repositories.BasalInsulinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;

@Service
public class BasalInsulinService {

    private final BasalInsulinRepository basalInsulinRepository;

    @Autowired
    public BasalInsulinService(BasalInsulinRepository basalInsulinRepository) {
        this.basalInsulinRepository = basalInsulinRepository;
    }

    public BasalInsulin registerBasalInsulinForDay(DailyMeasurements day) {
        BasalInsulin basalInsulin = new BasalInsulin(generateIdentifier(), day);
        basalInsulinRepository.save(basalInsulin);
        return basalInsulin;
    }

}
