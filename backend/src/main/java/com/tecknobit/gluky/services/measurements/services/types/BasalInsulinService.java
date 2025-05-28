package com.tecknobit.gluky.services.measurements.services.types;

import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.repositories.types.BasalInsulinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;

@Service
public class BasalInsulinService extends GlycemicMeasurementsService<BasalInsulin, BasalInsulinRepository> {

    private final BasalInsulinRepository basalInsulinRepository;

    @Autowired
    public BasalInsulinService(BasalInsulinRepository basalInsulinRepository) {
        super(basalInsulinRepository);
        this.basalInsulinRepository = basalInsulinRepository;
    }

    public BasalInsulin registerBasalInsulinForDay(DailyMeasurements day) {
        BasalInsulin basalInsulin = new BasalInsulin(generateIdentifier(), day);
        basalInsulinRepository.save(basalInsulin);
        return basalInsulin;
    }

    public void fillBasalInsulin(BasalInsulin basalInsulin, String glycemia, int insulinUnits) {
        String basalInsulinId = basalInsulin.getId();
        int glycemiaValue = convertGlycemicValue(glycemia);
        if (!basalInsulin.isFilled()) {
            long annotationDate = System.currentTimeMillis();
            basalInsulinRepository.fillBasalInsulin(
                    annotationDate,
                    glycemiaValue,
                    insulinUnits,
                    basalInsulinId
            );
        } else {
            basalInsulinRepository.fillBasalInsulin(
                    glycemiaValue,
                    insulinUnits,
                    basalInsulinId
            );
        }
    }

}
