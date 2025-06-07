package com.tecknobit.gluky.services.analyses.dtos;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tecknobit.equinoxcore.annotations.DTO;

import static com.tecknobit.glukycore.ConstantsKt.REPORT_IDENTIFIER_KEY;
import static com.tecknobit.glukycore.ConstantsKt.REPORT_URL;

/**
 * The {@code Report} record class is used as DTO to share with the clients the created report
 *
 * @param reportId  The identifier of the report
 * @param name      The name of the report
 * @param reportUrl The url where the report can be downloaded
 * @author N7ghtm4r3 - Tecknobit
 */
@DTO
public record Report(
        @JsonGetter(REPORT_IDENTIFIER_KEY) String reportId,
        String name,
        @JsonGetter(REPORT_URL) String reportUrl
) {
}
