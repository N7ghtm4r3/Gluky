package com.tecknobit.glukycore.helpers

import com.tecknobit.equinoxcore.annotations.Validator
import com.tecknobit.equinoxcore.helpers.InputsValidator
import com.tecknobit.equinoxcore.json.treatsAsString
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

object GlukyInputsValidator : InputsValidator() {

    const val UNSET_CUSTOM_DATE = -1L

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

    @Validator
    fun isCustomTrendPeriodValid(
        from: Long?,
        to: Long?,
        period: GlycemicTrendPeriod,
    ): Boolean {
        if (from == UNSET_CUSTOM_DATE && to == UNSET_CUSTOM_DATE)
            return true
        val initialDate = from ?: 0
        val endDate = to ?: 0
        if (initialDate <= 0 || endDate <= 0)
            return false
        return ((endDate - initialDate) <= period.millis)
    }

}