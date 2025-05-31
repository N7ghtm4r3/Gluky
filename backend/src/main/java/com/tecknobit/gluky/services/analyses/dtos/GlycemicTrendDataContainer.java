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
import static com.tecknobit.glukycore.ConstantsKt.*;
import static com.tecknobit.glukycore.enums.GlycemicTrendLabelType.Companion;
import static java.util.Calendar.*;

@DTO
public class GlycemicTrendDataContainer {

    public static final int MAX_ALLOWED_SETS = 4;

    private GlycemicTrendData breakfast;

    private GlycemicTrendData morningSnack;

    private GlycemicTrendData lunch;

    private GlycemicTrendData afternoonSnack;

    private GlycemicTrendData dinner;

    private GlycemicTrendData basalInsulin;

    private final GlycemicTrendPeriod period;

    private final List<DailyMeasurements> dailyMeasurements;

    public GlycemicTrendDataContainer(GlycemicTrendPeriod period, List<DailyMeasurements> dailyMeasurements) {
        this.period = period;
        this.dailyMeasurements = dailyMeasurements;
        arrangeTrendData();
    }

    private void arrangeTrendData() {
        GlycemicItemsOrganizer organizer = new GlycemicItemsOrganizer();
        for (MeasurementType type : MeasurementType.getEntries()) {
            List<GlycemicMeasurementItem> items = new ArrayList<>();
            for (DailyMeasurements measurements : dailyMeasurements)
                items.add(measurements.getMeasurement(type));
            loadSpecificTrend(type, organizer.perform(period, items));
        }
    }

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

    @Returner
    private <T extends GlycemicMeasurementItem> GlycemicTrendData convertToTrendData(
            GlycemicTrendPeriod period,
            HashMap<Integer, List<T>> mealMap
    ) {
        if (mealMap == null || mealMap.isEmpty())
            return null;
        return new GlycemicTrendData(
                period,
                mealMap
        );
    }

    public GlycemicTrendData getBreakfast() {
        return breakfast;
    }

    @JsonGetter(MORNING_SNACK_KEY)
    public GlycemicTrendData getMorningSnack() {
        return morningSnack;
    }

    public GlycemicTrendData getLunch() {
        return lunch;
    }

    @JsonGetter(AFTERNOON_SNACK_KEY)
    public GlycemicTrendData getAfternoonSnack() {
        return afternoonSnack;
    }

    public GlycemicTrendData getDinner() {
        return dinner;
    }

    @JsonGetter(BASAL_INSULIN_KEY)
    public GlycemicTrendData getBasalInsulin() {
        return basalInsulin;
    }

    public static class GlycemicTrendData {

        private final GlycemicTrendLabelType type;

        private final List<GlycemiaPoint>[] sets;

        private final GlycemiaPoint higherGlycemia;

        private final GlycemiaPoint lowerGlycemia;

        private final double averageGlycemia;

        public <T extends GlycemicMeasurementItem> GlycemicTrendData(GlycemicTrendPeriod period,
                                                                     HashMap<Integer, List<T>> measurementsMapped) {
            type = Companion.periodToRelatedLabel(period);
            sets = new List[MAX_ALLOWED_SETS];
            loadSets(measurementsMapped);
            higherGlycemia = findHigherGlycemia();
            lowerGlycemia = findLowerGlycemia();
            averageGlycemia = computeAverageGlycemia();
        }

        private <T extends GlycemicMeasurementItem> void loadSets(HashMap<Integer, List<T>> measurementsMapped) {
            int indexSet = 0;
            for (List<T> measurements : measurementsMapped.values()) {
                sets[indexSet] = convertToPoints(measurements);
                indexSet++;
            }
        }

        @Assembler
        private <T extends GlycemicMeasurementItem> List<GlycemiaPoint> convertToPoints(List<T> measurements) {
            if (measurements == null || measurements.isEmpty())
                return null;
            List<GlycemiaPoint> points = new ArrayList<>();
            for (GlycemicMeasurementItem measurement : measurements)
                points.add(new GlycemiaPoint(measurement.getAnnotationDate(), measurement.getGlycemia()));
            return points;
        }

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

        @JsonGetter(GLYCEMIC_LABEL_TYPE_KEY)
        public GlycemicTrendLabelType getType() {
            return type;
        }

        @JsonGetter(FIRST_SET_KEY)
        public List<GlycemiaPoint> getFirstSet() {
            return sets[0];
        }

        @JsonGetter(SECOND_SET_KEY)
        public List<GlycemiaPoint> getSecondSet() {
            return sets[1];
        }

        @JsonGetter(THIRD_SET_KEY)
        public List<GlycemiaPoint> getThirdSet() {
            return sets[2];
        }

        @JsonGetter(FOURTH_SET_KEY)
        public List<GlycemiaPoint> getFourthSet() {
            return sets[3];
        }

        @JsonGetter(HIGHER_GLYCEMIA_KEY)
        public GlycemiaPoint getHigherGlycemia() {
            return higherGlycemia;
        }

        @JsonGetter(LOWER_GLYCEMIA_KEY)
        public GlycemiaPoint getLowerGlycemia() {
            return lowerGlycemia;
        }

        @JsonGetter(AVERAGE_GLYCEMIA_KEY)
        public double getAverageGlycemia() {
            return averageGlycemia;
        }

        public record GlycemiaPoint(long date, double value) {

            public static final class GlycemiaPointComparator implements Comparator<GlycemiaPoint> {

                @Override
                public int compare(GlycemiaPoint o1, GlycemiaPoint o2) {
                    return (int) (o1.value - o2.value);
                }

            }

        }

    }

    private static class GlycemicItemsOrganizer {

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

        @Wrapper
        private <T extends GlycemicMeasurementItem> HashMap<Integer, List<T>> organizeSingleWeek(
                List<T> items
        ) {
            return organize(items, t -> 0);
        }

        @Wrapper
        private <T extends GlycemicMeasurementItem> HashMap<Integer, List<T>> organizePerWeek(
                List<T> items
        ) {
            return organize(items, t -> getWeekOfMonth(t.getAnnotationDate()));
        }

        private int getWeekOfMonth(long annotationDate) {
            Calendar calendar = getInstance();
            calendar.setTime(new java.sql.Date(annotationDate));
            int weekOfMonth = calendar.get(WEEK_OF_MONTH);
            return Math.min(weekOfMonth, MAX_ALLOWED_SETS);
        }

        @Wrapper
        private <T extends GlycemicMeasurementItem> HashMap<Integer, List<T>> organizePerMonth(
                List<T> items
        ) {
            return organize(items, t -> getMonth(t.getAnnotationDate()));
        }

        private int getMonth(long annotationDate) {
            Calendar calendar = getInstance();
            calendar.setTime(new Date(annotationDate));
            return calendar.get(MONTH);
        }

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