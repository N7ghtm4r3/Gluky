package com.tecknobit.gluky.services.analyses.helpers;

import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import com.tecknobit.glukycore.enums.MeasurementType;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.tecknobit.gluky.services.analyses.dtos.GlycemicTrendDataContainer.MAX_ALLOWED_SETS;

public class GlycemicItemsOrganizer {

    private static final GlycemicItemsOrganizer organizer = new GlycemicItemsOrganizer();

    private GlycemicItemsOrganizer() {
    }

    @Returner
    public HashMap<MeasurementType, HashMap<Integer, List<Meal>>> organizeItems(GlycemicTrendPeriod period,
                                                                                List<Meal> items) {
        switch (period) {
            case ONE_WEEK -> {
                HashMap<MeasurementType, HashMap<Integer, List<Meal>>> organizedItems = new HashMap<>();
                //organizedItems.put(0, items);
                return organizedItems;
            }
            case ONE_MONTH -> {
                return organizePerWeek(items);
            }
            default -> {
                return organizePerMonth(items);
            }
        }
    }

    private HashMap<MeasurementType, HashMap<Integer, List<Meal>>> organizePerWeek(List<Meal> items) {
        HashMap<MeasurementType, HashMap<Integer, List<Meal>>> organizedItems = new HashMap<>();
        for (Meal meal : items) {
            HashMap<Integer, List<Meal>> set = organizedItems.computeIfAbsent(meal.getType(), type -> new HashMap<>());
            int weekOfMonth = getWeekOfMonth(meal.getAnnotationDate());
            List<Meal> organizedSet = set.computeIfAbsent(weekOfMonth, weekN -> new ArrayList<>());
            organizedSet.add(meal);
        }
        return organizedItems;
    }

    private int getWeekOfMonth(long annotationDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(annotationDate));
        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        return Math.min(weekOfMonth / 7, MAX_ALLOWED_SETS);
    }

    private HashMap<MeasurementType, HashMap<Integer, List<Meal>>> organizePerMonth(List<Meal> items) {
        HashMap<MeasurementType, HashMap<Integer, List<Meal>>> organizedItems = new HashMap<>();

        return organizedItems;
    }

    public static GlycemicItemsOrganizer getOrganizer() {
        return organizer;
    }

}
