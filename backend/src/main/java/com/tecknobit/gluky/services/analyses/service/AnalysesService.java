package com.tecknobit.gluky.services.analyses.service;

import com.tecknobit.gluky.services.measurements.services.types.BasalInsulinService;
import com.tecknobit.gluky.services.measurements.services.types.MealsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalysesService {

    private final MealsService mealsService;

    private final BasalInsulinService basalInsulinService;

    @Autowired
    public AnalysesService(MealsService mealsService, BasalInsulinService basalInsulinService) {
        this.mealsService = mealsService;
        this.basalInsulinService = basalInsulinService;
    }


}
