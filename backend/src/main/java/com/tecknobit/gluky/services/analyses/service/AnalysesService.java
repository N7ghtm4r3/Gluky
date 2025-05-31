package com.tecknobit.gluky.services.analyses.service;

import com.tecknobit.equinoxbackend.resourcesutils.ResourcesManager;
import com.tecknobit.gluky.services.analyses.dtos.GlycemicTrendDataContainer;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.services.MeasurementsService;
import com.tecknobit.gluky.services.measurements.services.types.BasalInsulinService;
import com.tecknobit.gluky.services.measurements.services.types.MealsService;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.glukycore.enums.GlycemicTrendGroupingDay;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import kotlin.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.tecknobit.gluky.services.shared.controllers.DefaultGlukyController.dayFormatter;
import static com.tecknobit.glukycore.helpers.GlukyInputsValidator.UNSET_CUSTOM_DATE;

@Service
public class AnalysesService implements ResourcesManager {

    private final MeasurementsService measurementsService;

    private final MealsService mealsService;

    private final BasalInsulinService basalInsulinService;

    @Autowired
    public AnalysesService(MeasurementsService measurementsService, MealsService mealsService,
                           BasalInsulinService basalInsulinService) {
        this.measurementsService = measurementsService;
        this.mealsService = mealsService;
        this.basalInsulinService = basalInsulinService;
    }

