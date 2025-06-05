package com.tecknobit.gluky.services.measurements.repositories.types;

import com.tecknobit.equinoxcore.annotations.Structure;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * The {@code GlycemicMeasurementsRepository} interface is useful to manage the queries for the glycemic items operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see GlycemicMeasurementItem
 */
@Structure
@NoRepositoryBean
public interface GlycemicMeasurementsRepository<T extends GlycemicMeasurementItem> extends JpaRepository<T, String> {

}