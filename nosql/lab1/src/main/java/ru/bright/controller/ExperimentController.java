package ru.bright.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bright.dto.ConflictExperimentResponse;
import ru.bright.service.ConflictExperimentService;

@RestController
@RequestMapping("/api/experiments")
@Validated
public class ExperimentController {
    private final ConflictExperimentService service;

    public ExperimentController(ConflictExperimentService service) {
        this.service = service;
    }

    @PostMapping("/write-conflict")
    public ConflictExperimentResponse experiment(
            @RequestParam(defaultValue = "20") @Min(2) @Max(100) int writers) {
        return service.run(writers);
    }
}
