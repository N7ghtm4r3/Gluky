package com.tecknobit.glukycore

import kotlinx.serialization.Serializable

@Serializable
enum class MeasurementType {

    BREAKFAST,

    FIRST_SNACK,

    LUNCH,

    SECOND_SNACK,

    DINNER,

    BASAL_INSULIN;

    companion object {

        fun meals(): List<MeasurementType> {
            return entries.dropLast(1)
        }

    }

}