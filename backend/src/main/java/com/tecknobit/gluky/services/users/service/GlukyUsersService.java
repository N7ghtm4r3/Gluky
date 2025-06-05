package com.tecknobit.gluky.services.users.service;

import com.tecknobit.equinoxbackend.environment.services.users.service.EquinoxUsersService;
import com.tecknobit.equinoxbackend.resourcesutils.ResourcesManager;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.gluky.services.users.repository.GlukyUsersRepository;
import org.springframework.stereotype.Service;

/**
 * The {@code GlukyUsersService} class is useful to manage all the user database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ResourcesManager
 * @see EquinoxUsersService
 */
@Service
public class GlukyUsersService extends EquinoxUsersService<GlukyUser, GlukyUsersRepository> {

}
