package com.tecknobit.gluky.services.analyses.helpers;

import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.equinoxcore.annotations.Wrapper;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static com.tecknobit.gluky.services.analyses.dtos.GlycemicTrendDataContainer.MAX_ALLOWED_SETS;
import static java.util.Calendar.*;

public class GlycemicItemsOrganizer {

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
        calendar.setTime(new Date(annotationDate));
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
