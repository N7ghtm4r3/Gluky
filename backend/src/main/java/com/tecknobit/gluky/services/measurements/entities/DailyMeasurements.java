package com.tecknobit.gluky.services.measurements.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.CREATION_DATE_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.OWNER_KEY;
import static com.tecknobit.glukycore.ConstantsKt.*;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity
@Table(
        name = MEASUREMENTS_KEY,
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {CREATION_DATE_KEY, OWNER_KEY}
                )
        }
)
public class DailyMeasurements extends EquinoxItem {

    @Column(name = CREATION_DATE_KEY)
    private final long creationDate;

    @OneToOne
    @JoinColumn(name = BREAKFAST_KEY)
    private final Meal breakfast;

    @OneToOne
    @JoinColumn(name = MORNING_SNACK_KEY)
    private final Meal morningSnack;

    @OneToOne
    @JoinColumn(name = LUNCH_KEY)
    private final Meal lunch;

    @OneToOne
    @JoinColumn(name = AFTERNOON_SNACK_KEY)
    private final Meal afternoonSnack;

    @OneToOne
    @JoinColumn(name = DINNER_KEY)
    private final Meal dinner;

    @OneToOne
    @JoinColumn(name = BASAL_INSULIN_KEY)
    private final BasalInsulin basalInsulin;

    @Lob
    @Column(
            name = DAILY_NOTES_KEY,
            columnDefinition = "LONGTEXT",
            nullable = false
    )
    private final String dailyNotes;

    @ManyToOne
    @OnDelete(action = CASCADE)
    @JoinColumn(name = OWNER_KEY)
    private GlukyUser owner;

    @EmptyConstructor
    public DailyMeasurements() {
        this(null, 0, null, null, null, null, null, null, null);
    }

    public DailyMeasurements(String id, long creationDate, Meal breakfast, Meal morningSnack, Meal lunch, Meal afternoonSnack,
                             Meal dinner, BasalInsulin basalInsulin, String dailyNotes) {
        super(id);
        this.creationDate = creationDate;
        this.breakfast = breakfast;
        this.morningSnack = morningSnack;
        this.lunch = lunch;
        this.afternoonSnack = afternoonSnack;
        this.dinner = dinner;
        this.basalInsulin = basalInsulin;
        this.dailyNotes = dailyNotes;
    }

    @JsonIgnore
    public long getCreationDate() {
        return creationDate;
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