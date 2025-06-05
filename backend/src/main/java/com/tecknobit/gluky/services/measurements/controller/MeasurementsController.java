package com.tecknobit.gluky.services.measurements.controller;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.services.MeasurementsService;
import com.tecknobit.gluky.services.shared.controllers.DefaultGlukyController;
import com.tecknobit.glukycore.enums.MeasurementType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.PUT;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem.UNSET_VALUE;
import static com.tecknobit.glukycore.ConstantsKt.*;
import static com.tecknobit.glukycore.helpers.GlukyInputsValidator.INSTANCE;

/**
 * The {@code MeasurementsController} class is useful to manage all the measurements related requests
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxController
 * @see DefaultGlukyController
 */
@RestController
@RequestMapping(value = BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}/" + MEASUREMENTS_KEY)
public class MeasurementsController extends DefaultGlukyController {

    /**
     * {@code WRONG_GLYCEMIC_VALUE_MESSAGE} error message when a glycemic value is wrong
     */
    private static final String WRONG_GLYCEMIC_VALUE_MESSAGE = "wrong_glycemic_value";

    /**
     * {@code WRONG_INSULIN_UNITS_VALUE_MESSAGE} error message when an insulin units value is wrong
     */
    private static final String WRONG_INSULIN_UNITS_VALUE_MESSAGE = "wrong_insulin_units_value";

    /**
     * {@code WRONG_MEAL_CONTENT_MESSAGE} error message when a content of a meal is wrong
     */
    private static final String WRONG_MEAL_CONTENT_MESSAGE = "wrong_meal_content";

    /**
     * {@code measurementsService} the used service to manage the measurements database operations
     */
    private final MeasurementsService measurementsService;

    /**
     * Constructor to init the controller
     *
     * @param measurementsService The service used to manage the measurements database operations
     */
    @Autowired
    public MeasurementsController(MeasurementsService measurementsService) {
        this.measurementsService = measurementsService;
    }

