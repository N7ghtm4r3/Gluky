package com.tecknobit.glukycore.enums

import kotlinx.serialization.Serializable

@Serializable
enum class MeasurementType {

    BREAKFAST,

    MORNING_SNACK,

    LUNCH,

    AFTERNOON_SNACK,

    DINNER,

    BASAL_INSULIN;

    companion object {

        fun meals(): List<MeasurementType> {
            return entries.dropLast(1)
        }

    }

}