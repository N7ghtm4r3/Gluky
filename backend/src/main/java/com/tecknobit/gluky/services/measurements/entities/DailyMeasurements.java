package com.tecknobit.gluky.services.measurements.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.equinoxcore.annotations.CustomParametersOrder;
import com.tecknobit.equinoxcore.annotations.Returner;
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

/**
 * The {@code DailyMeasurements} class represents the container of the daily measurements inserted by the user that
 * currently owns
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 */
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

    /**
     * {@code creationDate} the date of the creation of the container
     */
    @Column(name = CREATION_DATE_KEY)
    private final long creationDate;

    /**
     * {@code breakfast} the breakfast measurement
     */
    @OneToOne
    @JoinColumn(
            name = BREAKFAST_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + BREAKFAST_KEY + ") REFERENCES " +
                            MEALS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private Meal breakfast;

    /**
     * {@code morningSnack} the morning snack measurement
     */
    @OneToOne
    @JoinColumn(
            name = MORNING_SNACK_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + MORNING_SNACK_KEY + ") REFERENCES " +
                            MEALS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private Meal morningSnack;

    /**
     * {@code lunch} the lunch measurement
     */
    @OneToOne
    @JoinColumn(
            name = LUNCH_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + LUNCH_KEY + ") REFERENCES " +
                            MEALS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private Meal lunch;

    /**
     * {@code afternoonSnack} the afternoon snack measurement
     */
    @OneToOne
    @JoinColumn(
            name = AFTERNOON_SNACK_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + AFTERNOON_SNACK_KEY + ") REFERENCES " +
                            MEALS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private Meal afternoonSnack;

    /**
     * {@code dinner} the dinner measurement
     */
    @OneToOne
    @JoinColumn(
            name = DINNER_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + DINNER_KEY + ") REFERENCES " +
                            MEALS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private Meal dinner;

    /**
     * {@code basalInsulin} the basal insulin measurement
     */
    @OneToOne
    @JoinColumn(
            name = BASAL_INSULIN_KEY,
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + BASAL_INSULIN_KEY + ") REFERENCES " +
                            BASAL_INSULIN_RECORDS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    private BasalInsulin basalInsulin;

    /**
     * {@code dailyNotes} the notes about the daily measurements
     */
    @Lob
    @Column(
            name = DAILY_NOTES_KEY,
            columnDefinition = "LONGTEXT DEFAULT ''",
            nullable = false
    )
    private final String dailyNotes;

    /**
     * {@code owner} the owner of the daily measurements
     */
    @ManyToOne
    @OnDelete(action = CASCADE)
    @JoinColumn(name = OWNER_KEY)
    @SuppressWarnings("FieldCanBeLocal")
    private final GlukyUser owner;

    /**
     * Constructor to init the {@link DailyMeasurements} class
     *
     * @apiNote empty constructor required
     */
    @EmptyConstructor
    public DailyMeasurements() {
        this(null, -1, null, null, null, null, null, null, "", null);
    }

    /**
     * Constructor to init the {@link DailyMeasurements} class
     *
     * @param id           The identifier of the measurements
     * @param creationDate The date of the creation of the container
     * @param owner        The owner of the daily measurements
     */
    public DailyMeasurements(String id, long creationDate, GlukyUser owner) {
        this(id, creationDate, null, null, null, null, null, null, "", owner);
    }

    /**
     * Constructor to init the {@link DailyMeasurements} class
     *
     * @param id The identifier of the measurements
     * @param creationDate The date of the creation of the container
     * @param breakfast The breakfast measurement
     * @param morningSnack The morning snack measurement
     * @param lunch The lunch measurement
     * @param afternoonSnack The afternoon snack measurement
     * @param dinner The dinner measurement
     * @param basalInsulin The basal insulin measurement
     * @param dailyNotes The notes about the daily measurements
     * @param owner The owner of the daily measurements
     */
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

    /**
     * Method used to get {@link #creationDate} instance
     *
     * @return {@link #creationDate} instance as {@code long}
     */
    @JsonIgnore
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * Method used to get {@link #breakfast} instance
     *
     * @return {@link #breakfast} instance as {@link Meal}
     */
    public Meal getBreakfast() {
        return breakfast;
    }

    /**
     * Method used to get {@link #morningSnack} instance
     *
     * @return {@link #morningSnack} instance as {@link Meal}
     */
    @JsonGetter(MORNING_SNACK_KEY)
    public Meal getMorningSnack() {
        return morningSnack;
    }

    /**
     * Method used to get {@link #lunch} instance
     *
     * @return {@link #lunch} instance as {@link Meal}
     */
    public Meal getLunch() {
        return lunch;
    }

    /**
     * Method used to get {@link #afternoonSnack} instance
     *
     * @return {@link #afternoonSnack} instance as {@link Meal}
     */
    @JsonGetter(AFTERNOON_SNACK_KEY)
    public Meal getAfternoonSnack() {
        return afternoonSnack;
    }

    /**
     * Method used to get {@link #dinner} instance
     *
     * @return {@link #dinner} instance as {@link Meal}
     */
    public Meal getDinner() {
        return dinner;
    }

    /**
     * Method used to get {@link #basalInsulin} instance
     *
     * @return {@link #basalInsulin as {@link BasalInsulin}
     */
    @JsonGetter(BASAL_INSULIN_KEY)
    public BasalInsulin getBasalInsulin() {
        return basalInsulin;
    }

    /**
     * Method used to get {@link #dailyNotes} instance
     *
     * @return {@link #dailyNotes} instance as {@link String}
     */
    @JsonGetter(DAILY_NOTES_KEY)
    public String getDailyNotes() {
        return dailyNotes;
    }

    /**
     * Method used to locally attach the meals entities after their creation
     *
     * @param meals The meals entities to locally attach
     */
    @JsonIgnore
    @CustomParametersOrder(order = {BREAKFAST_KEY, MORNING_SNACK_KEY, LUNCH_KEY, AFTERNOON_SNACK_KEY, DINNER_KEY})
    public void attachMeals(ArrayList<Meal> meals) {
        breakfast = meals.get(0);
        morningSnack = meals.get(1);
        lunch = meals.get(2);
        afternoonSnack = meals.get(3);
        dinner = meals.get(4);
    }

    /**
     * Method used to locally attach the basal insulin entity after its creation
     *
     * @param basalInsulin The basal insulin entity to locally attach
     */
    @JsonIgnore
    public void attachBasalInsulin(BasalInsulin basalInsulin) {
        this.basalInsulin = basalInsulin;
    }

    /**
     * Method used to return the measurement specified by the {@code type}
     *
     * @param type The type used to return the related measurement
     *
     * @return the measurement specified by the {@code type} as {@link GlycemicMeasurementItem}
     */
    @Returner
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

    /**
     * Method used to check whether the daily measurements is currently filled. It is considered filled when at least
     * one measurement is filled
     *
     * @return whether the daily measurements is currently filled as {@code boolean}
     */
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