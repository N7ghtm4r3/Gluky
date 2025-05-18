package com.tecknobit.glukycore.helpers

import com.tecknobit.equinoxcore.annotations.Validator
import com.tecknobit.equinoxcore.helpers.InputsValidator

object GlukyInputsValidator : InputsValidator() {

    @Validator
    fun glycemiaValueIsValid(
        glycemiaValue: String,
    ): Boolean {
        try {
            val glycemia = glycemiaValue.toInt()
            return glycemia >= 1
        } catch (e: NumberFormatException) {
            return false
        }
    }

}