package com.tecknobit.glukycore.enums

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod.ONE_MONTH
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod.ONE_WEEK
import kotlinx.serialization.Serializable

/**
 * The `GlycemicTrendLabelType` enum represents all the type that a trend label can assume, this indicates to the clients
 * what they have to do to labelling the dates values for example for a chart helper
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Serializable
enum class GlycemicTrendLabelType {

    /**
     * `COMPUTE_WEEK` the label must be computed counting the week of a month, for example: first week,
     * second week, etc...
     */
    COMPUTE_WEEK,

    /**
     * `COMPUTE_MONTH` the label must be computed retrieving the value of the month, for example: April, May, June, etc...
     */
    COMPUTE_MONTH;

    companion object {

        /**
         * Utility method used to retrieve the related label based on the [period]
         *
         * @param period The period used to retrieve the related label type
         *
         * @return the label type as nullable [GlycemicTrendLabelType]
         */
        @Returner
        fun periodToRelatedLabel(
            period: GlycemicTrendPeriod,
        ): GlycemicTrendLabelType? {
            return when (period) {
                ONE_WEEK -> null
                ONE_MONTH -> COMPUTE_WEEK
                else -> COMPUTE_MONTH
            }
        }

    }

}