package com.tecknobit.glukycore.enums

import kotlinx.serialization.Serializable

/**
 * The `MeasurementType` enum represents all the type of the measurements currently available
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Serializable
enum class MeasurementType {

    /**
     * `BREAKFAST` the breakfast measurement type
     */
    BREAKFAST,

    /**
     * `MORNING_SNACK` the morning snack measurement type
     */
    MORNING_SNACK,

    /**
     * `LUNCH` the lunch measurement type
     */
    LUNCH,

    /**
     * `AFTERNOON_SNACK` the afternoon snack measurement type
     */
    AFTERNOON_SNACK,

    /**
     * `DINNER` the dinner measurement type
     */
    DINNER,

    /**
     * `BASAL_INSULIN` the basal insulin measurement type
     */
    BASAL_INSULIN;

    companion object {

        /**
         * Method used to obtain the measurements that are a meals
         *
         * @return the list of the measurements that are a meals as [List] of [MeasurementType]
         */
        fun meals(): List<MeasurementType> {
            return entries.dropLast(1)
        }

    }

}