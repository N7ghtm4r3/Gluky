package com.tecknobit.gluky.services.analyses.service;

import com.tecknobit.equinoxbackend.resourcesutils.ResourcesManager;
import com.tecknobit.gluky.services.analyses.dtos.GlycemicTrendDataContainer;
import com.tecknobit.gluky.services.analyses.helpers.GlycemicItemsOrganizer;
import com.tecknobit.gluky.services.analyses.helpers.ReportGenerator;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.measurements.services.types.BasalInsulinService;
import com.tecknobit.gluky.services.measurements.services.types.MealsService;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.glukycore.enums.GlycemicTrendGroupingDay;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.tecknobit.gluky.services.shared.controllers.DefaultGlukyController.dayFormatter;
import static com.tecknobit.glukycore.helpers.GlukyInputsValidator.UNSET_CUSTOM_DATE;

@Service
public class AnalysesService implements ResourcesManager {

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
        to = convertToStartOfTheDay(to + TimeUnit.DAYS.toMillis(1));
        List<Meal> meals = mealsService.retrieveMeasurements(userId, groupingDay, from, to);
        List<BasalInsulin> basalInsulinRecords = basalInsulinService.retrieveMeasurements(userId, groupingDay, from, to);
        GlycemicItemsOrganizer organizer = new GlycemicItemsOrganizer();
        return new GlycemicTrendDataContainer(
                period,
                organizer.perform(period, meals),
                organizer.perform(period, basalInsulinRecords)
        );
    }

    public String generateReport(GlukyUser user, GlycemicTrendPeriod period, GlycemicTrendGroupingDay groupingDay,
                                 long from, long to) throws IOException {
        ReportGenerator generator = new ReportGenerator(user, period, "");
        generator.generate();
        return "";
    }

    private long convertToStartOfTheDay(long timestamp) {
        String date = dayFormatter.formatAsString(timestamp);
        return dayFormatter.formatAsTimestamp(date);
    }

}
