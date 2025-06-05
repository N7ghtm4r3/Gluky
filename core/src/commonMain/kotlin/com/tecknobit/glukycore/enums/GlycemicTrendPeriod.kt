package com.tecknobit.glukycore.enums

import kotlinx.serialization.Serializable

/**
 * The `GlycemicTrendPeriod` enum represents all the type of the trend period currently available
 *
 * @param millis The period in millis
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Serializable
enum class GlycemicTrendPeriod(
    val millis: Long,
) {

    /**
     * `ONE_WEEK` period of one week
     */
    ONE_WEEK(
        millis = 604_800_000L
    ),

    /**
     * `ONE_MONTH` period of one moth (30 days)
     */
    ONE_MONTH(
        millis = 2_592_000_000L
    ),

    /**
     * `THREE_MONTHS` period of three months (90 days)
     */
    THREE_MONTHS(
        millis = 7_776_000_000L
    ),

    /**
     * `FOUR_MONTHS` period of four months (120 days)
     */
    FOUR_MONTHS(
        millis = 10_368_000_000L
    );

}