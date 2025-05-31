package com.tecknobit.gluky.services.measurements.repositories.types;

import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GlycemicMeasurementsRepository<T extends GlycemicMeasurementItem> extends JpaRepository<T, String> {

}