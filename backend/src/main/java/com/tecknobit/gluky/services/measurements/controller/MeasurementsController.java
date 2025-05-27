package com.tecknobit.gluky.services.measurements.controller;

import com.tecknobit.gluky.services.measurements.services.MeasurementsService;
import com.tecknobit.gluky.services.shared.controllers.DefaultGlukyController;
import com.tecknobit.glukycore.enums.MeasurementType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.glukycore.ConstantsKt.*;
import static com.tecknobit.glukycore.helpers.GlukyInputsValidator.INSTANCE;

@RestController
@RequestMapping(value = BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}/" + MEASUREMENTS_KEY)
public class MeasurementsController extends DefaultGlukyController {

    private static final String WRONG_GLYCEMIC_VALUE_MESSAGE = "wrong_glycemic_value";

    private static final String WRONG_INSULIN_UNITS_VALUE_MESSAGE = "wrong_insulin_units_value";

    private static final String WRONG_MEAL_CONTENT_MESSAGE = "wrong_meal_content";

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

    @PutMapping(
            path = "/{" + TARGET_DAY_KEY + "}/" + MEALS_KEY + "/{" + MEASUREMENT_TYPE_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    public <T> T fillMeal(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(TARGET_DAY_KEY) String targetDay, // TODO: TO WARN ABOUT THE REQUIRED "dd-MM-yyyy" FORMAT
            @PathVariable(MEASUREMENT_TYPE_KEY) MeasurementType type,
            @RequestBody Map<String, Object> payload
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if (!measurementsService.isDayFilled(userId, targetDay))
            return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        loadJsonHelper(payload);
        String glycemia = jsonHelper.getString(GLYCEMIA_KEY);
        if (!INSTANCE.isGlycemiaValueValid(glycemia))
            return (T) failedResponse(WRONG_GLYCEMIC_VALUE_MESSAGE);
        String postPrandialGlycemia = jsonHelper.getString(POST_PRANDIAL_GLYCEMIA_KEY);
        if (!INSTANCE.isGlycemiaValueValid(postPrandialGlycemia))
            return (T) failedResponse(WRONG_GLYCEMIC_VALUE_MESSAGE);
        JSONObject mealContent = jsonHelper.getJSONObject(CONTENT_KEY);
        if (!INSTANCE.isMealContentValid(mealContent.toString()))
            return (T) failedResponse(WRONG_MEAL_CONTENT_MESSAGE);
        int insulinUnits = jsonHelper.getInt(INSULIN_UNITS_KEY, -1);
        return (T) successResponse();
    }

}
