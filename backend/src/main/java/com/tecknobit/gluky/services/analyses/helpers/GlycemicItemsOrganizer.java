package com.tecknobit.gluky.services.analyses.helpers;

import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.equinoxcore.annotations.Wrapper;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import com.tecknobit.glukycore.enums.MeasurementType;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static com.tecknobit.gluky.services.analyses.dtos.GlycemicTrendDataContainer.MAX_ALLOWED_SETS;
import static com.tecknobit.glukycore.enums.MeasurementType.BASAL_INSULIN;
import static java.util.Calendar.*;

public class GlycemicItemsOrganizer {

    private static final GlycemicItemsOrganizer organizer = new GlycemicItemsOrganizer();

    private GlycemicItemsOrganizer() {
    }

    @Returner
    public <T extends GlycemicMeasurementItem> HashMap<MeasurementType, HashMap<Integer, List<T>>> perform(
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
    private <T extends GlycemicMeasurementItem> HashMap<MeasurementType, HashMap<Integer, List<T>>> organizeSingleWeek(
            List<T> items
    ) {
        return organize(items, t -> 0);
    }

    @Wrapper
    private <T extends GlycemicMeasurementItem> HashMap<MeasurementType, HashMap<Integer, List<T>>> organizePerWeek(
            List<T> items
    ) {
        return organize(items, t -> getWeekOfMonth(t.getAnnotationDate()));
    }

    private int getWeekOfMonth(long annotationDate) {
        Calendar calendar = getInstance();
        calendar.setTime(new Date(annotationDate));
        int weekOfMonth = calendar.get(WEEK_OF_MONTH);
        return Math.min(weekOfMonth, MAX_ALLOWED_SETS);
    }

    @Wrapper
    private <T extends GlycemicMeasurementItem> HashMap<MeasurementType, HashMap<Integer, List<T>>> organizePerMonth(
            List<T> items
    ) {
        return organize(items, t -> getMonth(t.getAnnotationDate()));
    }

    private int getMonth(long annotationDate) {
        Calendar calendar = getInstance();
        calendar.setTime(new Date(annotationDate));
        return calendar.get(MONTH);
    }

    private <T extends GlycemicMeasurementItem> HashMap<MeasurementType, HashMap<Integer, List<T>>> organize(
            List<T> items,
            Function<T, Integer> getOrganizerKey
    ) {
        HashMap<MeasurementType, HashMap<Integer, List<T>>> organizedItems = new HashMap<>();
        for (T item : items) {
            MeasurementType type = getMeasurementType(item);
            HashMap<Integer, List<T>> set = organizedItems.computeIfAbsent(type, measurementType -> new HashMap<>());
            int organizerKey = getOrganizerKey.apply(item);
            List<T> organizedSet = set.computeIfAbsent(organizerKey, key -> new ArrayList<>());
            organizedSet.add(item);
        }
        return organizedItems;
    }

    @Returner
    private MeasurementType getMeasurementType(GlycemicMeasurementItem item) {
        if (item instanceof BasalInsulin)
            return BASAL_INSULIN;
        else
            return ((Meal) item).getType();
    }

    public static GlycemicItemsOrganizer getOrganizer() {
        return organizer;
    }

}
