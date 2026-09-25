package ru.bright.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bright.exception.ResourceNotFoundException;
import ru.bright.model.ManagerSettings;
import ru.bright.service.SettingsService;

@RestController
@RequestMapping("/api/managers")
public class SettingsController {
    private final SettingsService service;

    public SettingsController(SettingsService service) {
        this.service = service;
    }

    @PutMapping("/{managerId}/settings")
    public ManagerSettings saveSettings(@PathVariable String managerId,
                                        @Valid @RequestBody ManagerSettings settings) {
        ManagerSettings normalized = new ManagerSettings(
                managerId, settings.language(), settings.theme(), settings.pageSize());
        service.saveSettings(normalized);
        return normalized;
    }

    @GetMapping("/{managerId}/settings")
    public ManagerSettings getSettings(@PathVariable String managerId) {
        return service.getSettings(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Settings not found: " + managerId));
    }
}
