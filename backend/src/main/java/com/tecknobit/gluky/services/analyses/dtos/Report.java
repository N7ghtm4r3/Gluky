package com.tecknobit.gluky.services.analyses.dtos;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tecknobit.equinoxcore.annotations.DTO;

import static com.tecknobit.glukycore.ConstantsKt.REPORT_IDENTIFIER_KEY;
import static com.tecknobit.glukycore.ConstantsKt.REPORT_URL;

@DTO
public record Report(
        @JsonGetter(REPORT_IDENTIFIER_KEY) String reportId,
        String name,
        @JsonGetter(REPORT_URL) String reportUrl
) {
}
