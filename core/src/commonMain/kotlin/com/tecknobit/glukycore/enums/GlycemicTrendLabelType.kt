package com.tecknobit.glukycore.enums

import com.tecknobit.glukycore.enums.GlycemicTrendPeriod.ONE_MONTH
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod.ONE_WEEK
import kotlinx.serialization.Serializable

@Serializable
enum class GlycemicTrendLabelType {

    COMPUTE_WEEK,

    COMPUTE_MONTH;

    companion object {

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