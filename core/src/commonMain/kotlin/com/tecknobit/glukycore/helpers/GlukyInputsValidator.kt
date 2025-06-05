package com.tecknobit.glukycore.helpers

import com.tecknobit.equinoxcore.annotations.Validator
import com.tecknobit.equinoxcore.helpers.InputsValidator
import com.tecknobit.equinoxcore.json.treatsAsString
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * The `GlukyInputsValidator` class is useful to validate the inputs
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see InputsValidator
 */
object GlukyInputsValidator : InputsValidator() {

    /**
     * `UNSET_CUSTOM_DATE` constant value used to indicate a date value is currently unset
     */
    const val UNSET_CUSTOM_DATE = -1L

    /**
     * `ONE_DAY_MILLIS` constant value to represent one day as millis
     */
    private const val ONE_DAY_MILLIS = 86_400_000L

    /**
     * Method used to validate a glycemic value
     *
     * @param glycemiaValue The glycemic value to validate
     *
     * @return whether the glycemic value is valid as [Boolean]
     */
    @Validator
    fun isGlycemiaValueValid(
        glycemiaValue: String?,
    ): Boolean {
        if (glycemiaValue.isNullOrEmpty())
            return true
        try {
            val glycemia = glycemiaValue.toInt()
            return glycemia >= 1
        } catch (e: NumberFormatException) {
            return false
        }
    }

    /**
     * Method used to validate the content of the meal
     *
     * @param rawMealContent The content of the meal to validate
     *
     * @return whether the content of the meal is valid as [Boolean]
     */
    @Validator
    fun isMealContentValid(
        rawMealContent: String,
    ): Boolean {
        val mealContent = Json.decodeFromString<JsonObject>(rawMealContent)
        mealContent.entries.forEach { entry ->
            if (entry.key.isBlank() || entry.value.treatsAsString().isBlank())
                return false
        }
        return true
    }

    /**
     * Method used to validate a custom dates range
     *
     * @param from The start date of the range
     * @param to The end date of the range
     * @param period The period to respect with the dates range
     *
     * @return whether the custom dates range is valid as [Boolean]
     */
    @Validator
    fun isCustomTrendPeriodValid(
        from: Long?,
        to: Long?,
        period: GlycemicTrendPeriod,
    ): Boolean {
        if (from == UNSET_CUSTOM_DATE && to == UNSET_CUSTOM_DATE)
            return true
        val initialDate = from ?: 0
        var endDate = to ?: 0
        if (initialDate <= 0 || endDate <= 0)
            return false
        endDate -= ONE_DAY_MILLIS
        return ((endDate - initialDate) <= period.millis)
    }

}