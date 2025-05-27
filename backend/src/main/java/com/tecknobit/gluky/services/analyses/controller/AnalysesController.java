package com.tecknobit.gluky.services.analyses.controller;

import com.tecknobit.gluky.services.analyses.service.AnalysesService;
import com.tecknobit.gluky.services.shared.controllers.DefaultGlukyController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysesController extends DefaultGlukyController {

    private final AnalysesService analysesService;

    @Autowired
    public AnalysesController(AnalysesService analysesService) {
        this.analysesService = analysesService;
    }

}