    /*
    {
    "response": {
        "breakfast": {
            "label_type": "COMPUTE_MONTH",
            "higher_glycemia": {
                "date": 1740873600000,
                "value": 112.0
            },
            "lower_glycemia": {
                "date": 1741046400000,
                "value": 109.0
            },
            "average_glycemia": 110.4,
            "second_set": [
                {
                    "date": 1743465600000,
                    "value": 110.0
                },
                {
                    "date": 1743552000000,
                    "value": 112.0
                },
                {
                    "date": 1743638400000,
                    "value": 111.0
                },
                {
                    "date": 1743724800000,
                    "value": 109.0
                },
                {
                    "date": 1743811200000,
                    "value": 111.0
                },
                {
                    "date": 1743897600000,
                    "value": 110.0
                },
                {
                    "date": 1743984000000,
                    "value": 111.0
                },
                {
                    "date": 1744070400000,
                    "value": 109.0
                },
                {
                    "date": 1744156800000,
                    "value": 111.0
                },
                {
                    "date": 1744243200000,
                    "value": 110.0
                }
            ],
            "fourth_set": null,
            "first_set": [
                {
                    "date": 1740787200000,
                    "value": 110.0
                },
                {
                    "date": 1740873600000,
                    "value": 112.0
                },
                {
                    "date": 1740960000000,
                    "value": 111.0
                },
                {
                    "date": 1741046400000,
                    "value": 109.0
                },
                {
                    "date": 1741132800000,
                    "value": 111.0
                },
                {
                    "date": 1741219200000,
                    "value": 110.0
                },
                {
                    "date": 1741305600000,
                    "value": 111.0
                },
                {
                    "date": 1741392000000,
                    "value": 109.0
                },
                {
                    "date": 1741478400000,
                    "value": 111.0
                },
                {
                    "date": 1741564800000,
                    "value": 110.0
                }
            ],
            "third_set": [
                {
                    "date": 1746057600000,
                    "value": 110.0
                },
                {
                    "date": 1746144000000,
                    "value": 112.0
                },
                {
                    "date": 1746230400000,
                    "value": 111.0
                },
                {
                    "date": 1746316800000,
                    "value": 109.0
                },
                {
                    "date": 1746403200000,
                    "value": 111.0
                },
                {
                    "date": 1746489600000,
                    "value": 110.0
                },
                {
                    "date": 1746576000000,
                    "value": 111.0
                },
                {
                    "date": 1746662400000,
                    "value": 109.0
                },
                {
                    "date": 1746748800000,
                    "value": 111.0
                },
                {
                    "date": 1746835200000,
                    "value": 110.0
                }
            ]
        },
        "lunch": {
            "label_type": "COMPUTE_MONTH",
            "higher_glycemia": {
                "date": 1740873600000,
                "value": 117.0
            },
            "lower_glycemia": {
                "date": 1741046400000,
                "value": 114.0
            },
            "average_glycemia": 115.3,
            "second_set": [
                {
                    "date": 1743465600000,
                    "value": 115.0
                },
                {
                    "date": 1743552000000,
                    "value": 117.0
                },
                {
                    "date": 1743638400000,
                    "value": 116.0
                },
                {
                    "date": 1743724800000,
                    "value": 114.0
                },
                {
                    "date": 1743811200000,
                    "value": 115.0
                },
                {
                    "date": 1743897600000,
                    "value": 116.0
                },
                {
                    "date": 1743984000000,
                    "value": 115.0
                },
                {
                    "date": 1744070400000,
                    "value": 114.0
                },
                {
                    "date": 1744156800000,
                    "value": 115.0
                },
                {
                    "date": 1744243200000,
                    "value": 116.0
                }
            ],
            "fourth_set": null,
            "first_set": [
                {
                    "date": 1740787200000,
                    "value": 115.0
                },
                {
                    "date": 1740873600000,
                    "value": 117.0
                },
                {
                    "date": 1740960000000,
                    "value": 116.0
                },
                {
                    "date": 1741046400000,
                    "value": 114.0
                },
                {
                    "date": 1741132800000,
                    "value": 115.0
                },
                {
                    "date": 1741219200000,
                    "value": 116.0
                },
                {
                    "date": 1741305600000,
                    "value": 115.0
                },
                {
                    "date": 1741392000000,
                    "value": 114.0
                },
                {
                    "date": 1741478400000,
                    "value": 115.0
                },
                {
                    "date": 1741564800000,
                    "value": 116.0
                }
            ],
            "third_set": [
                {
                    "date": 1746057600000,
                    "value": 115.0
                },
                {
                    "date": 1746144000000,
                    "value": 117.0
                },
                {
                    "date": 1746230400000,
                    "value": 116.0
                },
                {
                    "date": 1746316800000,
                    "value": 114.0
                },
                {
                    "date": 1746403200000,
                    "value": 115.0
                },
                {
                    "date": 1746489600000,
                    "value": 116.0
                },
                {
                    "date": 1746576000000,
                    "value": 115.0
                },
                {
                    "date": 1746662400000,
                    "value": 114.0
                },
                {
                    "date": 1746748800000,
                    "value": 115.0
                },
                {
                    "date": 1746835200000,
                    "value": 116.0
                }
            ]
        },
        "dinner": {
            "label_type": "COMPUTE_MONTH",
            "higher_glycemia": {
                "date": 1740873600000,
                "value": 107.0
            },
            "lower_glycemia": {
                "date": 1741046400000,
                "value": 104.0
            },
            "average_glycemia": 105.3,
            "second_set": [
                {
                    "date": 1743465600000,
                    "value": 105.0
                },
                {
                    "date": 1743552000000,
                    "value": 107.0
                },
                {
                    "date": 1743638400000,
                    "value": 106.0
                },
                {
                    "date": 1743724800000,
                    "value": 104.0
                },
                {
                    "date": 1743811200000,
                    "value": 105.0
                },
                {
                    "date": 1743897600000,
                    "value": 106.0
                },
                {
                    "date": 1743984000000,
                    "value": 105.0
                },
                {
                    "date": 1744070400000,
                    "value": 104.0
                },
                {
                    "date": 1744156800000,
                    "value": 105.0
                },
                {
                    "date": 1744243200000,
                    "value": 106.0
                }
            ],
            "fourth_set": null,
            "first_set": [
                {
                    "date": 1740787200000,
                    "value": 105.0
                },
                {
                    "date": 1740873600000,
                    "value": 107.0
                },
                {
                    "date": 1740960000000,
                    "value": 106.0
                },
                {
                    "date": 1741046400000,
                    "value": 104.0
                },
                {
                    "date": 1741132800000,
                    "value": 105.0
                },
                {
                    "date": 1741219200000,
                    "value": 106.0
                },
                {
                    "date": 1741305600000,
                    "value": 105.0
                },
                {
                    "date": 1741392000000,
                    "value": 104.0
                },
                {
                    "date": 1741478400000,
                    "value": 105.0
                },
                {
                    "date": 1741564800000,
                    "value": 106.0
                }
            ],
            "third_set": [
                {
                    "date": 1746057600000,
                    "value": 105.0
                },
                {
                    "date": 1746144000000,
                    "value": 107.0
                },
                {
                    "date": 1746230400000,
                    "value": 106.0
                },
                {
                    "date": 1746316800000,
                    "value": 104.0
                },
                {
                    "date": 1746403200000,
                    "value": 105.0
                },
                {
                    "date": 1746489600000,
                    "value": 106.0
                },
                {
                    "date": 1746576000000,
                    "value": 105.0
                },
                {
                    "date": 1746662400000,
                    "value": 104.0
                },
                {
                    "date": 1746748800000,
                    "value": 105.0
                },
                {
                    "date": 1746835200000,
                    "value": 106.0
                }
            ]
        },
        "morning_snack": {
            "label_type": "COMPUTE_MONTH",
            "higher_glycemia": {
                "date": 1740873600000,
                "value": 102.0
            },
            "lower_glycemia": {
                "date": 1741046400000,
                "value": 99.0
            },
            "average_glycemia": 100.3,
            "second_set": [
                {
                    "date": 1743465600000,
                    "value": 100.0
                },
                {
                    "date": 1743552000000,
                    "value": 102.0
                },
                {
                    "date": 1743638400000,
                    "value": 101.0
                },
                {
                    "date": 1743724800000,
                    "value": 99.0
                },
                {
                    "date": 1743811200000,
                    "value": 100.0
                },
                {
                    "date": 1743897600000,
                    "value": 101.0
                },
                {
                    "date": 1743984000000,
                    "value": 100.0
                },
                {
                    "date": 1744070400000,
                    "value": 99.0
                },
                {
                    "date": 1744156800000,
                    "value": 100.0
                },
                {
                    "date": 1744243200000,
                    "value": 101.0
                }
            ],
            "fourth_set": null,
            "first_set": [
                {
                    "date": 1740787200000,
                    "value": 100.0
                },
                {
                    "date": 1740873600000,
                    "value": 102.0
                },
                {
                    "date": 1740960000000,
                    "value": 101.0
                },
                {
                    "date": 1741046400000,
                    "value": 99.0
                },
                {
                    "date": 1741132800000,
                    "value": 100.0
                },
                {
                    "date": 1741219200000,
                    "value": 101.0
                },
                {
                    "date": 1741305600000,
                    "value": 100.0
                },
                {
                    "date": 1741392000000,
                    "value": 99.0
                },
                {
                    "date": 1741478400000,
                    "value": 100.0
                },
                {
                    "date": 1741564800000,
                    "value": 101.0
                }
            ],
            "third_set": [
                {
                    "date": 1746057600000,
                    "value": 100.0
                },
                {
                    "date": 1746144000000,
                    "value": 102.0
                },
                {
                    "date": 1746230400000,
                    "value": 101.0
                },
                {
                    "date": 1746316800000,
                    "value": 99.0
                },
                {
                    "date": 1746403200000,
                    "value": 100.0
                },
                {
                    "date": 1746489600000,
                    "value": 101.0
                },
                {
                    "date": 1746576000000,
                    "value": 100.0
                },
                {
                    "date": 1746662400000,
                    "value": 99.0
                },
                {
                    "date": 1746748800000,
                    "value": 100.0
                },
                {
                    "date": 1746835200000,
                    "value": 101.0
                }
            ]
        },
        "afternoon_snack": {
            "label_type": "COMPUTE_MONTH",
            "higher_glycemia": {
                "date": 1740873600000,
                "value": 110.0
            },
            "lower_glycemia": {
                "date": 1741046400000,
                "value": 107.0
            },
            "average_glycemia": 108.3,
            "second_set": [
                {
                    "date": 1743465600000,
                    "value": 108.0
                },
                {
                    "date": 1743552000000,
                    "value": 110.0
                },
                {
                    "date": 1743638400000,
                    "value": 109.0
                },
                {
                    "date": 1743724800000,
                    "value": 107.0
                },
                {
                    "date": 1743811200000,
                    "value": 108.0
                },
                {
                    "date": 1743897600000,
                    "value": 109.0
                },
                {
                    "date": 1743984000000,
                    "value": 108.0
                },
                {
                    "date": 1744070400000,
                    "value": 107.0
                },
                {
                    "date": 1744156800000,
                    "value": 108.0
                },
                {
                    "date": 1744243200000,
                    "value": 109.0
                }
            ],
            "fourth_set": null,
            "first_set": [
                {
                    "date": 1740787200000,
                    "value": 108.0
                },
                {
                    "date": 1740873600000,
                    "value": 110.0
                },
                {
                    "date": 1740960000000,
                    "value": 109.0
                },
                {
                    "date": 1741046400000,
                    "value": 107.0
                },
                {
                    "date": 1741132800000,
                    "value": 108.0
                },
                {
                    "date": 1741219200000,
                    "value": 109.0
                },
                {
                    "date": 1741305600000,
                    "value": 108.0
                },
                {
                    "date": 1741392000000,
                    "value": 107.0
                },
                {
                    "date": 1741478400000,
                    "value": 108.0
                },
                {
                    "date": 1741564800000,
                    "value": 109.0
                }
            ],
            "third_set": [
                {
                    "date": 1746057600000,
                    "value": 108.0
                },
                {
                    "date": 1746144000000,
                    "value": 110.0
                },
                {
                    "date": 1746230400000,
                    "value": 109.0
                },
                {
                    "date": 1746316800000,
                    "value": 107.0
                },
                {
                    "date": 1746403200000,
                    "value": 108.0
                },
                {
                    "date": 1746489600000,
                    "value": 109.0
                },
                {
                    "date": 1746576000000,
                    "value": 108.0
                },
                {
                    "date": 1746662400000,
                    "value": 107.0
                },
                {
                    "date": 1746748800000,
                    "value": 108.0
                },
                {
                    "date": 1746835200000,
                    "value": 109.0
                }
            ]
        },
        "basal_insulin": {
            "label_type": "COMPUTE_MONTH",
            "higher_glycemia": {
                "date": 1740960000000,
                "value": 115.0
            },
            "lower_glycemia": {
                "date": 1744243200000,
                "value": 99.0
            },
            "average_glycemia": 106.3,
            "second_set": [
                {
                    "date": 1743465600000,
                    "value": 108.0
                },
                {
                    "date": 1743552000000,
                    "value": 104.0
                },
                {
                    "date": 1743638400000,
                    "value": 113.0
                },
                {
                    "date": 1743724800000,
                    "value": 107.0
                },
                {
                    "date": 1743811200000,
                    "value": 102.0
                },
                {
                    "date": 1743897600000,
                    "value": 100.0
                },
                {
                    "date": 1743984000000,
                    "value": 114.0
                },
                {
                    "date": 1744070400000,
                    "value": 110.0
                },
                {
                    "date": 1744156800000,
                    "value": 103.0
                },
                {
                    "date": 1744243200000,
                    "value": 99.0
                }
            ],
            "fourth_set": null,
            "first_set": [
                {
                    "date": 1740787200000,
                    "value": 110.0
                },
                {
                    "date": 1740873600000,
                    "value": 105.0
                },
                {
                    "date": 1740960000000,
                    "value": 115.0
                },
                {
                    "date": 1741046400000,
                    "value": 108.0
                },
                {
                    "date": 1741132800000,
                    "value": 102.0
                },
                {
                    "date": 1741219200000,
                    "value": 100.0
                },
                {
                    "date": 1741305600000,
                    "value": 112.0
                },
                {
                    "date": 1741392000000,
                    "value": 109.0
                },
                {
                    "date": 1741478400000,
                    "value": 106.0
                },
                {
                    "date": 1741564800000,
                    "value": 101.0
                }
            ],
            "third_set": [
                {
                    "date": 1746057600000,
                    "value": 107.0
                },
                {
                    "date": 1746144000000,
                    "value": 105.0
                },
                {
                    "date": 1746230400000,
                    "value": 111.0
                },
                {
                    "date": 1746316800000,
                    "value": 109.0
                },
                {
                    "date": 1746403200000,
                    "value": 103.0
                },
                {
                    "date": 1746489600000,
                    "value": 101.0
                },
                {
                    "date": 1746576000000,
                    "value": 115.0
                },
                {
                    "date": 1746662400000,
                    "value": 106.0
                },
                {
                    "date": 1746748800000,
                    "value": 104.0
                },
                {
                    "date": 1746835200000,
                    "value": 100.0
                }
            ]
        }
    },
    "status": "SUCCESSFUL"
}
     */
    public GlycemicTrendDataContainer getGlycemicTrend(String userId, GlycemicTrendPeriod period,
                                                       GlycemicTrendGroupingDay groupingDay, long from, long to) {
        List<DailyMeasurements> dailyMeasurements = measurementsService.getMultipleDailyMeasurements(userId, period,
                groupingDay, from, to);
        return new GlycemicTrendDataContainer(period, dailyMeasurements);
        /*Pair<Long, Long> normalizedDates = normalizeDates(from, to, period);
        from = convertToStartOfTheDay(normalizedDates.getFirst());
        to = convertToStartOfTheDay(normalizedDates.getSecond() + TimeUnit.DAYS.toMillis(1));
        List<Meal> meals = mealsService.retrieveMeasurements(userId, groupingDay, from, to);
        List<BasalInsulin> basalInsulinRecords = basalInsulinService.retrieveMeasurements(userId, groupingDay, from, to);*/
        /*GlycemicItemsOrganizer organizer = new GlycemicItemsOrganizer();
        return new GlycemicTrendDataContainer(
                period,
                organizer.perform(period, meals),
                organizer.perform(period, basalInsulinRecords)
        );*/
    }

