package com.tecknobit.gluky.services.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
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

@Entity
@Table(name = USERS_KEY)
public class GlukyUser extends EquinoxUser {

    @OneToMany(
            mappedBy = OWNER_KEY,
            cascade = CascadeType.ALL
    )
    private final List<DailyMeasurements> measurements;

    @EmptyConstructor
    public GlukyUser() {
        this(null, null, null, null, null, null, null, null, EMPTY_LIST);
    }

    public GlukyUser(String id, String token, String name, String surname, String email, String password,
                     String profilePic, String language, List<DailyMeasurements> dailyMeasurements) {
        super(id, token, name, surname, email, password, profilePic, language);
        this.measurements = dailyMeasurements;
    }

    @JsonIgnore
    public List<DailyMeasurements> getMeasurements() {
        return measurements;
    }

}