    /**
     * Endpoint used to retrieve the daily measurements for a specific target day
     *
     * @param userId    The identifier of the user
     * @param token     The token of the user
     * @param targetDay The target day to get
     * @param <T>       the type of the result
     * @return the result of the request as {@link T}
     * @apiNote the format of the target day must be {@code dd-MM-yyyy}
     */
    @GetMapping(
            path = "/{" + TARGET_DAY_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{user_id}/measurements/{target_day}", method = GET)
    public <T> T getDailyMeasurements(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(TARGET_DAY_KEY) String targetDay
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(measurementsService.getDailyMeasurements(userId, targetDay));
    }

    /**
     * Endpoint used to fill a target day
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param targetDay The target day to fill
     *
     * @return the result of the request as {@link T}
     *
     * @param <T> the type of the result
     *
     * @apiNote the format of the target day must be {@code dd-MM-yyyy}
     */
    @PutMapping(
            path = "/{" + TARGET_DAY_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{user_id}/measurements/{target_day}", method = PUT)
    public <T> T fillDay(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(TARGET_DAY_KEY) String targetDay
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        try {
            return (T) successResponse(measurementsService.fillDay(me, targetDay));
        } catch (Exception e) {
            return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    /**
     * Endpoint used to fill a meal
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param targetDay The target day of the meal to fill
     * @param type The type of the meal to fill
     * @param payload Payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "glycemia": "the pre-prendial glycemia value" -> [String],
     *                                  "post_prandial_glycemia": "the post-prendial glycemia value" -> [String],
     *                                  "insulin_units": "the administrated insulin units" -> [Integer],
     *                                  "content": "the content of the meal" -> [JSON],
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the result of the request as {@link T}
     *
     * @param <T> the type of the result
     *
     * @apiNote the format of the target day must be {@code dd-MM-yyyy}
     */
    @PutMapping(
            path = "/{" + TARGET_DAY_KEY + "}/" + MEALS_KEY + "/{" + MEASUREMENT_TYPE_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{user_id}/measurements/{target_day}/meals/{type}", method = PUT)
    public <T> T fillMeal(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(TARGET_DAY_KEY) String targetDay,
            @PathVariable(MEASUREMENT_TYPE_KEY) MeasurementType type,
            @RequestBody Map<String, Object> payload
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        DailyMeasurements measurements = measurementsService.getDailyMeasurements(userId, targetDay);
        if (measurements == null)
            return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        loadJsonHelper(payload);
        String glycemia = jsonHelper.getString(GLYCEMIA_KEY);
        if (!INSTANCE.isGlycemiaValueValid(glycemia))
            return (T) failedResponse(WRONG_GLYCEMIC_VALUE_MESSAGE);
        String postPrandialGlycemia = jsonHelper.getString(POST_PRANDIAL_GLYCEMIA_KEY);
        if (!INSTANCE.isGlycemiaValueValid(postPrandialGlycemia))
            return (T) failedResponse(WRONG_GLYCEMIC_VALUE_MESSAGE);
        int insulinUnits = jsonHelper.getInt(INSULIN_UNITS_KEY, UNSET_VALUE);
        if (insulinUnits < UNSET_VALUE)
            return (T) failedResponse(WRONG_INSULIN_UNITS_VALUE_MESSAGE);
        JSONObject mealContent = jsonHelper.getJSONObject(CONTENT_KEY, new JSONObject());
        if (!INSTANCE.isMealContentValid(mealContent.toString()))
            return (T) failedResponse(WRONG_MEAL_CONTENT_MESSAGE);
        measurementsService.fillMeal(measurements, type, glycemia, postPrandialGlycemia, insulinUnits, mealContent);
        return (T) successResponse(mealContent);
    }

    /**
     * Endpoint used to fill a basal insulin record
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param targetDay The target day of the basal insulin record to fill
     * @param payload Payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "glycemia": "the pre-prendial glycemia value" -> [String],
     *                                  "insulin_units": "the administrated insulin units" -> [Integer],
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the result of the request as {@link T}
     *
     * @param <T> the type of the result
     *
     * @apiNote the format of the target day must be {@code dd-MM-yyyy}
     */
    @PutMapping(
            path = "/{" + TARGET_DAY_KEY + "}/" + BASAL_INSULIN_KEY,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{user_id}/measurements/{target_day}/basal_insulin", method = PUT)
    public <T> T fillBasalInsulin(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(TARGET_DAY_KEY) String targetDay,
            @RequestBody Map<String, Object> payload
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        DailyMeasurements measurements = measurementsService.getDailyMeasurements(userId, targetDay);
        if (measurements == null)
            return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        loadJsonHelper(payload);
        String glycemia = jsonHelper.getString(GLYCEMIA_KEY);
        if (!INSTANCE.isGlycemiaValueValid(glycemia))
            return (T) failedResponse(WRONG_GLYCEMIC_VALUE_MESSAGE);
        int insulinUnits = jsonHelper.getInt(INSULIN_UNITS_KEY, UNSET_VALUE);
        if (insulinUnits < UNSET_VALUE)
            return (T) failedResponse(WRONG_INSULIN_UNITS_VALUE_MESSAGE);
        measurementsService.fillBasalInsulin(measurements, glycemia, insulinUnits);
        return (T) successResponse();
    }

    /**
     * Endpoint used to save the notes related to the target day
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param targetDay The target day where save the notes
     * @param payload Payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "daily_notes": "the content of the daily notes" -> [String]
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the result of the request as {@link T}
     *
     * @param <T> the type of the result
     *
     * @apiNote the format of the target day must be {@code dd-MM-yyyy}
     */
    @PutMapping(
            path = "/{" + TARGET_DAY_KEY + "}/" + DAILY_NOTES_KEY,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{user_id}/measurements/{target_day}/daily_notes", method = PUT)
    public <T> T saveDailyNotes(
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(TARGET_DAY_KEY) String targetDay,
            @RequestBody Map<String, Object> payload
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        DailyMeasurements measurements = measurementsService.getDailyMeasurements(userId, targetDay);
        if (measurements == null)
            return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        loadJsonHelper(payload);
        String dailyNotes = jsonHelper.getString(DAILY_NOTES_KEY, "");
        measurementsService.saveDailyNotes(measurements, dailyNotes);
        return (T) successResponse();
    }

}
