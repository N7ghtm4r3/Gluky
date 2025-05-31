package com.tecknobit.gluky.services.analyses.service;

import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.equinoxbackend.resourcesutils.ResourcesManager;
import com.tecknobit.gluky.services.analyses.dtos.GlycemicTrendDataContainer;
import com.tecknobit.gluky.services.analyses.dtos.Report;
import com.tecknobit.gluky.services.analyses.helpers.ReportGenerator;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.services.MeasurementsService;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.glukycore.enums.GlycemicTrendGroupingDay;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import kotlin.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.tecknobit.glukycore.ConstantsKt.REPORTS_KEY;

@Service
public class AnalysesService implements ResourcesManager {

    private final MeasurementsService measurementsService;

    @Autowired
    public AnalysesService(MeasurementsService measurementsService) {
        this.measurementsService = measurementsService;
    }

    public GlycemicTrendDataContainer getGlycemicTrend(String userId, GlycemicTrendPeriod period,
                                                       GlycemicTrendGroupingDay groupingDay, long from, long to) {
        List<DailyMeasurements> dailyMeasurements = measurementsService.getMultipleDailyMeasurements(userId, period,
                groupingDay, from, to);
        return new GlycemicTrendDataContainer(period, dailyMeasurements);
    }

    public Report generateReport(GlukyUser user, GlycemicTrendPeriod period, GlycemicTrendGroupingDay groupingDay,
                                 long from, long to) throws IOException {
        Pair<Long, Long> normalizedDates = measurementsService.normalizeDates(from, to, period);
        from = normalizedDates.getFirst();
        to = normalizedDates.getSecond();
        List<DailyMeasurements> dailyMeasurements = measurementsService.getMultipleDailyMeasurements(user.getId(), period,
                groupingDay, from, to);
        if (dailyMeasurements.isEmpty())
            throw new IllegalStateException("No measurements data available");
        String reportId = EquinoxController.generateIdentifier();
        ReportGenerator generator = new ReportGenerator(user, period, normalizedDates.getFirst(),
                normalizedDates.getSecond(), dailyMeasurements, reportId);
        String reportUrl = generator.generate();
        return new Report(reportId, reportUrl);
    }

    public boolean deleteReport(String reportId) {
        String reportPath = RESOURCES_PATH + REPORTS_KEY + "/";
        File reportsFolder = new File(reportPath);
        String[] reports = reportsFolder.list((dir, name) -> name.contains(reportId));
        if (reports == null || reports.length == 0)
            return false;
        return new File(reportPath + reports[0]).delete();
    }

}
