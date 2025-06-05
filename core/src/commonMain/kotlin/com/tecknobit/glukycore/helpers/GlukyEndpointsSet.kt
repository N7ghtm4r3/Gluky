package com.tecknobit.glukycore.helpers

import com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet

/**
 * The `GlukyEndpointsSet` class is a container with all the Gluky's system endpoints
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see EquinoxBaseEndpointsSet
 */
object GlukyEndpointsSet : EquinoxBaseEndpointsSet() {

    /**
     * `ANALYSES_ENDPOINT` the endpoint used to retrieve the analyses related to the user measurements
     */
    const val ANALYSES_ENDPOINT = "/analyses"

    /**
     * `REPORTS_ENDPOINT` the endpoint used to handle the reports operations
     */
    const val REPORTS_ENDPOINT = "/reports"

}