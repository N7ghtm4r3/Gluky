package com.tecknobit.gluky.services.users.service;

import com.tecknobit.equinoxbackend.environment.services.users.service.EquinoxUsersService;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.gluky.services.users.repository.GlukyUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The {@code GlukyUsersService} class is useful to manage all the user database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see com.tecknobit.equinoxbackend.apis.resources.ResourcesManager
 * @see EquinoxUsersService
 */
@Service
public class GlukyUsersService extends EquinoxUsersService<GlukyUser, GlukyUsersRepository> {

    /**
     * Constructor to init the {@link GlukyUsersService} service
     *
     * @param usersRepository The instance for the users repository
     */
    @Autowired
    public GlukyUsersService(GlukyUsersRepository usersRepository) {
        super(usersRepository);
    }

}
