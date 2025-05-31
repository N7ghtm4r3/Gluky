package com.tecknobit.glukycore.enums

import kotlinx.serialization.Serializable

@Serializable
enum class GlycemicTrendPeriod(
    val millis: Long,
) {

    ONE_WEEK(
        millis = 604_800_000L
    ),

    ONE_MONTH(
        millis = 2_592_000_000L
    ),

    THREE_MONTHS(
        millis = 7_776_000_000L
    ),

    FOUR_MONTHS(
        millis = 10_368_000_000L
    );

}