package com.tecknobit.gluky.services.measurements.controller;

import com.tecknobit.gluky.services.measurements.services.MeasurementsService;
import com.tecknobit.gluky.services.shared.controllers.DefaultGlukyController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.glukycore.ConstantsKt.MEASUREMENTS_KEY;
import static com.tecknobit.glukycore.ConstantsKt.TARGET_DAY_KEY;

@RestController
@RequestMapping(value = BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}/" + MEASUREMENTS_KEY)
public class MeasurementsController extends DefaultGlukyController {

    private final MeasurementsService measurementsService;

    @Autowired
    public MeasurementsController(MeasurementsService measurementsService) {
        this.measurementsService = measurementsService;
    }

    @GetMapping(
            path = "/{" + TARGET_DAY_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    public <T> T getDailyMeasurements(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(TARGET_DAY_KEY) String targetDay // TODO: TO WARN ABOUT THE REQUIRED "dd-MM-yyyy" FORMAT
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(measurementsService.getDailyMeasurements(userId, targetDay));
    }

    @PutMapping(
            path = "/{" + TARGET_DAY_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    public <T> T fillDay(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(TARGET_DAY_KEY) String targetDay // TODO: TO WARN ABOUT THE REQUIRED "dd-MM-yyyy" FORMAT
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        try {
            return (T) successResponse(measurementsService.fillDay(me, targetDay));
        } catch (Exception e) {
            return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

}
