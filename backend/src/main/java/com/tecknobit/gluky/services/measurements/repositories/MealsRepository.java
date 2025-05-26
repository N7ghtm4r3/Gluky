package com.tecknobit.gluky.services.measurements.repositories;

import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealsRepository extends JpaRepository<Meal, String> {


}
