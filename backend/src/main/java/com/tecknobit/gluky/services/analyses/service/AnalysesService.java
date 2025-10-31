package com.tecknobit.gluky.services.analyses.service;

import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.gluky.services.analyses.dtos.GlycemicTrendDataContainer;
import com.tecknobit.gluky.services.analyses.dtos.Report;
import com.tecknobit.gluky.services.analyses.helpers.ReportCreator;
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

import static com.tecknobit.equinoxbackend.apis.resources.ResourcesManager.RESOURCES_PATH;
import static com.tecknobit.glukycore.ConstantsKt.REPORTS_KEY;

/**
 * The {@code AnalysesService} class is useful to manage all the analyses database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Service
public class AnalysesService {

    /**
     * {@code measurementsService} the service to manage the measurements database operations
     */
    private final MeasurementsService measurementsService;

    /**
     * Constructor to init the service
     *
     * @param measurementsService The service to manage the measurements database operations
     */
    @Autowired
    public AnalysesService(MeasurementsService measurementsService) {
        this.measurementsService = measurementsService;
    }

    /**
     * Method used to retrieve the glycemic trend
     *
     * @param userId      The identifier of the user
     * @param period      The period to respect with the dates range
     * @param groupingDay The grouping day
     * @param from        The start date from retrieve the measurements
     * @param to          The end date to retrieve the measurements
     * @return the glycemic trend as {@link GlycemicTrendDataContainer}
     */
    public GlycemicTrendDataContainer getGlycemicTrend(String userId, GlycemicTrendPeriod period,
                                                       GlycemicTrendGroupingDay groupingDay, long from, long to) {
        List<DailyMeasurements> dailyMeasurements = measurementsService.getMultipleDailyMeasurements(userId, period,
                groupingDay, from, to);
        return new GlycemicTrendDataContainer(period, dailyMeasurements);
    }

    /**
     * Method used to create a report
     *
     * @param user The user who request the report creation
     * @param period The period to respect with the dates range
     * @param groupingDay The grouping day
     * @param from The start date from retrieve the measurements
     * @param to The end date to retrieve the measurements
     *
     * @return the created report as {@link Report}
     */
    public Report createReport(GlukyUser user, GlycemicTrendPeriod period, GlycemicTrendGroupingDay groupingDay,
                               long from, long to) throws IOException {
        Pair<Long, Long> normalizedDates = measurementsService.normalizeDates(from, to, period);
        from = normalizedDates.getFirst();
        to = normalizedDates.getSecond();
        List<DailyMeasurements> dailyMeasurements = measurementsService.getMultipleDailyMeasurements(user.getId(), period,
                groupingDay, from, to);
        if (dailyMeasurements.isEmpty())
            throw new IllegalStateException("No measurements data available");
        String reportId = EquinoxController.generateIdentifier();
        ReportCreator creator = new ReportCreator(user, period, normalizedDates.getFirst(),
                normalizedDates.getSecond(), dailyMeasurements, reportId);
        Pair<String, String> reportUrl = creator.create();
        return new Report(reportId, reportUrl.getFirst(), reportUrl.getSecond());
    }

    /**
     * Method used to delete a report using its identifier
     *
     * @param reportId The identifier of the report to delete
     *
     * @return whether the report has beel delete as {@code boolean}
     */
    public boolean deleteReport(String reportId) {
        String reportPath = RESOURCES_PATH + REPORTS_KEY + "/";
        File reportsFolder = new File(reportPath);
        String[] reports = reportsFolder.list((dir, name) -> name.contains(reportId));
        if (reports == null || reports.length == 0)
            return false;
        return new File(reportPath + reports[0]).delete();
    }

}
