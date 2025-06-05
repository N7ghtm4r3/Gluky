package com.tecknobit.gluky.services.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.equinoxbackend.environment.services.users.entity.EquinoxUser;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.OWNER_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static java.util.Collections.EMPTY_LIST;

/**
 * The {@code GlukyUser} class is useful to represent a base Gluky's system user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see EquinoxUser
 */
@Entity
@Table(name = USERS_KEY)
public class GlukyUser extends EquinoxUser {

    /**
     * {@code measurements} the measurements owned by the user
     */
    @OneToMany(
            mappedBy = OWNER_KEY,
            cascade = CascadeType.ALL
    )
    private final List<DailyMeasurements> measurements;

    /**
     * Constructor to init the {@link GlukyUser} class
     *
     * @apiNote empty constructor required
     */
    @EmptyConstructor
    public GlukyUser() {
        this(null, null, null, null, null, null, null, null, EMPTY_LIST);
    }

    /**
     * Constructor to init the {@link EquinoxUser} class
     *
     * @param id                Identifier of the user
     * @param token             The token which the user is allowed to operate on server
     * @param name              The password of the user
     * @param surname           The surname of the user
     * @param email             The password of the user
     * @param password          The password of the user
     * @param profilePic        The profile pic of the user
     * @param language          The password of the user
     * @param dailyMeasurements The measurements owned by the user
     */
    public GlukyUser(String id, String token, String name, String surname, String email, String password,
                     String profilePic, String language, List<DailyMeasurements> dailyMeasurements) {
        super(id, token, name, surname, email, password, profilePic, language);
        this.measurements = dailyMeasurements;
    }

    /**
     * Method used to get {@link #measurements} instance
     *
     * @return {@link #measurements} instance as {@link String}
     */
    @JsonIgnore
    public List<DailyMeasurements> getMeasurements() {
        return measurements;
    }

}
