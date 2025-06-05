package com.tecknobit.gluky.services.shared.controllers;

import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.gluky.services.users.repository.GlukyUsersRepository;
import com.tecknobit.gluky.services.users.service.GlukyUsersService;
import org.springframework.web.bind.annotation.RestController;

/**
 * The {@code DefaultGlukyController} class is useful to give the base behavior of the <b>Gluky's controllers</b>
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxController
 * @see GlukyUser
 * @see GlukyUsersRepository
 * @see GlukyUsersService
 */
@RestController
public class DefaultGlukyController extends EquinoxController<GlukyUser, GlukyUsersRepository, GlukyUsersService> {
}
