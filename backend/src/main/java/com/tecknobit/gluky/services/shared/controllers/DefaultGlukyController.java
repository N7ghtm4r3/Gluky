package com.tecknobit.gluky.services.shared.controllers;

import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.gluky.services.users.repository.GlukyUsersRepository;
import com.tecknobit.gluky.services.users.service.GlukyUsersService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultGlukyController extends EquinoxController<GlukyUser, GlukyUsersRepository, GlukyUsersService> {

    public static final TimeFormatter dayFormatter = TimeFormatter.getInstance("dd-MM-yyyy");

}
