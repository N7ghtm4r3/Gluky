package com.tecknobit.gluky.services.users.repository;

import com.tecknobit.equinoxbackend.environment.services.users.repository.EquinoxUsersRepository;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import org.springframework.stereotype.Repository;

@Repository
public interface GlukyUsersRepository extends EquinoxUsersRepository<GlukyUser> {
}
