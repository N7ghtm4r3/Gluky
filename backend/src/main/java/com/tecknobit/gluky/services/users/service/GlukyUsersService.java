package com.tecknobit.gluky.services.users.service;

import com.tecknobit.equinoxbackend.environment.services.users.service.EquinoxUsersService;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.gluky.services.users.repository.GlukyUsersRepository;
import org.springframework.stereotype.Service;

@Service
public class GlukyUsersService extends EquinoxUsersService<GlukyUser, GlukyUsersRepository> {
}
