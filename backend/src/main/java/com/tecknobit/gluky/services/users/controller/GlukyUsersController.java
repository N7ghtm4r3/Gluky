package com.tecknobit.gluky.services.users.controller;

import com.tecknobit.equinoxbackend.environment.services.users.controller.EquinoxUsersController;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.gluky.services.users.repository.GlukyUsersRepository;
import com.tecknobit.gluky.services.users.service.GlukyUsersService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlukyUsersController extends EquinoxUsersController<GlukyUser, GlukyUsersRepository, GlukyUsersService> {


}
