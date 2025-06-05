package com.tecknobit.gluky.services.users.repository;

import com.tecknobit.equinoxbackend.environment.services.users.entity.EquinoxUser;
import com.tecknobit.equinoxbackend.environment.services.users.repository.EquinoxUsersRepository;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The {@code GlukyUsersRepository} interface is useful to manage the queries for the users operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see EquinoxUsersRepository
 * @see GlukyUser
 * @see EquinoxUser
 */
@Repository
public interface GlukyUsersRepository extends EquinoxUsersRepository<GlukyUser> {
}
