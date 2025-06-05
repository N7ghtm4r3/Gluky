package com.tecknobit.gluky.services.analyses.controller;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.gluky.services.analyses.service.AnalysesService;
import com.tecknobit.gluky.services.shared.controllers.DefaultGlukyController;
import com.tecknobit.glukycore.enums.GlycemicTrendGroupingDay;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.DELETE;
import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.glukycore.ConstantsKt.*;
import static com.tecknobit.glukycore.helpers.GlukyEndpointsSet.ANALYSES_ENDPOINT;
import static com.tecknobit.glukycore.helpers.GlukyEndpointsSet.REPORTS_ENDPOINT;
import static com.tecknobit.glukycore.helpers.GlukyInputsValidator.INSTANCE;

/**
 * The {@code AnalysesController} class is useful to manage all the analyses related requests
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxController
 * @see DefaultGlukyController
 */
@RestController
@RequestMapping(value = BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}" + ANALYSES_ENDPOINT)
public class AnalysesController extends DefaultGlukyController {

    /**
     * {@code WRONG_CUSTOM_TREND_PERIOD_MESSAGE} error message when a custom trend period is not valid
     */
    private static final String WRONG_CUSTOM_TREND_PERIOD_MESSAGE = "wrong_custom_trend_period";

    /**
     * {@code analysesService} the service used to manage the analyses database operations
     */
    private final AnalysesService analysesService;

    /**
     * Constructor to init the controller
     *
     * @param analysesService The service used to manage the analyses database operations
     */
    @Autowired
    public AnalysesController(AnalysesService analysesService) {
        this.analysesService = analysesService;
    }

    /**
     * Endpoint used to retrieve the glycemic trend with the specified parameters
     *
     * @param userId      The identifier of the user
     * @param token       The token of the user
     * @param period      The period to respect with the dates range
     * @param groupingDay The grouping day
     * @param from        The start date from retrieve the measurements
     * @param to          The end date to retrieve the measurements
     * @param <T>         the type of the result
     * @return the result of the request as {@link T}
     */
    @GetMapping(
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{user_id}/analyses", method = GET)
    public <T> T getGlycemicTrend(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestParam(
                    name = GLYCEMIC_TREND_PERIOD_KEY,
                    defaultValue = "ONE_MONTH",
                    required = false
            ) GlycemicTrendPeriod period,
            @RequestParam(
                    name = GLYCEMIC_TREND_GROUPING_DAY_KEY,
                    defaultValue = "ALL",
                    required = false
            ) GlycemicTrendGroupingDay groupingDay,
            @RequestParam(name = FROM_DATE_KEY, defaultValue = "-1", required = false) long from,
            @RequestParam(name = TO_DATE_KEY, defaultValue = "-1", required = false) long to
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if (!INSTANCE.isCustomTrendPeriodValid(from, to, period))
            return (T) failedResponse(WRONG_CUSTOM_TREND_PERIOD_MESSAGE);
        return (T) successResponse(analysesService.getGlycemicTrend(userId, period, groupingDay, from, to));
    }

    /**
     * Endpoint used to create a report related to a glycemic trend with the specified parameters
     *
     * @param userId    The identifier of the user
     * @param token     The token of the user
     * @param period The period to respect with the dates range
     * @param groupingDay The grouping day
     * @param from The start date from retrieve the measurements
     * @param to The end date to retrieve the measurements
     *
     * @param <T>       the type of the result
     *
     * @return the result of the request as {@link T}
     */
    @GetMapping(
            path = REPORTS_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{user_id}/analyses/reports", method = GET)
    public <T> T createReport(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestParam(
                    name = GLYCEMIC_TREND_PERIOD_KEY,
                    defaultValue = "ONE_MONTH",
                    required = false
            ) GlycemicTrendPeriod period,
            @RequestParam(
                    name = GLYCEMIC_TREND_GROUPING_DAY_KEY,
                    defaultValue = "ALL",
                    required = false
            ) GlycemicTrendGroupingDay groupingDay,
            @RequestParam(name = FROM_DATE_KEY, defaultValue = "-1", required = false) long from,
            @RequestParam(name = TO_DATE_KEY, defaultValue = "-1", required = false) long to
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if (!INSTANCE.isCustomTrendPeriodValid(from, to, period))
            return (T) failedResponse(WRONG_CUSTOM_TREND_PERIOD_MESSAGE);
        try {
            return (T) successResponse(analysesService.createReport(me, period, groupingDay, from, to));
        } catch (Exception e) {
            return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    /**
     * Endpoint used to delete a report
     *
     * @param userId    The identifier of the user
     * @param token     The token of the user
     * @param reportId The identifier of the report to delete
     *
     * @return the result of the request as {@link String}
     */
    @DeleteMapping(
            path = REPORTS_ENDPOINT + "/{" + REPORT_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{user_id}/analyses/reports/{report_id}", method = DELETE)
    public String deleteReport(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(REPORT_IDENTIFIER_KEY) String reportId
    ) {
        if (!isMe(userId, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if (!analysesService.deleteReport(reportId))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        return successResponse();
    }

}
