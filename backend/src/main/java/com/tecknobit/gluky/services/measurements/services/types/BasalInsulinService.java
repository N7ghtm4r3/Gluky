package com.tecknobit.gluky.services.measurements.services.types;

import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.repositories.types.BasalInsulinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;

/**
 * The {@code BasalInsulinService} class is useful to manage all the {@link BasalInsulin} database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see GlycemicMeasurementsService
 */
@Service
public class BasalInsulinService extends GlycemicMeasurementsService {

    /**
     * {@code basalInsulinRepository} the instance for the basal insulin repository
     */
    private final BasalInsulinRepository basalInsulinRepository;

    /**
     * Constructor to init the service
     *
     * @param basalInsulinRepository The instance for the basal insulin repository
     */
    @Autowired
    public BasalInsulinService(BasalInsulinRepository basalInsulinRepository) {
        this.basalInsulinRepository = basalInsulinRepository;
    }

    /**
     * Method used to register a basal insulin record for the specified day
     *
     * @param day The day owner of the basal insulin record
     * @return the basal insulin entity just registered as {@link BasalInsulin}
     */
    public BasalInsulin registerBasalInsulinForDay(DailyMeasurements day) {
        BasalInsulin basalInsulin = new BasalInsulin(generateIdentifier(), day);
        basalInsulinRepository.save(basalInsulin);
        return basalInsulin;
    }

    /**
     * Method used to fill a basal insulin record
     *
     * @param basalInsulin The basal insulin record to fill
     * @param glycemia The glycemia value
     * @param insulinUnits The administered insulin units
     */
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
