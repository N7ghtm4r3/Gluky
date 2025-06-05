package com.tecknobit.glukycore.enums

import kotlinx.serialization.Serializable

/**
 * The `GlycemicTrendGroupingDay` enum represents the grouping day (the week days) to group the data of the analyses just
 * for one specific day
 *
 * @param capitalized The value of the grouping day capitalized
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Serializable
enum class GlycemicTrendGroupingDay(
    val capitalized: String,
) {

    /**
     * `ALL` group the data for all the week days
     */
    ALL("All"),

    /**
     * `SUNDAY` group the data just for the values of `Sunday`
     */
    SUNDAY("Sunday"),

    /**
     * `MONDAY` group the data just for the values of `Monday`
     */
    MONDAY("Monday"),

    /**
     * `TUESDAY` group the data just for the values of `Tuesday`
     */
    TUESDAY("Tuesday"),

    /**
     * `WEDNESDAY` group the data just for the values of `Wednesday`
     */
    WEDNESDAY("Wednesday"),

    /**
     * `THURSDAY` group the data just for the values of `Thursday`
     */
    THURSDAY("Thursday"),

    /**
     * `FRIDAY` group the data just for the values of `Friday`
     */
    FRIDAY("Friday"),

    /**
     * `SATURDAY` group the data just for the values of `Saturday`
     */
    SATURDAY("Saturday")

}