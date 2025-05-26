package com.tecknobit.gluky.services.measurements.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import jakarta.persistence.*;

import static com.tecknobit.glukycore.ConstantsKt.*;

@Entity
@Table(name = MEASUREMENTS_KEY)
public class DailyMeasurements extends EquinoxItem {

    @OneToOne
    private final Meal breakfast;

    @OneToOne
    private final Meal morningSnack;

    @OneToOne
    private final Meal lunch;

    @OneToOne
    private final Meal afternoonSnack;

    @OneToOne
    private final Meal dinner;

    @OneToOne
    private final BasalInsulin basalInsulin;

    @Lob
    @Column(
            name = DAILY_NOTES_KEY,
            columnDefinition = "LONGTEXT",
            nullable = false
    )
    private final String dailyNotes;

    @EmptyConstructor
    public DailyMeasurements() {
        this(null, null, null, null, null, null, null, null);
    }

    public DailyMeasurements(String id, Meal breakfast, Meal morningSnack, Meal lunch, Meal afternoonSnack, Meal dinner,
                             BasalInsulin basalInsulin, String dailyNotes) {
        super(id);
        this.breakfast = breakfast;
        this.morningSnack = morningSnack;
        this.lunch = lunch;
        this.afternoonSnack = afternoonSnack;
        this.dinner = dinner;
        this.basalInsulin = basalInsulin;
        this.dailyNotes = dailyNotes;
    }

    public Meal getBreakfast() {
        return breakfast;
    }

    @JsonGetter(MORNING_SNACK_KEY)
    public Meal getMorningSnack() {
        return morningSnack;
    }

    public Meal getLunch() {
        return lunch;
    }

    @JsonGetter(AFTERNOON_SNACK_KEY)
    public Meal getAfternoonSnack() {
        return afternoonSnack;
    }

    public Meal getDinner() {
        return dinner;
    }

    @JsonGetter(BASAL_INSULIN_KEY)
    public BasalInsulin getBasalInsulin() {
        return basalInsulin;
    }

    @JsonGetter(DAILY_NOTES_KEY)
    public String getDailyNotes() {
        return dailyNotes;
    }

}