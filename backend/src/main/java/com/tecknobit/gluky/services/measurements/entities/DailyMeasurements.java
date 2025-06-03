package com.tecknobit.gluky.services.measurements.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.equinoxcore.annotations.CustomParametersOrder;
import com.tecknobit.gluky.services.measurements.entities.types.BasalInsulin;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.glukycore.enums.MeasurementType;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;

import java.util.ArrayList;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.glukycore.ConstantsKt.*;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@SuppressWarnings("FieldCanBeLocal")
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
    @JoinColumn(
            name = BREAKFAST_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + BREAKFAST_KEY + ") REFERENCES " +
                            MEALS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private Meal breakfast;

    @OneToOne
    @JoinColumn(
            name = MORNING_SNACK_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + MORNING_SNACK_KEY + ") REFERENCES " +
                            MEALS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private Meal morningSnack;

    @OneToOne
    @JoinColumn(
            name = LUNCH_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + LUNCH_KEY + ") REFERENCES " +
                            MEALS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private Meal lunch;

    @OneToOne
    @JoinColumn(
            name = AFTERNOON_SNACK_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + AFTERNOON_SNACK_KEY + ") REFERENCES " +
                            MEALS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private Meal afternoonSnack;

    @OneToOne
    @JoinColumn(
            name = DINNER_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + DINNER_KEY + ") REFERENCES " +
                            MEALS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private Meal dinner;

    @OneToOne
    @JoinColumn(
            name = BASAL_INSULIN_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + BASAL_INSULIN_KEY + ") REFERENCES " +
                            BASAL_INSULIN_RECORDS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private BasalInsulin basalInsulin;

    @Lob
    @Column(
            name = DAILY_NOTES_KEY,
            columnDefinition = "LONGTEXT DEFAULT ''",
            nullable = false
    )
    private final String dailyNotes;

    @ManyToOne
    @OnDelete(action = CASCADE)
    @JoinColumn(name = OWNER_KEY)
    private final GlukyUser owner;

    @EmptyConstructor
    public DailyMeasurements() {
        this(null, -1, null, null, null, null, null, null, "", null);
    }

    public DailyMeasurements(String id, long creationDate, GlukyUser owner) {
        this(id, creationDate, null, null, null, null, null, null, "", owner);
    }

    public DailyMeasurements(String id, long creationDate, Meal breakfast, Meal morningSnack, Meal lunch, Meal afternoonSnack,
                             Meal dinner, BasalInsulin basalInsulin, String dailyNotes, GlukyUser owner) {
        super(id);
        this.creationDate = creationDate;
        this.breakfast = breakfast;
        this.morningSnack = morningSnack;
        this.lunch = lunch;
        this.afternoonSnack = afternoonSnack;
        this.dinner = dinner;
        this.basalInsulin = basalInsulin;
        this.dailyNotes = dailyNotes;
        this.owner = owner;
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

    @JsonIgnore
    @CustomParametersOrder(order = {BREAKFAST_KEY, MORNING_SNACK_KEY, LUNCH_KEY, AFTERNOON_SNACK_KEY, DINNER_KEY})
    public void attachMeals(ArrayList<Meal> measurements) {
        breakfast = measurements.get(0);
        morningSnack = measurements.get(1);
        lunch = measurements.get(2);
        afternoonSnack = measurements.get(3);
        dinner = measurements.get(4);
    }

    @JsonIgnore
    public void attachBasalInsulin(BasalInsulin basalInsulin) {
        this.basalInsulin = basalInsulin;
    }

    @JsonIgnore
    public GlycemicMeasurementItem getMeasurement(MeasurementType type) {
        return switch (type) {
            case BREAKFAST -> breakfast;
            case MORNING_SNACK -> morningSnack;
            case LUNCH -> lunch;
            case AFTERNOON_SNACK -> afternoonSnack;
            case DINNER -> dinner;
            case BASAL_INSULIN -> basalInsulin;
        };
    }

    @JsonIgnore
    public boolean isFilled() {
        for (MeasurementType type : MeasurementType.getEntries()) {
            GlycemicMeasurementItem measurement = getMeasurement(type);
            if (measurement.isFilled())
                return true;
        }
        return false;
    }

}