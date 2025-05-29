package com.tecknobit.gluky.services.analyses.controller;

import com.tecknobit.gluky.services.analyses.service.AnalysesService;
import com.tecknobit.gluky.services.shared.controllers.DefaultGlukyController;
import com.tecknobit.glukycore.enums.GlycemicTrendGroupingDay;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.glukycore.ConstantsKt.*;
import static com.tecknobit.glukycore.helpers.GlukyEndpointsSet.ANALYSES_ENDPOINT;
import static com.tecknobit.glukycore.helpers.GlukyEndpointsSet.REPORTS_ENDPOINT;
import static com.tecknobit.glukycore.helpers.GlukyInputsValidator.INSTANCE;

@RestController
@RequestMapping(value = BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}" + ANALYSES_ENDPOINT)
public class AnalysesController extends DefaultGlukyController {

    private static final String WRONG_CUSTOM_TREND_PERIOD_MESSAGE = "wrong_custom_trend_period";

    private final AnalysesService analysesService;

    @Autowired
    public AnalysesController(AnalysesService analysesService) {
        this.analysesService = analysesService;
    }

    @GetMapping(
            headers = {
                    TOKEN_KEY
            }
    )
    public <T> T getGlycemicTrend(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestHeader(
                    name = GLYCEMIC_TREND_PERIOD_KEY,
                    defaultValue = "ONE_MONTH",
                    required = false
            ) GlycemicTrendPeriod period,
            @RequestHeader(
                    name = GLYCEMIC_TREND_GROUPING_DAY_KEY,
                    defaultValue = "ALL",
                    required = false
            ) GlycemicTrendGroupingDay groupingDay,
            @RequestHeader(name = FROM_DATE_KEY, defaultValue = "-1", required = false) long from,
            @RequestHeader(name = TO_DATE_KEY, defaultValue = "-1", required = false) long to
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if (!INSTANCE.isCustomTrendPeriodValid(from, to, period))
            return (T) failedResponse(WRONG_CUSTOM_TREND_PERIOD_MESSAGE);
        return (T) successResponse(analysesService.getGlycemicTrend(userId, period, groupingDay, from, to));
    }

    @GetMapping(
            path = REPORTS_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    public <T> T generateReport(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestHeader(
                    name = GLYCEMIC_TREND_PERIOD_KEY,
                    defaultValue = "ONE_MONTH",
                    required = false
            ) GlycemicTrendPeriod period,
            @RequestHeader(
                    name = GLYCEMIC_TREND_GROUPING_DAY_KEY,
                    defaultValue = "ALL",
                    required = false
            ) GlycemicTrendGroupingDay groupingDay,
            @RequestHeader(name = FROM_DATE_KEY, defaultValue = "-1", required = false) long from,
            @RequestHeader(name = TO_DATE_KEY, defaultValue = "-1", required = false) long to
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if (!INSTANCE.isCustomTrendPeriodValid(from, to, period))
            return (T) failedResponse(WRONG_CUSTOM_TREND_PERIOD_MESSAGE);
        try {
            return (T) successResponse(analysesService.generateReport(me, period, groupingDay, from, to));
        } catch (IOException e) {
            e.printStackTrace(); // TODO: 29/05/2025 TO REMOVE 
            return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

}
