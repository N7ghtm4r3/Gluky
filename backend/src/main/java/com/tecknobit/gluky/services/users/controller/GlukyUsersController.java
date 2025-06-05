package com.tecknobit.gluky.services.users.controller;

import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.equinoxbackend.environment.services.users.controller.EquinoxUsersController;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.gluky.services.users.repository.GlukyUsersRepository;
import com.tecknobit.gluky.services.users.service.GlukyUsersService;
import org.springframework.web.bind.annotation.RestController;

/**
 * The {@code GlukyUsersController} class is useful to manage all the user related requests
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxController
 * @see EquinoxUsersController
 */
@RestController
public class GlukyUsersController extends EquinoxUsersController<GlukyUser, GlukyUsersRepository, GlukyUsersService> {


}
