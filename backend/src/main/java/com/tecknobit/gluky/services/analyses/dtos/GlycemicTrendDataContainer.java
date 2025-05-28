package com.tecknobit.gluky.services.analyses.dtos;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tecknobit.equinoxcore.annotations.DTO;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.glukycore.enums.GlycemicTrendLabelType;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import com.tecknobit.glukycore.enums.MeasurementType;

import java.util.HashMap;
import java.util.List;

import static com.tecknobit.glukycore.ConstantsKt.*;

@DTO
public class GlycemicTrendDataContainer {

    public static final int MAX_ALLOWED_SETS = 4;

    private GlycemicTrendData breakfast;

    private GlycemicTrendData morningSnack;

    private GlycemicTrendData lunch;

    private GlycemicTrendData afternoonSnack;

    private GlycemicTrendData dinner;

    private GlycemicTrendData basalInsulin;

    public GlycemicTrendDataContainer(GlycemicTrendPeriod period,
                                      HashMap<MeasurementType, HashMap<Integer, List<Meal>>> meals,
                                      HashMap<MeasurementType, HashMap<Integer, List<BasalInsulin>>> basalInsulinRecords) {


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

        private final GlycemiaPoint higherGlycemia;

        private final GlycemiaPoint lowerGlycemia;

        private final GlycemiaPoint averageGlycemia;

        private final List<GlycemiaPoint> firstSet;

        private final List<GlycemiaPoint> secondSet;

        private final List<GlycemiaPoint> thirdSet;

        private final List<GlycemiaPoint> fourthSet;

        private final GlycemicTrendLabelType type;

        public GlycemicTrendData(HashMap<Integer, List<GlycemicMeasurementItem>> data) {
            this.higherGlycemia = null;
            this.lowerGlycemia = null;
            this.averageGlycemia = null;
            this.firstSet = firstSet;
            this.secondSet = secondSet;
            this.thirdSet = thirdSet;
            this.fourthSet = fourthSet;
            this.type = type;
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
        public GlycemiaPoint getAverageGlycemia() {
            return averageGlycemia;
        }

        @JsonGetter(FIRST_SET_KEY)
        public List<GlycemiaPoint> getFirstSet() {
            return firstSet;
        }

        @JsonGetter(SECOND_SET_KEY)
        public List<GlycemiaPoint> getSecondSet() {
            return secondSet;
        }

        @JsonGetter(THIRD_SET_KEY)
        public List<GlycemiaPoint> getThirdSet() {
            return thirdSet;
        }

        @JsonGetter(FOURTH_SET_KEY)
        public List<GlycemiaPoint> getFourthSet() {
            return fourthSet;
        }

        @JsonGetter(GLYCEMIC_LABEL_TYPE_KEY)
        public GlycemicTrendLabelType getType() {
            return type;
        }

        public static class GlycemiaPoint {

            private final long date;

            private final double value;

            public GlycemiaPoint(long date, double value) {
                this.date = date;
                this.value = value;
            }

            public long getDate() {
                return date;
            }

            public double getValue() {
                return value;
            }

        }

    }

}
