package com.tecknobit.glukycore.helpers

import com.tecknobit.equinoxcore.annotations.Validator
import com.tecknobit.equinoxcore.helpers.InputsValidator
import com.tecknobit.equinoxcore.json.treatsAsString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

object GlukyInputsValidator : InputsValidator() {

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

}