    public String generateReport(GlukyUser user, GlycemicTrendPeriod period, GlycemicTrendGroupingDay groupingDay,
                                 long from, long to) throws IOException {
        Pair<Long, Long> normalizedDates = measurementsService.normalizeDates(from, to, period);
        from = normalizedDates.getFirst();
        to = normalizedDates.getSecond();
        List<DailyMeasurements> dailyMeasurements = measurementsService.getMultipleDailyMeasurements(user.getId(), period,
                groupingDay, from, to);
        System.out.println(dailyMeasurements);
        return "";
        /*ReportGenerator generator = new ReportGenerator(user, period, normalizedDates.getFirst(),
                normalizedDates.getSecond(), meals, basalInsulinRecords,"");
        generator.generate();
        return "";*/
    }

    // TODO: 31/05/2025 TO REMOVE
    private Pair<Long, Long> normalizeDates(long from, long to, GlycemicTrendPeriod period) {
        if (to == UNSET_CUSTOM_DATE)
            to = System.currentTimeMillis();
        if (from == UNSET_CUSTOM_DATE)
            from = to - period.getMillis();
        return new Pair<>(from, to);
    }

    // TODO: 31/05/2025 TO REMOVE
    private long convertToStartOfTheDay(long timestamp) {
        String date = dayFormatter.formatAsString(timestamp);
        return dayFormatter.formatAsTimestamp(date);
    }

}
