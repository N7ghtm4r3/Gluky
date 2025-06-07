package com.tecknobit.gluky.services.analyses.dtos;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tecknobit.equinoxcore.annotations.Assembler;
import com.tecknobit.equinoxcore.annotations.DTO;
import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.equinoxcore.annotations.Wrapper;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.glukycore.enums.GlycemicTrendLabelType;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import com.tecknobit.glukycore.enums.MeasurementType;

import java.sql.Date;
import java.util.*;
import java.util.function.Function;

import static com.tecknobit.gluky.services.analyses.dtos.GlycemicTrendDataContainer.GlycemicTrendData.GlycemiaPoint.GlycemiaPointComparator;
import static com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem.UNSET_VALUE;
import static com.tecknobit.glukycore.ConstantsKt.*;
import static com.tecknobit.glukycore.enums.GlycemicTrendLabelType.Companion;
import static java.util.Calendar.*;

/**
 * The {@code GlycemicTrendDataContainer} class is used as DTO to share with the clients the data related to the
 * glycemic trend over a selected period
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@DTO
public class GlycemicTrendDataContainer {

    /**
     * {@code MAX_ALLOWED_SETS} the number of the maximum sets retrievable at once
     */
    public static final int MAX_ALLOWED_SETS = 4;

    /**
     * {@code breakfast} the trend data related to the breakfast measurements
     */
    private GlycemicTrendData breakfast;

    /**
     * {@code morningSnack} the trend data related to the morning snack measurements
     */
    private GlycemicTrendData morningSnack;

    /**
     * {@code lunch} the trend data related to the lunch measurements
     */
    private GlycemicTrendData lunch;

    /**
     * {@code afternoonSnack} the trend data related to the afternoon snack measurements
     */
    private GlycemicTrendData afternoonSnack;

    /**
     * {@code dinner} the trend data related to the dinner measurements
     */
    private GlycemicTrendData dinner;

    /**
     * {@code basalInsulin} the trend data related to the basal insulin measurements
     */
    private GlycemicTrendData basalInsulin;

    /**
     * {@code period} the period used to retrieve the trend data
     */
    private final GlycemicTrendPeriod period;

    /**
     * {@code dailyMeasurements} the measurements retrieved
     */
    private final List<DailyMeasurements> dailyMeasurements;

    /**
     * {@code from} the start date from retrieve the measurements
     */
    private final long from;

    /**
     * {@code to} the end date from retrieve the measurements
     */
    private final long to;

    /**
     * Constructor to init the container
     *
     * @param period            The period used to retrieve the trend data
     * @param dailyMeasurements The measurements retrieved
     */
    public GlycemicTrendDataContainer(GlycemicTrendPeriod period, List<DailyMeasurements> dailyMeasurements) {
        this.period = period;
        this.dailyMeasurements = dailyMeasurements;
        if (!dailyMeasurements.isEmpty()) {
            from = dailyMeasurements.get(0).getCreationDate();
            to = dailyMeasurements.get(dailyMeasurements.size() - 1).getCreationDate();
        } else {
            from = -1;
            to = -1;
        }
        arrangeTrendData();
    }

    /**
     * Method used to arrange the trend data in the specific set
     */
    private void arrangeTrendData() {
        GlycemicItemsOrganizer organizer = new GlycemicItemsOrganizer();
        for (MeasurementType type : MeasurementType.getEntries()) {
            List<GlycemicMeasurementItem> items = new ArrayList<>();
            for (DailyMeasurements measurements : dailyMeasurements) {
                GlycemicMeasurementItem measurement = measurements.getMeasurement(type);
                if (measurement.getGlycemia() != UNSET_VALUE)
                    items.add(measurement);
            }
            loadSpecificTrend(type, organizer.perform(period, items));
        }
    }

    /**
     * Method used to load the specified trend data set
     *
     * @param type The type of the trend to load
     * @param trendData The data used to load the specific set
     *
     * @param <T> the type of the measurement
     */
    private <T extends GlycemicMeasurementItem> void loadSpecificTrend(MeasurementType type,
                                                                       HashMap<Integer, List<T>> trendData) {
        switch (type) {
            case BREAKFAST -> breakfast = convertToTrendData(period, trendData);
            case MORNING_SNACK -> morningSnack = convertToTrendData(period, trendData);
            case LUNCH -> lunch = convertToTrendData(period, trendData);
            case AFTERNOON_SNACK -> afternoonSnack = convertToTrendData(period, trendData);
            case DINNER -> dinner = convertToTrendData(period, trendData);
            case BASAL_INSULIN -> basalInsulin = convertToTrendData(period, trendData);
        }
    }

    /**
     * Method used to convert the raw trend data to the {@link GlycemicTrendData} object
     *
     * @param period The period used to retrieve the trend data 
     * @param trendDataMap The map which contains the data to convert
     *
     * @return the converted data as {@link GlycemicTrendData}
     * @param <T> the type of the measurement
     */
    @Returner
    private <T extends GlycemicMeasurementItem> GlycemicTrendData convertToTrendData(
            GlycemicTrendPeriod period,
            HashMap<Integer, List<T>> trendDataMap
    ) {
        if (trendDataMap == null || trendDataMap.isEmpty())
            return null;
        return new GlycemicTrendData(
                period,
                trendDataMap
        );
    }

    /**
     * Method used to get {@link #breakfast} instance
     *
     * @return {@link #breakfast} instance as {@link GlycemicTrendData}
     */
    public GlycemicTrendData getBreakfast() {
        return breakfast;
    }

    /**
     * Method used to get {@link #morningSnack} instance
     *
     * @return {@link #morningSnack} instance as {@link GlycemicTrendData}
     */
    @JsonGetter(MORNING_SNACK_KEY)
    public GlycemicTrendData getMorningSnack() {
        return morningSnack;
    }

    /**
     * Method used to get {@link #lunch} instance
     *
     * @return {@link #lunch} instance as {@link GlycemicTrendData}
     */
    public GlycemicTrendData getLunch() {
        return lunch;
    }

    /**
     * Method used to get {@link #afternoonSnack} instance
     *
     * @return {@link #afternoonSnack} instance as {@link GlycemicTrendData}
     */
    @JsonGetter(AFTERNOON_SNACK_KEY)
    public GlycemicTrendData getAfternoonSnack() {
        return afternoonSnack;
    }

    /**
     * Method used to get {@link #dinner} instance
     *
     * @return {@link #dinner} instance as {@link GlycemicTrendData}
     */
    public GlycemicTrendData getDinner() {
        return dinner;
    }

    /**
     * Method used to get {@link #basalInsulin} instance
     *
     * @return {@link #basalInsulin} instance as {@link GlycemicTrendData}
     */
    @JsonGetter(BASAL_INSULIN_KEY)
    public GlycemicTrendData getBasalInsulin() {
        return basalInsulin;
    }

    /**
     * Method used to get {@link #from} instance
     *
     * @return {@link #from} instance as {@code long}
     */
    public long getFrom() {
        return from;
    }

    /**
     * Method used to get {@link #to} instance
     *
     * @return {@link #to} instance as {@code long}
     */
    public long getTo() {
        return to;
    }

    /**
     * The {@code GlycemicTrendData} class is used to contains the trend data for each type of measurement
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    public static class GlycemicTrendData {

        /**
         * {@code type} the type of the label
         */
        private final GlycemicTrendLabelType type;

        /**
         * {@code sets} container of each set
         */
        private final List<GlycemiaPoint>[] sets;

        /**
         * {@code higherGlycemia} the higher glycemia value
         */
        private final GlycemiaPoint higherGlycemia;

        /**
         * {@code lowerGlycemia} the lower glycemia value
         */
        private final GlycemiaPoint lowerGlycemia;

        /**
         * {@code averageGlycemia} the average glycemia value
         */
        private final double averageGlycemia;

        /**
         * Constructor to init the object
         *
         * @param period The period used to retrieve the trend data 
         * @param mappedMeasurements The measurements data as map 
         * @param <T> the type of the measurement
         */
        public <T extends GlycemicMeasurementItem> GlycemicTrendData(GlycemicTrendPeriod period,
                                                                     HashMap<Integer, List<T>> mappedMeasurements) {
            type = Companion.periodToRelatedLabel(period);
            sets = new List[MAX_ALLOWED_SETS];
            loadSets(mappedMeasurements);
            higherGlycemia = findHigherGlycemia();
            lowerGlycemia = findLowerGlycemia();
            averageGlycemia = computeAverageGlycemia();
        }

        /**
         * Method used to load the {@link #sets}
         *
         * @param mappedMeasurements The measurements data as map
         * @param <T>                the type of the measurement
         */
        private <T extends GlycemicMeasurementItem> void loadSets(HashMap<Integer, List<T>> mappedMeasurements) {
            int indexSet = 0;
            for (List<T> measurements : mappedMeasurements.values()) {
                sets[indexSet] = convertToPoints(measurements);
                indexSet++;
            }
        }

        /**
         * Method used to convert the measurements into {@link GlycemiaPoint} object
         *
         * @param measurements The measurements list to convert
         *
         * @return The converted measurements list converted as {@link List} of {@link GlycemiaPoint}
         * @param <T> the type of the measurement
         */
        @Assembler
        private <T extends GlycemicMeasurementItem> List<GlycemiaPoint> convertToPoints(List<T> measurements) {
            if (measurements == null || measurements.isEmpty())
                return null;
            List<GlycemiaPoint> points = new ArrayList<>();
            for (GlycemicMeasurementItem measurement : measurements)
                points.add(new GlycemiaPoint(measurement.getAnnotationDate(), measurement.getGlycemia()));
            return points;
        }

        /**
         * Method used to find the higher glycemia point
         *
         * @return the higher glycemia point as {@link GlycemiaPoint}
         */
        private GlycemiaPoint findHigherGlycemia() {
            GlycemiaPoint[] higherPoints = new GlycemiaPoint[MAX_ALLOWED_SETS];
            for (int j = 0; j < MAX_ALLOWED_SETS; j++) {
                List<GlycemiaPoint> comparingSet = sets[j];
                if (comparingSet == null || comparingSet.isEmpty())
                    break;
                higherPoints[j] = comparingSet.stream()
                        .max(new GlycemiaPointComparator())
                        .orElse(null);
            }
            GlycemiaPoint higherGlycemiaPoint = higherPoints[0];
            for (int j = 1; j < MAX_ALLOWED_SETS; j++) {
                GlycemiaPoint comparingPoint = higherPoints[j];
                if (comparingPoint == null)
                    break;
                if (comparingPoint.value > higherGlycemiaPoint.value)
                    higherGlycemiaPoint = comparingPoint;
            }
            return higherGlycemiaPoint;
        }

        /**
         * Method used to find the lower glycemia point
         *
         * @return the lower glycemia point as {@link GlycemiaPoint}
         */
        private GlycemiaPoint findLowerGlycemia() {
            GlycemiaPoint[] lowerPoints = new GlycemiaPoint[MAX_ALLOWED_SETS];
            for (int j = 0; j < MAX_ALLOWED_SETS; j++) {
                List<GlycemiaPoint> comparingSet = sets[j];
                if (comparingSet == null || comparingSet.isEmpty())
                    break;
                lowerPoints[j] = comparingSet.stream()
                        .min(new GlycemiaPointComparator())
                        .orElse(null);
            }
            GlycemiaPoint lowerGlycemiaPoint = lowerPoints[0];
            for (int j = 1; j < MAX_ALLOWED_SETS; j++) {
                GlycemiaPoint comparingPoint = lowerPoints[j];
                if (comparingPoint == null)
                    break;
                if (comparingPoint.value < lowerGlycemiaPoint.value)
                    lowerGlycemiaPoint = comparingPoint;
            }
            return lowerGlycemiaPoint;
        }

        /**
         * Method used to compute the average glycemia value in the {@link #period}
         *
         * @return the average glycemia value as {@code double}
         */
        private double computeAverageGlycemia() {
            double totalGlycemias = 0;
            int totalRecords = 0;
            for (int j = 0; j < MAX_ALLOWED_SETS; j++) {
                List<GlycemiaPoint> points = sets[j];
                if (points == null || points.isEmpty())
                    break;
                totalGlycemias += points.stream()
                        .mapToDouble(GlycemiaPoint::value)
                        .sum();
                totalRecords += points.size();
            }
            return totalGlycemias / totalRecords;
        }

        /**
         * Method used to get {@link #type} instance
         *
         * @return {@link #type} instance as {@link GlycemicTrendLabelType}
         */
        @JsonGetter(GLYCEMIC_LABEL_TYPE_KEY)
        public GlycemicTrendLabelType getType() {
            return type;
        }

        /**
         * Method used to get the first set
         *
         * @return the first set as {@link List} of {@link GlycemiaPoint}
         */
        @JsonGetter(FIRST_SET_KEY)
        public List<GlycemiaPoint> getFirstSet() {
            return sets[0];
        }

        /**
         * Method used to get the second set
         *
         * @return the second set as {@link List} of {@link GlycemiaPoint}
         */
        @JsonGetter(SECOND_SET_KEY)
        public List<GlycemiaPoint> getSecondSet() {
            return sets[1];
        }

        /**
         * Method used to get the second set
         *
         * @return the second set as {@link List} of {@link GlycemiaPoint}
         */
        @JsonGetter(THIRD_SET_KEY)
        public List<GlycemiaPoint> getThirdSet() {
            return sets[2];
        }

        /**
         * Method used to get the fourth set
         *
         * @return the fourth set as {@link List} of {@link GlycemiaPoint}
         */
        @JsonGetter(FOURTH_SET_KEY)
        public List<GlycemiaPoint> getFourthSet() {
            return sets[3];
        }

        /**
         * Method used to get {@link #higherGlycemia} instance
         *
         * @return {@link #higherGlycemia} instance as {@link GlycemiaPoint}
         */
        @JsonGetter(HIGHER_GLYCEMIA_KEY)
        public GlycemiaPoint getHigherGlycemia() {
            return higherGlycemia;
        }

        /**
         * Method used to get {@link #lowerGlycemia} instance
         *
         * @return {@link #lowerGlycemia} instance as {@link GlycemiaPoint}
         */
        @JsonGetter(LOWER_GLYCEMIA_KEY)
        public GlycemiaPoint getLowerGlycemia() {
            return lowerGlycemia;
        }

        /**
         * Method used to get {@link #averageGlycemia} instance
         *
         * @return {@link #averageGlycemia} instance as {@code double}
         */
        @JsonGetter(AVERAGE_GLYCEMIA_KEY)
        public double getAverageGlycemia() {
            return averageGlycemia;
        }

        /**
         * The {@code GlycemiaPoint} record class is useful to represent a value on a chart
         *
         * @param date The date when the value has been annotated
         * @param value The glycemic value
         *
         * @author N7ghtm4r3 - Tecknobit
         */
        public record GlycemiaPoint(long date, double value) {

            /**
             * The {@code GlycemiaPointComparator} class is used to compare the {@link GlycemiaPoint} to correctly
             * order them in the sets
             *
             * @author N7ghtm4r3 - Tecknobit
             *
             * @see java.util.Comparator
             */
            public static final class GlycemiaPointComparator implements Comparator<GlycemiaPoint> {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public int compare(GlycemiaPoint o1, GlycemiaPoint o2) {
                    return (int) (o1.value - o2.value);
                }

            }

        }

    }

    /**
     * The {@code GlycemicItemsOrganizer} utility class is used to organize items in a list into a map,
     * using the period as the key to group the items into specific lists
     *
     * @author N7ghtm4r3 - Tecknobit
     */

    private static class GlycemicItemsOrganizer {

        /**
         * Method used to perform the organization routine
         *
         * @param period The period used to retrieve the trend data 
         * @param items The items list to organize
         *
         * @return the map with the items organized as {@link HashMap} of {@link Integer} of {@link List} of {@link T}
         * @param <T> the type of the measurement
         */
        @Returner
        public <T extends GlycemicMeasurementItem> HashMap<Integer, List<T>> perform(
                GlycemicTrendPeriod period,
                List<T> items
        ) {
            return switch (period) {
                case ONE_WEEK -> organizeSingleWeek(items);
                case ONE_MONTH -> organizePerWeek(items);
                default -> organizePerMonth(items);
            };
        }

        /**
         * Method used to organize the items per a single week
         *
         * @param items The items list to organize 
         * @return the map with the items organized as {@link HashMap} of {@link Integer} of {@link List} of {@link T}
         * @param <T> the type of the measurement
         */
        @Wrapper
        private <T extends GlycemicMeasurementItem> HashMap<Integer, List<T>> organizeSingleWeek(
                List<T> items
        ) {
            return organize(items, t -> 0);
        }

        /**
         * Method used to organize the items per week, for example: first week, second week, etc...
         *
         * @param items The items list to organize 
         * @return the map with the items organized as {@link HashMap} of {@link Integer} of {@link List} of {@link T}
         * @param <T> the type of the measurement
         */
        @Wrapper
        private <T extends GlycemicMeasurementItem> HashMap<Integer, List<T>> organizePerWeek(
                List<T> items
        ) {
            return organize(items, t -> getWeekOfMonth(t.getAnnotationDate()));
        }

        /**
         * Method used to retrive the number of the week
         *
         * @param annotationDate The date when the item has been annotated
         * @return the number of the week as {@code int}
         */
        private int getWeekOfMonth(long annotationDate) {
            Calendar calendar = getInstance();
            calendar.setTime(new java.sql.Date(annotationDate));
            int weekOfMonth = calendar.get(WEEK_OF_MONTH);
            return Math.min(weekOfMonth, MAX_ALLOWED_SETS);
        }

        /**
         * Method used to organize the items per week, for example: April, May, June, etc...
         *
         * @param items The items list to organize 
         * @return the map with the items organized as {@link HashMap} of {@link Integer} of {@link List} of {@link T}
         * @param <T> the type of the measurement
         */
        @Wrapper
        private <T extends GlycemicMeasurementItem> HashMap<Integer, List<T>> organizePerMonth(
                List<T> items
        ) {
            return organize(items, t -> getMonth(t.getAnnotationDate()));
        }

        /**
         * Method used to retrive the number of the month
         *
         * @param annotationDate The date when the item has been annotated
         * @return the number of the month as {@code int}
         */
        private int getMonth(long annotationDate) {
            Calendar calendar = getInstance();
            calendar.setTime(new Date(annotationDate));
            return calendar.get(MONTH);
        }

        /**
         * Core method used to organize the items
         *
         * @param items The items list to organize 
         * @param getOrganizerKey The lambda used to retrieve the key to organize the items
         *
         * @return the map with the items organized as {@link HashMap} of {@link Integer} of {@link List} of {@link T}
         * @param <T> the type of the measurement
         */
        private <T extends GlycemicMeasurementItem> HashMap<Integer, List<T>> organize(
                List<T> items,
                Function<T, Integer> getOrganizerKey
        ) {
            HashMap<Integer, List<T>> organizedItems = new HashMap<>();
            for (T item : items) {
                int organizerKey = getOrganizerKey.apply(item);
                List<T> organizedSet = organizedItems.computeIfAbsent(organizerKey, key -> new ArrayList<>());
                organizedSet.add(item);
            }
            return organizedItems;
        }

    }

}