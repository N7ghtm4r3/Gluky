package com.tecknobit.glukycore.enums

import kotlinx.serialization.Serializable

@Serializable
enum class GlycemicTrendGroupingDay(
    val capitalized: String,
) {

    ALL("All"),

    SUNDAY("Sunday"),

    MONDAY("Monday"),

    TUESDAY("Tuesday"),

    WEDNESDAY("Wednesday"),

    THURSDAY("Thursday"),

    FRIDAY("Friday"),

    SATURDAY("Saturday")

}