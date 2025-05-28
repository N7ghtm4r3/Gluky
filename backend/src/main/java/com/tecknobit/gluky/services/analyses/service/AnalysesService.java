package com.tecknobit.gluky.services.analyses.service;

import com.tecknobit.gluky.services.analyses.dtos.GlycemicTrendDataContainer;
import com.tecknobit.gluky.services.analyses.helpers.GlycemicItemsOrganizer;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.measurements.services.types.BasalInsulinService;
import com.tecknobit.gluky.services.measurements.services.types.MealsService;
import com.tecknobit.glukycore.enums.GlycemicTrendGroupingDay;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.gluky.services.shared.controllers.DefaultGlukyController.dayFormatter;
import static com.tecknobit.glukycore.helpers.GlukyInputsValidator.UNSET_CUSTOM_DATE;

@Service
public class AnalysesService {

    private final MealsService mealsService;

    private final BasalInsulinService basalInsulinService;

    @Autowired
    public AnalysesService(MealsService mealsService, BasalInsulinService basalInsulinService) {
        this.mealsService = mealsService;
        this.basalInsulinService = basalInsulinService;
    }

    public GlycemicTrendDataContainer getGlycemicTrend(String userId, GlycemicTrendPeriod period,
                                                       GlycemicTrendGroupingDay groupingDay, long from, long to) {
        if (to == UNSET_CUSTOM_DATE)
            to = System.currentTimeMillis();
        if (from == UNSET_CUSTOM_DATE)
            from = to - period.getMillis();
        from = convertToStartOfTheDay(from);
        to = convertToStartOfTheDay(to);
        List<Meal> meals = mealsService.retrieveMeasurements(userId, groupingDay, from, to);
        List<BasalInsulin> basalInsulinRecords = basalInsulinService.retrieveMeasurements(userId, groupingDay, from, to);
        GlycemicItemsOrganizer organizer = GlycemicItemsOrganizer.getOrganizer();
        return null;
    }

    private long convertToStartOfTheDay(long timestamp) {
        String date = dayFormatter.formatAsString(timestamp);
        return dayFormatter.formatAsTimestamp(date);
    }

}
