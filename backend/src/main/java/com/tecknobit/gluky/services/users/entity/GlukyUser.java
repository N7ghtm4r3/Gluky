package com.tecknobit.gluky.services.users.entity;

import com.tecknobit.equinoxbackend.environment.services.users.entity.EquinoxUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;

@Entity
@Table(name = USERS_KEY)
public class GlukyUser extends EquinoxUser {

}
