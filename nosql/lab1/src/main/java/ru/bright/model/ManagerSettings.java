package ru.bright.model;

import jakarta.validation.constraints.NotBlank;

public record ManagerSettings(String managerId, @NotBlank String language, @NotBlank String theme) {}
